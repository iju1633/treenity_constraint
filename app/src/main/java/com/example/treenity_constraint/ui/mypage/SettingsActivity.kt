package com.example.treenity_constraint.ui.mypage

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.treenity_constraint.R
import com.example.treenity_constraint.data.model.mypage.user.User
import com.example.treenity_constraint.di.NetWorkModule
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
            val prefs = sharedPreferences?.getString(key, "garfield") // user 가 작성한 String 값 가져와서

            // post
            val apiInterface = NetWorkModule.provideRetrofitInstance()
            val call = prefs?.let { apiInterface.changeName(1, it) } // 여기에 user 의 id 를 넣어야 함 -> 로그인 액티비티에서 로그인할 때 sharedPreference 로 저장하게 할 것!
            // test
            Log.d("tag", "onCreate: your new name is $prefs")
            call?.enqueue(object : retrofit2.Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    Log.d("tag", "onResponse: " + response.code())
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