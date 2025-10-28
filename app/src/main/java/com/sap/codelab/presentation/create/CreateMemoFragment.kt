package com.sap.codelab.presentation.create

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sap.codelab.R
import com.sap.codelab.databinding.FragmentCreateMemoBinding
import com.sap.codelab.domain.usecases.AddGeofenceForMemoUseCase
import com.sap.codelab.domain.usecases.GetMemoByIdUseCase
import com.sap.codelab.presentation.base.BaseFragment
import com.sap.codelab.utils.extensions.collectInLifecycle
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateMemoFragment :
    BaseFragment<FragmentCreateMemoBinding>(FragmentCreateMemoBinding::inflate),
    OnMapReadyCallback {

    private val viewModel: CreateMemoViewModel by viewModel()
    private var googleMap: GoogleMap? = null
    private var currentMarker: Marker? = null

    private val addGeofenceUseCase: AddGeofenceForMemoUseCase by inject()
    private val getMemoByIdUseCase: GetMemoByIdUseCase by inject()

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
                    enableMyLocation()
                    if (!checkBackgroundLocationPermission()) {
                        requestBackgroundLocationPermission()
                    }
                }

                permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                    enableMyLocation()
                    if (!checkBackgroundLocationPermission()) {
                        requestBackgroundLocationPermission()
                    }
                }

                else -> {
                    Toast.makeText(
                        requireContext(),
                        R.string.location_permission_denied,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    private val requestNotificationsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(
                    requireContext(),
                    R.string.notifications_permission_denied,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private val requestBackgroundLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewLifecycleOwner.lifecycleScope.launch {
                    addPendingGeofence()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.background_location_permission_denied,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.onClearPendingMemo()
                requestPostNotificationsPermissionIfNeeded()
            }
        }

    override val toolbar: Toolbar
        get() = binding.toolbar

    override fun FragmentCreateMemoBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupMenu()
        observeViewModel()
        setupInputListeners()

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_container) as? SupportMapFragment
        mapFragment?.getMapAsync(this@CreateMemoFragment)

        contentCreateMemo.clearLocationButton.setOnClickListener {
            viewModel.setSelectedLocation(null)
            currentMarker?.remove()
            currentMarker = null
            it.isVisible = false
        }
    }

    private fun observeViewModel() {
        viewModel.validationState.collectInLifecycle(this) { state ->
            binding.contentCreateMemo.memoTitleContainer.error = getErrorMessage(
                hasError = state.hasTitleError,
                errorMessageResId = R.string.memo_title_empty_error
            )

            binding.contentCreateMemo.memoDescription.error = getErrorMessage(
                hasError = state.hasDescriptionError,
                errorMessageResId = R.string.memo_text_empty_error
            )
        }

        viewModel.events.collectInLifecycle(this) { event ->
            when (event) {
                is CreateMemoEvent.NavigateBackWithSuccess -> {
                    viewLifecycleOwner.lifecycleScope.launch {
                        addGeofenceAfterSave(event.memoId)
                        requestPostNotificationsPermissionIfNeeded()
                        setFragmentResult(REQUEST_KEY_MEMO_CREATED, bundleOf())
                        findNavController().popBackStack()
                    }
                }

                is CreateMemoEvent.ShowError -> {
                    Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.selectedLocation.collectInLifecycle(this) { location ->
            binding.contentCreateMemo.clearLocationButton.isVisible = location != null
        }
    }

    private fun setupInputListeners() {
        binding.contentCreateMemo.memoTitle.doAfterTextChanged { text ->
            viewModel.updateMemoInput(
                title = text.toString(),
                description = binding.contentCreateMemo.memoDescription.text.toString()
            )
        }
        binding.contentCreateMemo.memoDescription.doAfterTextChanged { text ->
            viewModel.updateMemoInput(
                title = binding.contentCreateMemo.memoTitle.text.toString(),
                description = text.toString()
            )
        }
    }

    private suspend fun addGeofenceAfterSave(memoId: Long) {
        val location = viewModel.selectedLocation.value
        if (location != null && memoId > 0) {
            getMemoByIdUseCase(memoId)
                .onSuccess { savedMemo ->
                    if (checkBackgroundLocationPermission()) {
                        addGeofenceUseCase(savedMemo)
                            .onFailure { error ->
                                Log.w(
                                    "CreateMemoFragment",
                                    "Failed to add geofence via UseCase",
                                    error
                                )
                            }
                    } else {
                        viewModel.setPendingMemoForGeofence(savedMemo)
                        requestBackgroundLocationPermission()
                    }
                }
                .onFailure { getError ->
                    Log.e(
                        "CreateMemoFragment",
                        "Failed to get saved memo $memoId for geofence",
                        getError
                    )
                    Toast.makeText(
                        requireContext(),
                        R.string.failed_to_get_data_for_geofence,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private suspend fun addPendingGeofence() {
        viewModel.getPendingMemo()?.let { memo ->
            addGeofenceUseCase(memo)
                .onFailure { error ->
                    Log.w(
                        "CreateMemoFragment",
                        "Failed to add PENDING geofence via UseCase",
                        error
                    )
                }
            viewModel.onClearPendingMemo()
            requestPostNotificationsPermissionIfNeeded()
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(
                    menu: Menu,
                    menuInflater: MenuInflater
                ) {
                    menuInflater.inflate(R.menu.menu_create_memo, menu)
                }

                override fun onMenuItemSelected(item: MenuItem): Boolean {
                    return when (item.itemId) {
                        R.id.action_save -> {
                            viewModel.onActionSaveClicked()
                            true
                        }

                        else -> false
                    }
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    private fun getErrorMessage(hasError: Boolean, @StringRes errorMessageResId: Int): String? {
        return if (hasError) {
            requireContext().getString(errorMessageResId)
        } else {
            null
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true

        googleMap?.setOnMapClickListener { latLng ->
            viewModel.setSelectedLocation(latLng)
            addOrMoveMarker(latLng)
        }
        checkAndRequestLocationPermissions()

        viewModel.selectedLocation.value?.let { addOrMoveMarker(it) }
    }

    private fun addOrMoveMarker(latLng: LatLng) {
        currentMarker?.remove()
        currentMarker =
            googleMap?.addMarker(
                MarkerOptions().position(latLng).title(getString(R.string.place_of_reminder))
            )
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        binding.contentCreateMemo.clearLocationButton.isVisible = true
    }

    private fun checkAndRequestLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                enableMyLocation()
                if (!checkBackgroundLocationPermission()) {
                    requestBackgroundLocationPermission()
                }
            }

            shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) -> {
                Toast.makeText(
                    requireContext(),
                    R.string.location_permission_rationale,
                    Toast.LENGTH_LONG
                ).show()
                requestLocationPermissionLauncher.launch(
                    arrayOf(
                        ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION
                    )
                )
            }

            else -> {
                requestLocationPermissionLauncher.launch(
                    arrayOf(
                        ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun checkBackgroundLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!checkBackgroundLocationPermission()) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    Toast.makeText(
                        requireContext(),
                        R.string.background_location_permission_rationale,
                        Toast.LENGTH_LONG
                    ).show()
                }
                requestBackgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true
            googleMap?.uiSettings?.isMyLocationButtonEnabled = true
            val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    if (currentMarker == null) {
                        googleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLatLng,
                                13f
                            )
                        )
                    }
                }
            }
        }
    }

    private fun hasPostNotificationsPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestPostNotificationsPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !hasPostNotificationsPermission()
        ) {
            requestNotificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    companion object {
        const val REQUEST_KEY_MEMO_CREATED = "memo_created_request"
    }
}
