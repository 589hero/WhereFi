package com.example2.wherefi

data class PublicFreeWifi(
    var wifiId: Int,                        // wifiId(기본키)
    var placeName: String,                  // 설치장소명
    var detailedPlaceName: String,          // 설치장소상세
    var siDoName: String,                   // 설치시도명
    var siGunGuName: String,                // 설치시군구명
    var facilityType: String,               // 설치시설구분
    var serviceProviderName: String,        // 서비스제공사명
    var wifiSSID: String,                   // 와이파이SSID
    var installDate: String,                // 설치년월
    var roadNameAddress: String,            // 소재지도로명주소
    var landLotNumberAddress: String,       // 소재지지번주소
    var managementFacilityName: String,     // 관리기관명
    var managementFacilityNum: String,      // 관리기관전화번호
    var latitude: Double,                   // 위도
    var longitude: Double,                  // 경도
    var dataDate: String                    // 데이터기준일자
){
    // 디폴트 생성자 만드시 만들기!!
    constructor():this(
        0,
        "No Place",
        "No Detailed Place",
        "No Sido Name",
        "No Sigungu Name",
        "No Facility Type",
        "No Service Provider Name",
        "No Wifi SSID",
        "9999-99-99",
        "No RoadName Address",
        "LandLot Number Address",
        "No Management Facility Name",
        "No Management Facility Num",
        0.0,
        0.0,
        "9999-99-99")
}