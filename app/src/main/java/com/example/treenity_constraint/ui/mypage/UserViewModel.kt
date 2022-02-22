package com.example.treenity_constraint.ui.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.treenity_constraint.data.model.mypage.user.User
import com.example.treenity_constraint.data.repository.mypage.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel
@Inject
constructor(private val repository: UserRepository): ViewModel(){

    private val _resp = MutableLiveData<User>()
    val userResp: LiveData<User>
        get() = _resp

    init {
        getUserData()
    }

    private fun getUserData()= viewModelScope.launch {
        repository.getUserData().let { response->

            if(response.isSuccessful) {
                _resp.postValue(response.body())
            }else {
                Log.d("tag", "getUserDataError: ${response.message()}")
            }
        }
    }


}