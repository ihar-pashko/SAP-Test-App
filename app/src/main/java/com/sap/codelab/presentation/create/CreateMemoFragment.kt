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
import androidx.lifecycle.repeatOnLifecycle
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
import com.sap.codelab.presentation.base.BaseFragment
import com.sap.codelab.presentation.notifications.GeofenceHelper
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateMemoFragment :
    BaseFragment<FragmentCreateMemoBinding>(FragmentCreateMemoBinding::inflate),
    OnMapReadyCallback {

    private val viewModel: CreateMemoViewModel by viewModel()
    private var googleMap: GoogleMap? = null
    private var currentMarker: Marker? = null
    private lateinit var geofenceHelper: GeofenceHelper

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

    private val requestBackgroundLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                tryRegisterGeofenceAfterPermissionGrant()
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.background_location_permission_denied,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override val toolbar: Toolbar
        get() = binding.toolbar

    override fun FragmentCreateMemoBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        geofenceHelper = GeofenceHelper(requireContext())
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.validationState.collect { state ->
                        binding.contentCreateMemo.memoTitleContainer.error =
                            getErrorMessage(state.hasTitleError, R.string.memo_title_empty_error)
                        binding.contentCreateMemo.memoDescription.error =
                            getErrorMessage(
                                state.hasDescriptionError,
                                R.string.memo_text_empty_error
                            )
                    }
                }

                launch {
                    viewModel.events.collect { event ->
                        when (event) {
                            is CreateMemoEvent.NavigateBackWithSuccess -> {
                                tryRegisterGeofence(event.memoId)
                                setFragmentResult(REQUEST_KEY_MEMO_CREATED, bundleOf())
                                findNavController().popBackStack()
                            }

                            is CreateMemoEvent.ShowError -> {
                                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }

                launch {
                    viewModel.selectedLocation.collect { location ->
                        binding.contentCreateMemo.clearLocationButton.isVisible = location != null
                    }
                }
            }
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

    private fun tryRegisterGeofence(memoId: Long) {
        val location = viewModel.selectedLocation.value
        if (location != null && memoId > 0) {
            if (checkBackgroundLocationPermission()) {
                registerGeofence(memoId, location)
            } else {
                pendingGeofenceMemoId = memoId
                requestBackgroundLocationPermission()
            }
        } else if (memoId <= 0) {
            Log.w("CreateMemoFragment", "Invalid memoId ($memoId), cannot register geofence.")
        }
    }

    private fun registerGeofence(memoId: Long, location: LatLng) {
        Log.d("CreateMemoFragment", "Registering geofence for memo $memoId")
        geofenceHelper.addGeofence(
            id = memoId.toString(),
            latitude = location.latitude,
            longitude = location.longitude,
            radius = 200f
        )
        pendingGeofenceMemoId = null
    }

    private var pendingGeofenceMemoId: Long? = null

    private fun tryRegisterGeofenceAfterPermissionGrant() {
        pendingGeofenceMemoId?.let { memoId ->
            val location = viewModel.selectedLocation.value
            if (location != null) {
                registerGeofence(memoId, location)
            }
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
            googleMap?.addMarker(MarkerOptions().position(latLng).title("Place of reminder"))
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

    companion object {
        const val REQUEST_KEY_MEMO_CREATED = "memo_created_request"
    }
}
