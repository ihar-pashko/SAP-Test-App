package com.sap.codelab.presentation.detail

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import com.sap.codelab.R
import com.sap.codelab.databinding.FragmentViewMemoBinding
import com.sap.codelab.presentation.base.BaseFragment
import com.sap.codelab.presentation.model.MemoUI
import com.sap.codelab.utils.extensions.collectInLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ViewMemoFragment :
    BaseFragment<FragmentViewMemoBinding>(FragmentViewMemoBinding::inflate),
    OnMapReadyCallback {

    private val viewModel: ViewMemoViewModel by viewModel()
    private val args: ViewMemoFragmentArgs by navArgs()
    private var googleMap: GoogleMap? = null

    private var memoTitleText: TextInputEditText? = null
    private var memoDescriptionText: TextInputEditText? = null
    private var mapFrame: FrameLayout? = null

    override val toolbar: Toolbar
        get() = binding.toolbar

    override fun FragmentViewMemoBinding.onViewCreated(view: View, savedInstanceState: Bundle?) {
        memoTitleText = view.findViewById(R.id.memo_title)
        memoDescriptionText = view.findViewById(R.id.memo_description)
        mapFrame = view.findViewById(R.id.map_frame)

        observeViewModel()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view_container) as? SupportMapFragment
        mapFragment?.getMapAsync(this@ViewMemoFragment) ?: run {
            mapFrame?.isVisible = false
        }

        if (savedInstanceState == null) {
            viewModel.setMemoId(args.memoId)
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true
        googleMap?.isMyLocationEnabled = true

        observeLocation()
    }

    private fun observeViewModel() {
        viewModel.memoUiModel.collectInLifecycle(this) { memoUi ->
            updateTextUI(memoUi)
        }

        viewModel.errorState.collectInLifecycle(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.onClearError()
            }
        }
    }

    private fun observeLocation() {
        if (!isAdded) return
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.locationLatLng.collect { latLng ->
                    updateMapUI(latLng)
                }
            }
        }
    }

    private fun updateTextUI(memoUi: MemoUI?) {
        memoTitleText?.setText(memoUi?.title ?: "")
        memoDescriptionText?.setText(memoUi?.description ?: "")
    }

    private fun updateMapUI(latLng: LatLng?) {
        if (latLng != null && googleMap != null) {
            mapFrame?.isVisible = true
            googleMap?.clear()
            googleMap?.addMarker(
                MarkerOptions().position(latLng).title(getString(R.string.reminder_location_marker_title))
            )
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        } else {
            mapFrame?.isVisible = false
        }
    }
}
