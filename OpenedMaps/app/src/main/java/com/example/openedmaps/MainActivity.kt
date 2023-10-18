package com.example.openedmaps

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.GpsStatus
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.telephony.SmsManager
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.example.openedmaps.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
//import com.mikirinkode.openstreetmapandroid.databinding.ActivityMainBinding
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : AppCompatActivity(), IMyLocationProvider, MapListener, GpsStatus.Listener {

    //variables
    private val LOCATION_REQUEST_CODE = 100
    private lateinit var mapView: MapView
    private lateinit var mapController: IMapController
    private lateinit var mMyLocationOverlay : MyLocationNewOverlay
    private lateinit var controller: IMapController
    private lateinit var buttonReturn : Button

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    val hotspotsLocations = listOf(
        Pair("Hluhluwe iMfolozi", GeoPoint(-28.219831,31.951865)),
        Pair("Umgeni River Bird Park", GeoPoint(-29.808167,31.017467)),
        Pair("Durban Japanese Gardens", GeoPoint(-29.7999,31.03758)),
        Pair("Kruger National Park", GeoPoint(-24.03207, 31.80073)),
        Pair("Isimangaliso Wetland Park", GeoPoint(-27.90557, 32.54488)),
        Pair("Cape Point Nature Reserve", GeoPoint(-34.35695, 18.48650)),
        Pair("Kirstenbosch National Botanical Garden", GeoPoint(-33.98868, 18.43270)),
        Pair("Addo Elephant National Park", GeoPoint(-33.48333, 25.75000)),
        Pair("De Hoop Nature Reserve", GeoPoint(-34.44194, 20.48694)),
        Pair("Blyde River Canyon", GeoPoint(-24.66040, 30.81530)),
        Pair("Golden Gate Highlands National Park", GeoPoint(-28.52860, 28.64989)),
        Pair("West Coast National Park", GeoPoint(-33.37182, 18.14995)),
        Pair("Robben Island", GeoPoint(-33.80707, 18.36115)),
        Pair("Wakkerstroom Wetlands", GeoPoint(-27.356072, 30.152353)),
        Pair("Mkuze Game Reserve", GeoPoint(-27.616379, 32.026165)),
        Pair("Pilanesberg National Park", GeoPoint(-25.275091, 27.129119)),
        Pair("Amakhala Game Reserve", GeoPoint(-33.444911, 26.047528)),
        Pair("KwaZulu-Natal Midlands", GeoPoint(-29.449250, 30.073041)),
        Pair("Berg River Estuary", GeoPoint(-33.642299, 18.145987)),
        Pair("Marievale Bird Sanctuary", GeoPoint(-26.387440, 28.476200)),
        Pair("Kgalagadi Transfrontier Park", GeoPoint(-26.940892, 20.542524)),
        Pair("De Mond Nature Reserve", GeoPoint(-34.721278, 20.743892)),
        Pair("Mossel Bay Bird Island", GeoPoint(-34.173559, 22.150459)),
        Pair("Makapan Valley", GeoPoint(-24.009628, 28.299672)),
        Pair("Amatikulu Nature Reserve", GeoPoint(-29.292797, 31.550295)),
        Pair("Betty's Bay Penguin Colony", GeoPoint(-34.351579, 18.917669)),
        Pair("Augrabies Falls National Park", GeoPoint(-28.590318, 20.322346)),
        Pair("Tembe Elephant Park", GeoPoint(-27.102655, 32.406992)),
        Pair("Giant's Castle Game Reserve", GeoPoint(-29.347222, 29.388611)),
        Pair("Witsand Nature Reserve", GeoPoint(-33.242326, 21.738798)),


        )

    private val hotspotMarkers = mutableListOf<Marker>() //initializr an empty list for hotspot markers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //nav back



        //init binding
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        )
        mapView = binding.mapView
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.mapCenter
        mapView.setMultiTouchControls(true)
        mapView.getLocalVisibleRect(Rect())

        mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this),mapView)
        controller = mapView.controller

        mMyLocationOverlay.enableMyLocation()
        mMyLocationOverlay.enableFollowLocation()
        mMyLocationOverlay.isDrawAccuracyEnabled = true

        //set the initial zoom level
        controller.setZoom(6.0)

        mapView.overlays.add(mMyLocationOverlay)
        setupMap()
        mapView.addMapListener(this)

        //check and request location permissions
        managePermissions()
        val resetMapButton = findViewById<Button>(R.id.resetMapButton)

        resetMapButton.setOnClickListener {
            // Call a function to reset the map to its initial state
            resetMapToInitialState()
        }

        //create a custom overlay for the animated marker
        val animatedMarkerOverlay = object : Overlay(this) {
            override fun onSingleTapConfirmed(e: MotionEvent?, mapView: MapView?): Boolean {
                //calculate the latitude and longitude from the geopoint
                val geoPoint = mMyLocationOverlay.myLocation
                latitude = geoPoint.latitude
                longitude = geoPoint.longitude

                //create a custom dialog or info window to display the latitude and longitude
                val dialog = Dialog(this@MainActivity)
                dialog.setContentView(R.layout.custom)

                val latitudeTextView = dialog.findViewById<TextView>(R.id.latitudeTextView)
                val longitudeTextView = dialog.findViewById<TextView>(R.id.longitudeTextView)

                latitudeTextView.text = "Latitude: $latitude"
                longitudeTextView.text = "Longitude: $longitude"

                dialog.show()

                return true
            }
        }
        //add the animatedMarkerOverlay to the map


        // addHotspotMarkers() removed as they are now being called on the button click
        // add the animatedmarkeroverlay to the map
        mapView.overlays.add(animatedMarkerOverlay)
        //get a reference to the "view hotspots" button
        val viewHotspotsButton = findViewById<Button>(R.id.viewHotspotsButton)
        val btnReturn = findViewById<Button>(R.id.btnReturn)
        //add on onclicklistener to the button
        viewHotspotsButton.setOnClickListener {
            //call a function to add hotspot markers when the button is clicked
            addHotspotMarkers()
        }

        btnReturn.setOnClickListener {
            val intent = Intent(this,Birds_Activity::class.java)
            startActivity(intent)
        }
      //  val showRoutesButton = findViewById<Button>(R.id.showRoutesButton)
       // showRoutesButton.setOnClickListener {
        //    calculateAndDisplayRoutes()
       // }

    }



    private fun setupMap(){
        Configuration.getInstance().load(this,
            PreferenceManager.getDefaultSharedPreferences(this))
        //mapView = binding.mapView
        mapController = mapView.controller
        mapView.setMultiTouchControls(true)

        //init the start point
        val startPoint = GeoPoint(-29.8587, 31.0218)
        mapController.setCenter(startPoint)
        mapController.setZoom(12.0)

        //create marker for the start point (ic_location)
        val icLocationMarker = Marker(mapView)
        icLocationMarker.position = startPoint
        icLocationMarker.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_location, null)

        //add a click listener to the ic_location marker
        icLocationMarker.setOnMarkerClickListener{marker , mapView ->
            val latitude = marker.position.latitude
            val longitude = marker.position.longitude

            val dialog = Dialog(this@MainActivity)
            dialog.setContentView(R.layout.custom)

            val latitudeTextView = dialog.findViewById<TextView>(R.id.latitudeTextView)
            val longitudeTextView = dialog.findViewById<TextView>(R.id.longitudeTextView)

            latitudeTextView.text = "Latitude: $latitude"
            longitudeTextView.text = "Longitude: $longitude"

            dialog.show()

            true //Return true to indicate that the event is consumed
        }

        // Add the ic_location marker to the mapView
        mapView.overlays.add(icLocationMarker)
    }

    private fun addHotspotMarkers() {
        // Clear any existing hotspot markers from the map
        mapView.overlays.removeAll(hotspotMarkers)

        for ((name, location) in hotspotsLocations) {
            val marker = Marker(mapView)
            marker.position = location
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_location, null)
            marker.title = name  // Set the title of the marker to the hotspot name

            // Add a click listener to the hotspot marker
            marker.setOnMarkerClickListener { _, _ ->
                // Create a dialog to ask the user if they want to see the route
                val dialogBuilder = AlertDialog.Builder(this@MainActivity)
                dialogBuilder.setTitle("Hotspot Details")
                dialogBuilder.setMessage("Hotspot: $name")
                dialogBuilder.setPositiveButton("Show Route") { _, _ ->
                    // Calculate and display the route when the user selects "Show Route"
                    calculateAndDisplayRouteToHotspot(location)
                }
                dialogBuilder.setNegativeButton("Cancel") { _, _ ->
                    // Do nothing when the user cancels
                }
                dialogBuilder.create().show()
                true // Return true to indicate that the event is consumed
            }

            hotspotMarkers.add(marker) // Add the marker to the list
        }

        // Add the new hotspot markers to the map
        mapView.overlays.addAll(hotspotMarkers)
        mapView.invalidate() // Refresh the map to display the new markers
    }



    override fun onScroll(event: ScrollEvent?): Boolean {
        //handle map scroll event here
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        //handle map zoom event here
        return false
    }

    override fun onGpsStatusChanged(p0: Int) {
        //handle GPS status changes here
    }

    override fun startLocationProvider(myLocationConsumer: IMyLocationConsumer?): Boolean {
        //start location provider here
        return true
    }

    override fun stopLocationProvider() {
        //stop location provider here
    }

    override fun getLastKnownLocation(): Location {
        // get last known location here
        return Location("last_known_location")
    }

    override fun destroy() {
        //destroy resources here
    }

    //handle permissions
    private fun isLocationPermissionGranted(): Boolean
    {
        val fineLocation = ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fineLocation && coarseLocation
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE)
        {
            if (grantResults.isNotEmpty())
            {
                for (result in grantResults)
                {
                    if (result == PackageManager.PERMISSION_GRANTED){
                        //handle permission granted
                        //you can re-initialize the map here if needed
                        //setupMap()
                    }
                    else{
                        Toast.makeText(this,"Location permissions denied",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun managePermissions()
    {
        val requestPermissions = mutableListOf<String>()

        //location permissions
        if (!isLocationPermissionGranted())
        {
            //if theses weren't granted
            requestPermissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        }



        if (requestPermissions.isNotEmpty())
        {
            ActivityCompat.requestPermissions(this,requestPermissions.toTypedArray(),LOCATION_REQUEST_CODE)
        }
    }
    private fun calculateAndDisplayRoutes() {
        val startPoint = mMyLocationOverlay.myLocation

        if (startPoint == null) {
            Toast.makeText(this, "Location Loading Error", Toast.LENGTH_SHORT).show()
            return
        }

        val visibleHotspotMarkers = hotspotMarkers.filter { mapView.boundingBox.contains(it.position) }

        for (marker in visibleHotspotMarkers) {
            GlobalScope.launch(Dispatchers.IO) {
                val roadManager = OSRMRoadManager(this@MainActivity, "OBP_Tuto/1.0")
                var road: Road? = null
                var retryCount = 0

                while (road == null && retryCount < 3) {
                    road = try {
                        roadManager.getRoad(arrayListOf(startPoint, marker.position))
                    } catch (e: Exception) {

                        null
                    }
                    retryCount++
                }

                withContext(Dispatchers.Main) {
                    if (road != null && road.mStatus == Road.STATUS_OK) {
                        val roadOverlay = RoadManager.buildRoadOverlay(road)
                        mapView.overlays.add(roadOverlay)

                        val routeDetails =
                            "Start Location: Your Current Location\nEnd Location: ${marker.title}\nDistance: ${road.mLength}"
                        showRouteDetailsDialog(routeDetails)

                        mapView.invalidate()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Error when loading road - status=${road?.mStatus ?: "unknown"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
    private fun showRouteDetailsDialog(routeDetails: String) {
        runOnUiThread {
            val alertDialog = AlertDialog.Builder(this@MainActivity)
            alertDialog.setTitle("Route Details")
            alertDialog.setMessage(routeDetails)
            alertDialog.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialog.create().show()
        }
    }





    private fun calculateAndDisplayRouteToHotspot(hotspotLocation: GeoPoint) {
        val startPoint = mMyLocationOverlay.myLocation

        if (startPoint == null) {
            Toast.makeText(this, "Location Loading Error", Toast.LENGTH_SHORT).show()
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            val roadManager = OSRMRoadManager(this@MainActivity, "OBP_Tuto/1.0")
            var road: Road? = null
            var retryCount = 0

            while (road == null && retryCount < 3) {
                road = try {
                    roadManager.getRoad(arrayListOf(startPoint, hotspotLocation))
                } catch (e: Exception) {
                    null
                }
                retryCount++
            }

            withContext(Dispatchers.Main) {
                if (road != null && road.mStatus == Road.STATUS_OK) {
                    val roadOverlay = RoadManager.buildRoadOverlay(road)
                    mapView.overlays.add(roadOverlay)

                    val routeDetails = "Start Location: Your Current Location\nEnd Location: Hotspot\nDistance: ${road.mLength}"
                    showRouteDetailsDialog(routeDetails)

                    mapView.invalidate()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Error when loading road - status=${road?.mStatus ?: "unknown"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    private fun resetMapToInitialState() {
        // Set the initial zoom level
        mapController.setZoom(6.0)

        // Set the map center to the initial point (change these coordinates as needed)
        val initialPoint = GeoPoint(-29.8587, 31.0218)
        mapController.setCenter(initialPoint)
        mapView.invalidate()

    }

}