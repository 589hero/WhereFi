package com.example2.wherefi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : Fragment() {
    lateinit var siDoList: Array<String>
    lateinit var siGunGuList: Array<String>

    // fragment통신을 위한 interface. MapFragment <-> MainActivity <-> SettingFragment
    interface OnWifiRangeChangedListener{
        fun OnWifiRangeChanged(type: Int, siDoName: String, siGunGuName: String, radius: Double)
    }
    var WifiRangeChangedListener: OnWifiRangeChangedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        siDoList = resources.getStringArray(R.array.SiDoName)
        siGunGuList = resources.getStringArray(R.array.Gyeonggi)

        initRadioBtn()
        initSiDoSpinner()
        initRadiusSpinner()
    }

    fun initRadioBtn(){
        when(MainActivity.type){
            MainActivity.TYPE_SIGUNGU -> {
                siGunGuRadioBtn.isChecked = true
                radiusRadioBtn.isChecked = false
                favoriteRadioBtn.isChecked = false

                siDoSpinner.isEnabled = true
                siGunGuSpinner.isEnabled = true
                radiusSpinner.isEnabled = false

                Toast.makeText(requireActivity(),
                    "선택된 위치 : ${MainActivity.siDoName} ${MainActivity.siGunGuName}",
                    Toast.LENGTH_SHORT).show()
            }

            MainActivity.TYPE_RADIUS -> {
                siGunGuRadioBtn.isChecked = false
                radiusRadioBtn.isChecked = true
                favoriteRadioBtn.isChecked = false

                siDoSpinner.isEnabled = false
                siGunGuSpinner.isEnabled = false
                radiusSpinner.isEnabled = true

                Toast.makeText(requireActivity(),
                    "선택된 반경 : ${MainActivity.radius.toInt()}m",
                    Toast.LENGTH_SHORT).show()
            }

            MainActivity.TYPE_FAVORITES -> {
                siGunGuRadioBtn.isChecked = false
                radiusRadioBtn.isChecked = false
                favoriteRadioBtn.isChecked = true

                radiusSpinner.isEnabled = false
                siDoSpinner.isEnabled = false
                siGunGuSpinner.isEnabled = false

                Toast.makeText(requireActivity(), "즐겨찾기된 와이파이 검색", Toast.LENGTH_LONG).show()
            }
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.siGunGuRadioBtn -> {
                    siDoSpinner.isEnabled = true
                    siGunGuSpinner.isEnabled = true
                    radiusSpinner.isEnabled = false

                    MainActivity.type = MainActivity.TYPE_SIGUNGU

                    if(MainActivity.type == MainActivity.TYPE_SIGUNGU){
                        Toast.makeText(requireActivity(),
                            "선택된 위치 : ${MainActivity.siDoName} ${MainActivity.siGunGuName}",
                            Toast.LENGTH_SHORT).show()
                    }

                    WifiRangeChangedListener?.OnWifiRangeChanged(
                        MainActivity.type,
                        MainActivity.siDoName,
                        MainActivity.siGunGuName,
                        MainActivity.radius)
                }

                R.id.radiusRadioBtn -> {
                    siDoSpinner.isEnabled = false
                    siGunGuSpinner.isEnabled = false
                    radiusSpinner.isEnabled = true

                    MainActivity.type = MainActivity.TYPE_RADIUS

                    if(MainActivity.type == MainActivity.TYPE_RADIUS){
                        Toast.makeText(requireActivity(),
                            "선택된 반경 : ${MainActivity.radius.toInt()}m",
                            Toast.LENGTH_SHORT).show()
                    }

                    WifiRangeChangedListener?.OnWifiRangeChanged(
                        MainActivity.type,
                        MainActivity.siDoName,
                        MainActivity.siGunGuName,
                        MainActivity.radius)
                }

                R.id.favoriteRadioBtn ->{
                    siDoSpinner.isEnabled = false
                    siGunGuSpinner.isEnabled = false
                    radiusSpinner.isEnabled = false

                    MainActivity.type = MainActivity.TYPE_FAVORITES

                    if(MainActivity.type == MainActivity.TYPE_FAVORITES){
                        Toast.makeText(requireActivity(), "즐겨찾기된 와이파이 검색", Toast.LENGTH_SHORT).show()
                    }

                    WifiRangeChangedListener?.OnWifiRangeChanged(
                        MainActivity.type,
                        MainActivity.siDoName,
                        MainActivity.siGunGuName,
                        MainActivity.radius)
                }
            }
        }
    }

    fun initSiDoSpinner(){
        // 시도명 spinner 초기화
        val siDoAdapter = ArrayAdapter<String>(requireActivity(),
            android.R.layout.simple_spinner_dropdown_item, siDoList)

        siDoSpinner.adapter = siDoAdapter
        // siDoSpinner의 default값으로 selectedSiDo를 줌.
        siDoSpinner.setSelection(siDoAdapter.getPosition(MainActivity.siDoName))

        siDoSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do Nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                MainActivity.siDoName = siDoSpinner.selectedItem.toString()

                var text = MainActivity.siDoName
                var params: ViewGroup.LayoutParams = siDoSpinner.layoutParams

                if(text.length == 3 || text.length == 4){
                    params.width = text.length * 55
                }
                else if(text.length == 5){
                    params.width = text.length * 50
                }
                else{
                    params.width = text.length * 47
                }
                siDoSpinner.layoutParams = params

                when(MainActivity.siDoName){
                    "강원도" -> {
                        siGunGuList = resources.getStringArray(R.array.Gangwon)
                    }

                    "경기도" -> {
                        siGunGuList = resources.getStringArray(R.array.Gyeonggi)
                    }

                    "경상남도" -> {
                        siGunGuList = resources.getStringArray(R.array.GyeongNam)
                    }

                    "경상북도" -> {
                        siGunGuList = resources.getStringArray(R.array.GyeongBuk)
                    }

                    "광주광역시" -> {
                        siGunGuList = resources.getStringArray(R.array.Gwangju)
                    }

                    "대구광역시" -> {
                        siGunGuList = resources.getStringArray(R.array.Daegu)
                    }

                    "대전광역시" -> {
                        siGunGuList = resources.getStringArray(R.array.Daejeon)
                    }

                    "부산광역시" -> {
                        siGunGuList = resources.getStringArray(R.array.Busan)
                    }

                    "서울특별시" -> {
                        siGunGuList = resources.getStringArray(R.array.Seoul)
                    }

                    "세종특별자치시" -> {
                        siGunGuList = resources.getStringArray(R.array.Sejong)
                    }

                    "울산광역시" -> {
                        siGunGuList = resources.getStringArray(R.array.Ulsan)
                    }

                    "인천광역시" -> {
                        siGunGuList = resources.getStringArray(R.array.Incheon)
                    }

                    "전라남도" -> {
                        siGunGuList = resources.getStringArray(R.array.JeonNam)
                    }

                    "전라북도" -> {
                        siGunGuList = resources.getStringArray(R.array.JeonBuk)
                    }

                    "제주특별자치도" -> {
                        siGunGuList = resources.getStringArray(R.array.Jeju)
                    }

                    "충청남도" -> {
                        siGunGuList = resources.getStringArray(R.array.ChungNam)
                    }

                    "충청북도" -> {
                        siGunGuList = resources.getStringArray(R.array.ChungBuk)
                    }
                }

                initSiGunGuSpinner()
            }
        }
    }

    fun initSiGunGuSpinner(){
        //시군구 spinner 초기화
        val siGunGuAdapter = ArrayAdapter<String>(requireActivity(),
            android.R.layout.simple_spinner_dropdown_item, siGunGuList)

        siGunGuSpinner.adapter = siGunGuAdapter

        if(MainActivity.siGunGuName in siGunGuList){
            // siGunGuSpinner의 default값으로 selectedSiGunGuName으로 함.
            siGunGuSpinner.setSelection(siGunGuAdapter.getPosition(MainActivity.siGunGuName))
        }
        else{
            siGunGuSpinner.setSelection(0)
        }

        siGunGuSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do Nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                MainActivity.siGunGuName = siGunGuSpinner.selectedItem.toString()

                var text = MainActivity.siGunGuName
                var params: ViewGroup.LayoutParams = siGunGuSpinner.layoutParams

                if(text.length == 2){
                    params.width = text.length * 65
                }
                else{
                    params.width = text.length * 55
                }
                siGunGuSpinner.layoutParams = params

                if(MainActivity.type == MainActivity.TYPE_SIGUNGU){
                    Toast.makeText(requireActivity(),
                        "선택된 위치 : ${MainActivity.siDoName} ${MainActivity.siGunGuName}",
                        Toast.LENGTH_SHORT).show()
                }

                WifiRangeChangedListener?.OnWifiRangeChanged(
                    MainActivity.type,
                    MainActivity.siDoName,
                    MainActivity.siGunGuName,
                    MainActivity.radius)
            }
        }
    }

    fun initRadiusSpinner(){
        // radius spinner 초기화
        val radiusList = resources.getStringArray(R.array.Radius)
        val radiusAdapter = ArrayAdapter<String>(requireActivity(),
            android.R.layout.simple_spinner_dropdown_item, radiusList)

        radiusSpinner.adapter = radiusAdapter

        // radiusSpinner의 default값으로 selectedRadius으로 함. ==> 이거 좀 고치기
        radiusSpinner.setSelection(radiusAdapter.getPosition(MainActivity.radius.toInt().toString() + "m"))

        radiusSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do Nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // "50m" ==> "50" ==> 50: Double
                MainActivity.radius = radiusSpinner.selectedItem.toString().dropLast(1).toDouble()

                if(MainActivity.type == MainActivity.TYPE_RADIUS){
                    Toast.makeText(requireActivity(),
                        "선택된 반경 : ${MainActivity.radius.toInt()}m",
                        Toast.LENGTH_SHORT).show()
                }

                WifiRangeChangedListener?.OnWifiRangeChanged(
                    MainActivity.type,
                    MainActivity.siDoName,
                    MainActivity.siGunGuName,
                    MainActivity.radius)
            }
        }
    }

}
