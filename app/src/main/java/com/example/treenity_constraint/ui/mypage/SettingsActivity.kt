package com.example.treenity_constraint.ui.mypage

import android.Manifest
import android.Manifest.permission.ACTIVITY_RECOGNITION
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.treenity_constraint.R
import com.example.treenity_constraint.StepDetectorService
import com.example.treenity_constraint.data.model.mypage.user.User
import com.example.treenity_constraint.di.NetWorkModule
import retrofit2.Call
import retrofit2.Response
import kotlin.system.exitProcess

///////////////// 환경설정 페이지 /////////////////
class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener{

    // sensor permission
    private val activityPermission = 100
    @RequiresApi(Build.VERSION_CODES.Q) // api level 29 부터 신체 활동 센서가 달려있음
    val permission = arrayOf(ACTIVITY_RECOGNITION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mypage_settings_activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

            PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this)

    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    // 이벤트 작성 nickname 변경될 때, push 알람 설정 true 되었을 때 서버로 POST 요청보낼 것
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

        // 닉네임 변경되었을 때
        if(key == "signature") {
            val newName = sharedPreferences?.getString(key,"no name") // user 가 작성한 String 값 가져와서

            // post
            val apiInterface = NetWorkModule.provideRetrofitInstance()
            val user = newName?.let { User(it, 0, 0, 0) } // 작성된 새로운 이름으로 객체 생성
            val call = user?.let { apiInterface.changeName(it) } // body 로 전달
            
            // test
            Log.d("tag", "onCreate: your new name is $newName")
            
            call?.enqueue(object : retrofit2.Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    Log.d("tag", "onResponse: " + response.code())
                    Toast.makeText(this@SettingsActivity, "Please refresh My Page to see the changes", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.d("tag", "onFailure: " + t.message)
                }
            })

        }

        // push 알람 설정되었을 때
        if(key == "switch") {
            // TODO : 푸쉬 알람 띄우기
            if(sharedPreferences?.getBoolean(key, false) == true) {
                
            }
        }

        // permission 체크박스 눌렀을 때
        if(key == "permission") {
            val builder = AlertDialog.Builder(this)

            builder.setIcon(R.drawable.mypage_setting_icon)    // 제목 아이콘
            builder.setTitle("Goto Setting")    // 제목
            builder.setView(layoutInflater.inflate(R.layout.mypage_goto_application_settings, null))

            builder.setPositiveButton("Go to Permission Settings") { dialog, which ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }

            if(sharedPreferences?.getBoolean(key, false) == true) {
//                // 이전에 승인 거절한 경우이기 때문에 shouldShowRequestPermissionRationale
//                if(ContextCompat.checkSelfPermission(this, ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) { // permission check
////                    if (ActivityCompat.shouldShowRequestPermissionRationale( this, ACTIVITY_RECOGNITION)) {
//                        ActivityCompat.requestPermissions(this, permission, activityPermission) // 거부할 경우, 재요청에도 권한 요청 창이 뜨지 않기에 dialog 창을 띄워 설정하러가기 페이지로 이동시킬 예정
////                    }
//                }
//
//                // 이미 승인이 되어있는 상태였다면
//                if(ContextCompat.checkSelfPermission(this, ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Activity Sensor is already Activated", Toast.LENGTH_SHORT).show()
//                }
    
                // 권한이 거절된 상태 
                if (ContextCompat.checkSelfPermission(this, ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                    
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACTIVITY_RECOGNITION)) { 
                        // 1. 사용자가 승인 거절을 모두 한 적이 없는 경우, 권한 요청
                        ActivityCompat.requestPermissions(this, permission, activityPermission)

                     } else {
                        // 2. 사용자가 거절을 누른 경우, 요청을 다시 해봤자 다이얼로그가 안뜨기에, 설정 창으로 갈 수 있는 다이얼로그를 띄움
                        builder.show()
                     } 
                } else { 
                    // 3. 권한이 승인된 상태, 승인 되었다고 Toast 메시지 띄우기
                    Toast.makeText(this, "Activity Sensor is Activated", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
        


    

    // ACTIVITY_RECOGNITION 권한 수락/거부 시
    override fun onRequestPermissionsResult( requestCode: Int, permissions: Array<out String>, grantResults: IntArray ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == activityPermission) {
            if (ContextCompat.checkSelfPermission(this, ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Activity Permission Confirmed", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, StepDetectorService::class.java)
                startService(intent)
                Toast.makeText(this, "Activity Permission Activated", Toast.LENGTH_SHORT).show()
            } else { // 거부하기를 눌렀다면
                Toast.makeText(this, "Activity Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }





    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }


}