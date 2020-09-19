package com.example2.wherefi

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException

class MainActivity : AppCompatActivity() {
    val fragmentManager = supportFragmentManager
    val mapFragment = MapFragment()
    val favoriteFragment = FavoriteFragment()
    val settingFragment = SettingFragment()

    // sharedPreferences에 사용할 변수들
    private val KEY_TYPE = "type"
    private val KEY_SIDONAME = "lastSiDoName"
    private val KEY_SIGUNGUNAME = "lastSiGunGuName"
    private val KEY_RADIUS = "radius"
    private val KEY_FAVORITES = "favoriteWifiIds"
    private val KEY_LATITUDE = "lastLatitude"
    private val KEY_LONGITUDE = "lastLongitude"

    companion object{
        val TYPE_SIGUNGU = 1
        val TYPE_RADIUS = 2
        val TYPE_FAVORITES = 3

        var type = TYPE_SIGUNGU
        lateinit var siDoName: String
        lateinit var siGunGuName: String
        var radius: Double = 50.0
        var favoriteWifiIds = ArrayList<Int>()
//        lateinit var curLoc: LatLng
         var curLoc = LatLng(37.554752, 126.970631)

//        var latitude = 37.554752
//        var longitude = 126.970631
    }

    // 2번 뒤로가기 누르면 어플 종료할 때 사용됨
    private var lastBackPressTime: Long = 0
    private var isStart = true

    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

//        getStatesFromSharedPref()
//        init()
    }

    override fun onResume() {
        super.onResume()

        getStatesFromSharedPref()
    }

    override fun onPause() {
        super.onPause()

        // 여기서 즐겨찾기랑 초기 정보 SharedPreferences에 저장하기
        putStatesToSharedPref()
    }


    // 2번 뒤로가기 누르면 프로그램 종료되도록 하기.
    // 1번 누르면 Taost메세지로 "종료하려면 한 번 더 누르세요" 띄우기
    override fun onBackPressed() {
        // 뒤로가기 버튼 클릭
        if(System.currentTimeMillis() - lastBackPressTime >= 1500) {
            lastBackPressTime = System.currentTimeMillis()
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            //액티비티 종료
            finish()
        }
    }

    fun init(){
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, mapFragment, "mapFragment")
            .commit()

        // fragmentManager.beginTransaction().add(R.id.fragment_container, mapFragment).commit();

        fragmentManager.beginTransaction()
            .add(R.id.fragment_container, favoriteFragment, "favoriteFragment")
            .hide(favoriteFragment)
            .commit();
        fragmentManager.beginTransaction()
            .add(R.id.fragment_container, settingFragment, "settingFragment")
            .hide(settingFragment)
            .commit();

        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_map -> {
                    if(!mapFragment.isVisible){
                        changeToMapFragment()
                    }
                }

                R.id.nav_favorite -> {
                    if(!favoriteFragment.isVisible){
                        changeToFavoriteFragment()
                    }
                }

                R.id.nav_setting -> {
                    if(!settingFragment.isVisible){
                        changeToSettingFragment()
                    }
                }
            }

            true
        }

        settingFragment.WifiRangeChangedListener = object: SettingFragment.OnWifiRangeChangedListener{
            override fun OnWifiRangeChanged(
                type: Int,
                siDoName: String,
                siGunGuName: String,
                radius: Double) {

                when(type){
                    TYPE_SIGUNGU -> {
                        mapFragment.showMarkerUsingSiGunGu(siDoName, siGunGuName)
                    }

                    TYPE_RADIUS -> {
                        mapFragment.showMarkerUsingRadius(radius)
                    }

                    TYPE_FAVORITES -> {
                        mapFragment.showFavoriteMarkers()
                    }
                }
            }
        }
    }

    private fun putStatesToSharedPref(){
        val sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(KEY_TYPE, type) // default로 TYPE_SIGUNGU
        editor.putString(KEY_SIDONAME, siDoName) // default로 "서울특별시"
        editor.putString(KEY_SIGUNGUNAME, siGunGuName) // default로 "광진구"
        editor.putFloat(KEY_RADIUS, radius.toFloat()) // default로 50.0f
        editor.putFloat(KEY_LATITUDE, curLoc.latitude.toFloat())
        editor.putFloat(KEY_LONGITUDE, curLoc.longitude.toFloat())

        // 즐겨찾기 와이파이 ID array저장. ArrayList이므로 Json형태의 String으로 변환하여 이를 저장.
        var favoriteWifiIdJsonArray: JSONArray = JSONArray()

        for(favoriteWifiId in favoriteWifiIds){
            favoriteWifiIdJsonArray.put(favoriteWifiId)
        }

        if(!favoriteWifiIds.isEmpty()){
            editor.putString(KEY_FAVORITES, favoriteWifiIdJsonArray.toString())
        }
        else{
            editor.putString(KEY_FAVORITES, null)
        }

        editor.apply()
    }

    private fun getStatesFromSharedPref(){
        val sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)

        type = sharedPreferences.getInt(KEY_TYPE, TYPE_SIGUNGU)
        siDoName = sharedPreferences.getString(KEY_SIDONAME, "경기도")!!
        siGunGuName = sharedPreferences.getString(KEY_SIGUNGUNAME, "김포시")!!
        radius = sharedPreferences.getFloat(KEY_RADIUS, 200.0f).toDouble()
        curLoc = LatLng(sharedPreferences.getFloat(KEY_LATITUDE, 37.554752f).toDouble(),
            sharedPreferences.getFloat(KEY_LONGITUDE, 126.970631f).toDouble())

        // 다음은 Json형태의 String을 읽어서 favoriteWifiIds Array에 저장.
        favoriteWifiIds.clear()
        var favoriteWifiIdString = sharedPreferences.getString(KEY_FAVORITES, null)

        if(favoriteWifiIdString != null){
            var favoriteWifiIdJsonArray = JSONArray(favoriteWifiIdString)

                try{
                    for(i in 0 until favoriteWifiIdJsonArray.length()) {
                        favoriteWifiIds.add(favoriteWifiIdJsonArray.optInt(i))
                    }

                } catch(e: JSONException){
                    e.printStackTrace();
                }
        }

        if(isStart){
            isStart = false
            init()
        }

    }

    private fun changeToMapFragment(){
        if(!mapFragment.isAdded){
            fragmentManager.beginTransaction().add(R.id.fragment_container, mapFragment).commit();
        }

        if(mapFragment != null){
            fragmentManager.beginTransaction().show(mapFragment).commit();
        }

        if(favoriteFragment != null){
            fragmentManager.beginTransaction().hide(favoriteFragment).commit();
        }

        if(settingFragment != null){
            fragmentManager.beginTransaction().hide(settingFragment).commit();
        }

    }

    private fun changeToFavoriteFragment(){
        if(!favoriteFragment.isAdded){
            fragmentManager.beginTransaction().add(R.id.fragment_container, favoriteFragment).commit();
        }

        if(mapFragment != null){
            fragmentManager.beginTransaction().hide(mapFragment).commit();
        }

        if(favoriteFragment != null){
            fragmentManager.beginTransaction().show(favoriteFragment).commit();
        }

        if(settingFragment != null){
            fragmentManager.beginTransaction().hide(settingFragment).commit();
        }

    }

    private fun changeToSettingFragment(){
        if(!settingFragment.isAdded){
            fragmentManager.beginTransaction().add(R.id.fragment_container, settingFragment).commit();
        }

        if(mapFragment != null){
            fragmentManager.beginTransaction().hide(mapFragment).commit();
        }

        if(favoriteFragment != null){
            fragmentManager.beginTransaction().hide(favoriteFragment).commit();
        }

        if(settingFragment != null){
            fragmentManager.beginTransaction().show(settingFragment).commit();
        }
    }




}
