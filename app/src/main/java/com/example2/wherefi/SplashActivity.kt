package com.example2.wherefi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.app.ActivityCompat
import java.io.FileOutputStream

class SplashActivity : AppCompatActivity() {

    val SPLASH_VIEW_TIME: Long = 700 // 스플래시 화면을 보여주는 시간 (ms)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initPermission()
        initDB()
    }

    fun initDB(){
        // db파일 복사해서 넣으면 추가하기
        val dbFile = this.getDatabasePath("publicFreeWifi.db")
        if(!dbFile.parentFile.exists()){
            dbFile.parentFile.mkdir()
        }

        if(!dbFile.exists()){
            // raw에 있는 db파일을 읽어서 dbFile에 저장.
            val file = resources.openRawResource(R.raw.public_free_wifi)
            val fileSize = file.available()

            val buffer = ByteArray(fileSize)
            file.read(buffer)
            file.close()

            dbFile.createNewFile()
            val output = FileOutputStream(dbFile)
            output.write(buffer)
            output.close()
        }
    }

    fun initPermission(){
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            Handler().postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, SPLASH_VIEW_TIME)
        }
        else{
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            100 -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED){

                    Handler().postDelayed({
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }, SPLASH_VIEW_TIME)
                }
                else{
                    // 위치정보 제공 허용을 안하면 종료하기
                    finish()
                }
            }
        }
    }




}
