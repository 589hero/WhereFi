package com.example2.wherefi

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_main.*


class MapFragment : Fragment(), OnMapReadyCallback {
    lateinit var myWifiDBHelper: MyWifiDBHelper
    var googleMap: GoogleMap? = null
    lateinit var mapView: MapView

    var loc = LatLng(MainActivity.curLoc.latitude, MainActivity.curLoc.longitude)

    // 현재 위치 관련 변수들
    var isStart = true
    var fusedLocationClient: FusedLocationProviderClient? = null
    var locationCallback: LocationCallback? = null
    var locationRequest: LocationRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = rootView.findViewById(R.id.mapView) as MapView
        mapView.onCreate(savedInstanceState)
        MapsInitializer.initialize(activity)

        // mapView 초기화
        mapView.getMapAsync(this)
        getUserLocation()
        startLocationUpdate()

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()

        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()

        mapView.onStop()
    }

    override fun onResume() {
        super.onResume()

        mapView.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()

        stopLocationUpdate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    fun getUserLocation(){
        // 현재 위치를 loc에 저장.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient?.lastLocation?.addOnSuccessListener {
            MainActivity.curLoc = LatLng(it.latitude, it.longitude)
        }
    }

    fun startLocationUpdate(){
        locationRequest = LocationRequest.create()?.apply{
            interval = 60 * 1000
            fastestInterval = 30 * 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object: LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                for(location in locationResult.locations){
                    MainActivity.curLoc = LatLng(location.latitude, location.longitude)

                    if(isStart){
//                        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.curLoc, 16.0f))
                        isStart = false
                    }
                }
            }
        }

        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper() // MainThread의 message처리 looper를 사용
        )
    }

    fun stopLocationUpdate(){
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    fun initMap(){
        myWifiDBHelper = MyWifiDBHelper(activity)

        when(MainActivity.type){
            MainActivity.TYPE_SIGUNGU -> {
                showMarkerUsingSiGunGu(MainActivity.siDoName, MainActivity.siGunGuName)
            }

            MainActivity.TYPE_RADIUS -> {
                showMarkerUsingRadius(MainActivity.radius)
            }

            MainActivity.TYPE_FAVORITES -> {
                showFavoriteMarkers()
            }
        }

    }

    fun showMarkerUsingSiGunGu(siDoName: String, siGunGuName: String){
        googleMap?.clear()

        var wifiList = myWifiDBHelper.selectWifiUsingSiGunGu(siDoName, siGunGuName)

        var wifiNum = wifiList.size

        var latitude = 0.0
        var longitude = 0.0

        for(wifi in wifiList){
            // marker 띄우고,
            loc = LatLng(wifi.latitude, wifi.longitude)
            latitude += wifi.latitude / wifiNum
            longitude += wifi.longitude / wifiNum

            val placeName = wifi.placeName
            val detailedPlaceName = wifi.detailedPlaceName

            // loc위치에 marker 보여주기
            val markerOptions = MarkerOptions()
            markerOptions.position(loc)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            markerOptions.title(placeName)
            markerOptions.snippet(detailedPlaceName)
            var marker = googleMap?.addMarker(markerOptions)
            marker?.tag = wifi.wifiId
        }

        if(wifiNum == 0){
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.curLoc, 11.0f))
        }
        else{
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 11.0f))
        }
    }

    fun showMarkerUsingRadius(radius: Double){
        googleMap?.clear()

        var wifiList = myWifiDBHelper.selectWifiUsingRadius(MainActivity.curLoc, radius)

        for(wifi in wifiList){
            // marker 띄우고,
            loc = LatLng(wifi.latitude, wifi.longitude)

            val placeName = wifi.placeName
            // val detailedPlaceName = wifi.detailedPlaceName
            val roadNameAddress = wifi.roadNameAddress

            // loc위치에 marker 보여주기
            val markerOptions = MarkerOptions()
            markerOptions.position(loc)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title(placeName)
                .snippet(roadNameAddress)

            var marker = googleMap?.addMarker(markerOptions)
            marker?.tag = wifi.wifiId
        }

        val circleOptions = CircleOptions()
            .center(MainActivity.curLoc)
            .radius(radius)
            .strokeColor(Color.TRANSPARENT)
            .fillColor(0x220000FF)
        googleMap?.addCircle(circleOptions)

        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.curLoc, 16.0f))

    }

    fun showFavoriteMarkers(){
        googleMap?.clear()

        var wifiList = myWifiDBHelper.selectWifiIdUsingID()

        var wifiNum = wifiList.size

        var latitude = 0.0
        var longitude = 0.0

        for(wifi in wifiList){
            // marker 띄우고,
            loc = LatLng(wifi.latitude, wifi.longitude)
            latitude += wifi.latitude / wifiNum
            longitude += wifi.longitude / wifiNum

            val placeName = wifi.placeName
            val detailedPlaceName = wifi.detailedPlaceName

            // loc위치에 marker 보여주기
            val markerOptions = MarkerOptions()
            markerOptions.position(loc)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            markerOptions.title(placeName)
            markerOptions.snippet(detailedPlaceName)
            var marker = googleMap?.addMarker(markerOptions)
            marker?.tag = wifi.wifiId
        }

        if(wifiNum == 0){
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.curLoc, 7.0f))
        }
        else{
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 7.0f))
        }

    }

    override fun onMapReady(p0: GoogleMap?) {
        Log.i(TAG , "onMapReady")

        googleMap = p0!!
        // loc위치의 google map이동.
        googleMap?.isMyLocationEnabled = true

        googleMap?.setOnMyLocationButtonClickListener {
            Log.i(TAG, "위도 : ${MainActivity.curLoc.latitude}, 경도 : ${MainActivity.curLoc.longitude}")
            false
        }


        googleMap?.setOnInfoWindowLongClickListener {
            val dlgBuilder = AlertDialog.Builder(context)
            dlgBuilder.setTitle("즐겨찾기 추가")
                .setMessage("즐겨찾기에 추가하겠습니까?")
                .setPositiveButton("예"){
                    _, _ ->
                    if(it.tag.toString().toInt() in MainActivity.favoriteWifiIds){
                        Toast.makeText(requireActivity(), "이미 즐겨찾기에 추가되어 있습니다.", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        MainActivity.favoriteWifiIds.add(it.tag.toString().toInt())

                        val favoriteFragment = requireActivity()
                            .supportFragmentManager
                            .findFragmentByTag("favoriteFragment") as FavoriteFragment

                        favoriteFragment.addView()

                        Toast.makeText(requireActivity(), "즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    }

                }
                .setNegativeButton("아니오"){
                    _, _ ->
                }
                .show()
        }

        googleMap?.setInfoWindowAdapter(object: GoogleMap.InfoWindowAdapter{
            override fun getInfoContents(marker: Marker?): View? {
                return null
            }

            override fun getInfoWindow(marker: Marker?): View {
                var view = layoutInflater.inflate(R.layout.custom_info_window, fragment_container, false)

                var wifiIdText: TextView = view.findViewById(R.id.wifiIdText)
                var placeNameText: TextView = view.findViewById(R.id.placeNameText)
                var roadNameAddressText: TextView = view.findViewById(R.id.roadNameAddressText)

                wifiIdText.text = marker?.tag.toString()
                placeNameText.text = marker?.title
                roadNameAddressText.text = marker?.snippet

                return view
            }
        })

        initMap()
    }

//    fun initDB(){
//        // xml parsing해서 wifiList에 저장하기.
//        var wifiList = ArrayList<PublicFreeWifi>()
//        xmlParser(wifiList)
//
//        myWifiDBHelper = MyWifiDBHelper(activity)
//        val result = myWifiDBHelper.insertAllWifi(wifiList)
//
//
//        if(result){
//            Toast.makeText(activity, "DB INSERT SUCCESS", Toast.LENGTH_SHORT).show()
//        }
//        else{
//            Toast.makeText(activity, "DB INSERT FAILED", Toast.LENGTH_SHORT).show()
//        }
//
//        // wifiList.clear()
//    }
//
//
//    fun xmlParser(wifiList: ArrayList<PublicFreeWifi>){
//        val xmlStr = readFile()
//
//        val doc = Jsoup.parse(xmlStr)
//        val rows = doc.select("Row")
//
//
//        var wifiId = 0
//        for(row in rows){
//
//            var latitudeStr = row.select("위도").text()
//            var latitude: Double
//            if(latitudeStr.isBlank()){
//                latitude = 0.0
//            }
//            else{
//                latitude = latitudeStr.toDouble()
//            }
//
//            var longtitudeStr = row.select("경도").text()
//            var longtitude: Double
//            if(longtitudeStr.isBlank()){
//                longtitude = 0.0
//            }
//            else{
//                longtitude = longtitudeStr.toDouble()
//            }
//            Log.i(TAG, "wifiId : ${wifiId}")
//            wifiList.add(PublicFreeWifi(
//                wifiId++,
//                row.select("설치장소명").text(),
//                row.select("설치장소상세").text(),
//                row.select("설치시도명").text(),
//                row.select("설치시군구명").text(),
//                row.select("설치시설구분").text(),
//                row.select("서비스제공사명").text(),
//                row.select("와이파이SSID").text(),
//                row.select("설치년월").text(),
//                row.select("소재지도로명주소").text(),
//                row.select("소재지지번주소").text(),
//                row.select("관리기관명").text(),
//                row.select("관리기관전화번호").text(),
//                latitude,
//                longtitude,
//                row.select("데이터기준일자").text()
//            ))
//
//        }
//    }
//
//    fun readFile(): String{
//        // val scan = Scanner(resources.openRawResource(R.raw.public_free_wifi))
//        val inputStream = resources.openRawResource(R.raw.public_free_wifi)
//        var inputString = inputStream.bufferedReader().use{it.readText()}
//
//        return inputString
//    }

}
