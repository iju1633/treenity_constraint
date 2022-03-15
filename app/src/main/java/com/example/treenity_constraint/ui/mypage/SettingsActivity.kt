package com.example.treenity_constraint.ui.mypage

import android.Manifest.permission.ACTIVITY_RECOGNITION
import android.annotation.SuppressLint
import android.app.PendingIntent
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
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.example.treenity_constraint.R
import com.example.treenity_constraint.StepDetectorService
import com.example.treenity_constraint.TreenityApplication
import com.example.treenity_constraint.data.model.mypage.user.User
import com.example.treenity_constraint.di.NetWorkModule
import com.example.treenity_constraint.utils.MyWorker
import retrofit2.Call
import retrofit2.Response
import java.util.*


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

    override fun onStart() { // 따로 앱 설정에 가서 권한 승인을 준 경우 -> scroll down 해서 확인할 필요 없이 바로 센서가 작동한다는 메시지 띄움
        super.onStart()

        if (ContextCompat.checkSelfPermission(this, ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Activity Sensor is Activated", Toast.LENGTH_SHORT).show()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    // 이벤트 작성 nickname 변경될 때, push 알람 설정 true 되었을 때 서버로 POST 요청보낼 것
    @SuppressLint("InflateParams")
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
            // TODO : 푸쉬 알람 switch off 하면 푸쉬 알람 설정 없앰
            if(sharedPreferences?.getBoolean(key, false) == false) {

                val workId = TreenityApplication.myRequest.build().id
                WorkManager.getInstance(this).cancelWorkById(workId)

                Toast.makeText(this@SettingsActivity, "Push Alarm OFF", Toast.LENGTH_SHORT).show()
            } else {

                WorkManager.getInstance(this)
                    .enqueueUniquePeriodicWork(
                        "my_id",
                        ExistingPeriodicWorkPolicy.KEEP,
                        TreenityApplication.myRequest.build()
                    )
                Toast.makeText(this@SettingsActivity, "Push Alarm ON", Toast.LENGTH_SHORT).show()
            }
        }

        // permission 체크박스 눌렀을 때
        if(key == "permission") {
            
            // 다이얼로그
            val builder = AlertDialog.Builder(this)

            builder.setIcon(R.drawable.mypage_setting_icon)    // 제목 아이콘
            builder.setTitle("AUTHORIZATION")    // 제목
            builder.setView(layoutInflater.inflate(R.layout.mypage_goto_application_settings, null)) // null 때문에 @SuppressLint("InflateParams") 붙임(IDE 추천)

            builder.setPositiveButton("GO To APP SETTINGS") { dialog, which ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }

            if(sharedPreferences?.getBoolean(key, false) == true) {
    
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