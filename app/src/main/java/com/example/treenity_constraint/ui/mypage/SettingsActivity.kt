package com.example.treenity_constraint.ui.mypage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.treenity_constraint.R
import com.example.treenity_constraint.data.model.mypage.user.User
import com.example.treenity_constraint.di.NetWorkModule
import com.example.treenity_constraint.ui.store.ConfirmationActivity
import com.example.treenity_constraint.ui.store.StoreActivity
import retrofit2.Call
import retrofit2.Response

///////////////// 환경설정 페이지 /////////////////
class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

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
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        // 닉네임 변경되었을 때
        if(key == "signature") {
            val nextIntent = Intent(this@SettingsActivity, MyPageActivity::class.java)
            val newName = sharedPreferences?.getString(key,"no name") // user 가 작성한 String 값 가져와서
            // nextIntent.putExtra("name", newName.toString()) // MyPageActivity 에 값을 전달해주고(MyPageActivity 에서 scroll down 시 이름 새로고침 구현할 때 필요)

            // post
            val apiInterface = NetWorkModule.provideRetrofitInstance()
            val user = newName?.let { User(it, 0, 0, 0) } // 작성된 새로운 이름으로 객체 생성
            val call = user?.let { apiInterface.changeName(it) } // body 로 전달
            
            // test
            Log.d("tag", "onCreate: your new name is $newName")
            
            call?.enqueue(object : retrofit2.Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    Log.d("tag", "onResponse: " + response.code())
                    // Toast.makeText(applicationContext, "Please refresh My Page to see the changes",)
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.d("tag", "onFailure: " + t.message)
                }
            })

        }

        // push 알람 설정되었을 때
        if(key == "switch") {
            // TODO : switch 가 on 인지 off 인지 어떻게 판단할 건지 알아보기
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }


}