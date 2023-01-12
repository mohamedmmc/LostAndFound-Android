package com.amier.Activities
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.amier.modernloginregister.R
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider

import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode


class mapbos : AppCompatActivity(), PermissionsListener, LocationEngineListener, MapboxMap.OnMapClickListener{
    private lateinit var mapView : MapView
    private lateinit var map : MapboxMap
    private lateinit var permissionManager : PermissionsManager
    private lateinit var originLocation : Location
    var positionGps : MutableList<Double>? = ArrayList()
    lateinit var mSharedPref: SharedPreferences

    private var locationEngine : LocationEngine?=null
    private var locationLayerPlugin : LocationLayerPlugin? = null
    private var originPosition : Point? = null
    private var destinationPosition : Point? = null
    private var destinationMarker : Marker? = null
    private var startButton : Button? = null
    var type:String? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapbos)
        mSharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        type =intent.getStringExtra("type")
        Mapbox.getInstance(applicationContext,getString(R.string.access_token))
        mapView = findViewById(R.id.mapview)
        startButton = findViewById(R.id.button)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync{
            map = it
            map.addOnMapClickListener (this)
            enableLocation()
        }

        startButton?.setOnClickListener {
            if(positionGps!!.isNotEmpty()){
                mSharedPref.edit().apply{
                    intent.putExtra("lat",positionGps!![0].toString())
                    intent.putExtra("long",positionGps!![1].toString())
                    intent.putExtra("type",type)
                    putString("lat", positionGps!![0].toString())
                    putString("long", positionGps!![1].toString())
                }.apply()
            }
            println("position envoyé  est : "+mSharedPref.getString("lat",""))
            println("position envoyé  est : "+mSharedPref.getString("long",""))
            finish()
        }
    }
    @SuppressWarnings("MissingPermission")
    override fun onStart() {
        super.onStart()
        if (PermissionsManager.areLocationPermissionsGranted (  this)) {
            locationEngine?.requestLocationUpdates ()
            locationLayerPlugin?.onStart()
        }
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        locationEngine?.removeLocationUpdates()
        locationLayerPlugin?.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationEngine?.deactivate()
        mapView.onDestroy()
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        TODO("Not yet implemented")
    }

    override fun onPermissionResult(granted: Boolean) {
        if(granted){
            enableLocation()
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun initializeLocationEngine(){
        locationEngine = LocationEngineProvider(this).obtainBestLocationEngineAvailable()
        locationEngine?.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine?.activate()

        val lastLocation = locationEngine?.lastLocation
        if(lastLocation !=null){
            originLocation = lastLocation
            setCameraPosition(lastLocation)
        }else{
            locationEngine?.addLocationEngineListener(this)
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun initializeLocationLayer() {
        locationLayerPlugin = LocationLayerPlugin(mapView,map,locationEngine)
        locationLayerPlugin?.setLocationLayerEnabled(true)
        locationLayerPlugin?.cameraMode = CameraMode.TRACKING
        locationLayerPlugin?.renderMode = RenderMode.NORMAL

    }

    private fun setCameraPosition (location: Location) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
            LatLng(location.latitude, location.longitude),  13.0))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }

    @SuppressWarnings("MissingPermission")
    override fun onConnected() {
        locationEngine?.requestLocationUpdates()
    }

    override fun onLocationChanged(location: Location?) {
        location?.let {
            originLocation = location
            setCameraPosition(location)
        }
    }

    fun enableLocation(){
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            initializeLocationEngine()
            initializeLocationLayer()
        }else{
            permissionManager = PermissionsManager(this)
            permissionManager.requestLocationPermissions(this)
        }
    }

    override fun onMapClick(point: LatLng) {
        map.clear()
        positionGps!!.clear()
        destinationMarker = map.addMarker(MarkerOptions().position(point))
        positionGps!!.add(point.latitude)
        positionGps!!.add(point.longitude)
        println("position selectionn est : "+point.toString())
    }

}