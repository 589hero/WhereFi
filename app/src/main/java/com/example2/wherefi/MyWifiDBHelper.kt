package com.example2.wherefi

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.android.gms.maps.model.LatLng

class MyWifiDBHelper(val context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION){

    // 보통 class내부에서 DB이름, 애트리뷰트 이름 등을 선언함.
    companion object{
        // DB정보
        val DB_VERSION = 1
        val DB_NAME = "publicFreeWifi.db"
        val TABLE_NAME = "wifi"

        // DB의 애트리뷰트
        val WIFI_ID = "wifiId"                                  // wifiId(기본키)
        val PLACE_NAME = "placeName"                            // 설치장소명
        val DETAILED_PLACE_NAME = "detailedPlaceName"           // 설치장소상세
        val SIDO_NAME = "siDoName"                              // 설치시도명
        val SIGUNGU_NAME = "siGunGuName"                        // 설치시군구명
        val FACILITY_TYPE = "facilityType"                      // 설치시설구분
        val SERVICE_PROVIDER_NAME = "serviceProviderName"       // 서비스제공사명
        val WIFI_SSID = "wifiSSID"                              // 와이파이SSID
        var INSTALL_DATE = "installDate"                        // 설치년월
        val ROADNAME_ADDRESS = "roadNameAddress"                // 소재지도로명주소
        val LANDLOT_NUMBER_ADDRESS = "landLotNumberAddress"     // 소재지지번주소
        val MANAGEMENT_FACILITY_NAME = "managementFacilityName" // 관리기관명
        val MANAGEMENT_FACILITY_NUM = "managementFacilityNum"   // 관리기관전화번호
        val LATITUDE = "latitude"                               // 위도
        val LONGITUDE = "longitude"                             // 경도
        val DATA_DATE = "dataDate"                              // 데이터기준일자
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val create_table = "create table if not exists " + TABLE_NAME + "(" +
                WIFI_ID + " integer primary key autoincrement, " +
                PLACE_NAME + " text, " +
                DETAILED_PLACE_NAME + " integer, " +
                SIDO_NAME + " text, " +
                SIGUNGU_NAME + " text, " +
                FACILITY_TYPE + " text, " +
                SERVICE_PROVIDER_NAME + " text, " +
                WIFI_SSID + " text, " +
                INSTALL_DATE + " text, " +
                ROADNAME_ADDRESS + " text, " +
                LANDLOT_NUMBER_ADDRESS + " text, " +
                MANAGEMENT_FACILITY_NAME + " text, " +
                MANAGEMENT_FACILITY_NUM + " text, " +
                LATITUDE + " real, " +
                LONGITUDE + " real, " +
                DATA_DATE + " text)"

        db?.execSQL(create_table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val drop_table = "drop table if exists " + TABLE_NAME
        db?.execSQL(drop_table)
        onCreate(db)
    }

    fun insertWifi(wifi: PublicFreeWifi): Boolean{
        val values = ContentValues()
        // ID값은 autoincrement이므로 insert할 때 자동으로 하나 씩 증가해줌. put안해줘도됨.
        values.put(PLACE_NAME, wifi.placeName)
        values.put(DETAILED_PLACE_NAME, wifi.detailedPlaceName)
        values.put(SIDO_NAME, wifi.siDoName)
        values.put(SIGUNGU_NAME, wifi.siGunGuName)
        values.put(FACILITY_TYPE, wifi.facilityType)
        values.put(SERVICE_PROVIDER_NAME, wifi.serviceProviderName)
        values.put(WIFI_SSID, wifi.wifiSSID)
        values.put(INSTALL_DATE, wifi.installDate)
        values.put(ROADNAME_ADDRESS, wifi.roadNameAddress)
        values.put(LANDLOT_NUMBER_ADDRESS, wifi.landLotNumberAddress)
        values.put(MANAGEMENT_FACILITY_NAME, wifi.managementFacilityName)
        values.put(MANAGEMENT_FACILITY_NUM, wifi.managementFacilityNum)
        values.put(LATITUDE, wifi.latitude)
        values.put(LONGITUDE, wifi.longitude)
        values.put(DATA_DATE, wifi.dataDate)

        val db = this.writableDatabase
        // insert에 실패했으면 -1, 성공했으면 투플의 ID값 반환
        if(db.insert(TABLE_NAME, null, values) > 0){
            db.close()
            return true
        }
        else{
            db.close()
            return false
        }
    }

    fun insertAllWifi(wifiList: ArrayList<PublicFreeWifi>): Boolean{
        val db = this.writableDatabase
        var isSuccess = true

        var count = 0
        for(wifi in wifiList){
            val values = ContentValues()
            // ID값은 autoincrement이므로 insert할 때 자동으로 하나 씩 증가해줌. put안해줘도됨.
            values.put(PLACE_NAME, wifi.placeName)
            values.put(DETAILED_PLACE_NAME, wifi.detailedPlaceName)
            values.put(SIDO_NAME, wifi.siDoName)
            values.put(SIGUNGU_NAME, wifi.siGunGuName)
            values.put(FACILITY_TYPE, wifi.facilityType)
            values.put(SERVICE_PROVIDER_NAME, wifi.serviceProviderName)
            values.put(WIFI_SSID, wifi.wifiSSID)
            values.put(INSTALL_DATE, wifi.installDate)
            values.put(ROADNAME_ADDRESS, wifi.roadNameAddress)
            values.put(LANDLOT_NUMBER_ADDRESS, wifi.landLotNumberAddress)
            values.put(MANAGEMENT_FACILITY_NAME, wifi.managementFacilityName)
            values.put(MANAGEMENT_FACILITY_NUM, wifi.managementFacilityNum)
            values.put(LATITUDE, wifi.latitude)
            values.put(LONGITUDE, wifi.longitude)
            values.put(DATA_DATE, wifi.dataDate)

            count++

            // insert에 실패했으면 -1, 성공했으면 투플의 ID값 반환
            if(db.insert(TABLE_NAME, null, values) < 0){
                db.close()
                isSuccess = false
                break
            }
        }

        return isSuccess
    }

    fun selectWifiUsingSiGunGu(siDoName: String, siGunGuName: String): ArrayList<PublicFreeWifi>{
        val strSql = "select * from " + TABLE_NAME +
                " where " + SIDO_NAME + " = \'" + siDoName + "\' and " +
                SIGUNGU_NAME + " = \'" + siGunGuName + "\'"

        val db = this.readableDatabase
        val cursor = db.rawQuery(strSql, null)

        var wifiList = ArrayList<PublicFreeWifi>()

        if(cursor.count != 0){
            cursor.moveToFirst()
            val attributeCount = cursor.columnCount // Attribute 개수
            val tupleCount = cursor.count // 투플 개수

            do{
                var wifi = PublicFreeWifi(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getString(10),
                    cursor.getString(11),
                    cursor.getString(12),
                    cursor.getDouble(13),
                    cursor.getDouble(14),
                    cursor.getString(15)
                )

                wifiList.add(wifi)
            }while(cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return wifiList
    }

    fun selectWifiUsingRadius(curLoc: LatLng, radius: Double): ArrayList<PublicFreeWifi>{

        // 경도 또는 위도에서의 0.00045도 차이 ==> 약 50m차이.
        var radiusToCoor = 0.00045 * ((radius / 50).toInt())
        var SquaredRadiusToCoor = Math.pow(radiusToCoor, 2.0)

        // sqlite에서 sqrt과 pow지원을 안 해서 사용하려면 따로 정의해야됨 ==> 그냥 sqrt이랑 pow없애서 계산
        val strSql = "select * from ${TABLE_NAME} " +
                     "where (${LATITUDE} - ${curLoc.latitude}) * (${LATITUDE} - ${curLoc.latitude}) + " +
                     "(${LONGITUDE} - ${curLoc.longitude}) * (${LONGITUDE} - ${curLoc.longitude}) <= " +
                     "${SquaredRadiusToCoor}"

        var wifiList = ArrayList<PublicFreeWifi>()

        val db = this.readableDatabase
        val cursor = db.rawQuery(strSql, null)

        if(cursor.count != 0){
            cursor.moveToFirst()
            val attributeCount = cursor.columnCount // Attribute 개수
            val tupleCount = cursor.count // 투플 개수

            do{
                var wifi = PublicFreeWifi(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getString(10),
                    cursor.getString(11),
                    cursor.getString(12),
                    cursor.getDouble(13),
                    cursor.getDouble(14),
                    cursor.getString(15)
                )

                wifiList.add(wifi)
            }while(cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return wifiList
    }

    fun selectWifiIdUsingID(): ArrayList<PublicFreeWifi>{
        var strSql = "select * from ${TABLE_NAME} " +
                     "where "

        if(MainActivity.favoriteWifiIds.size == 0){
            strSql += "${WIFI_ID} = -1"
        }
        else{
            for(i in 0 until MainActivity.favoriteWifiIds.size){
                if(i == 0){
                    strSql += "${WIFI_ID} = ${MainActivity.favoriteWifiIds[i]} "
                }
                else{
                    strSql += "or ${WIFI_ID} = ${MainActivity.favoriteWifiIds[i]} "
                }
            }
        }

        val db = this.readableDatabase
        val cursor = db.rawQuery(strSql, null)

        var wifiList = ArrayList<PublicFreeWifi>()

        if(cursor.count != 0){
            cursor.moveToFirst()
            val attributeCount = cursor.columnCount // Attribute 개수
            val tupleCount = cursor.count // 투플 개수

            do{
                var wifi = PublicFreeWifi(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getString(10),
                    cursor.getString(11),
                    cursor.getString(12),
                    cursor.getDouble(13),
                    cursor.getDouble(14),
                    cursor.getString(15)
                )

                wifiList.add(wifi)
            }while(cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return wifiList
    }




}