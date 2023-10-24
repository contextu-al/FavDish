package com.tutorials.eu.favdish.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mapbox.maps.Style
import com.tutorials.eu.favdish.databinding.FragmentMapboxBinding

class MapboxFragment: Fragment() {

    private var mBinding: FragmentMapboxBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentMapboxBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapView = mBinding?.mapView
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)

    }

}