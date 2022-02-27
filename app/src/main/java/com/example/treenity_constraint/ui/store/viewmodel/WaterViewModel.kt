package com.example.treenity_constraint.ui.store.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.treenity_constraint.data.model.store.StoreItem
import com.example.treenity_constraint.data.repository.store.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaterViewModel
@Inject
constructor(private val repository: StoreRepository) : ViewModel() {

    private val _response = MutableLiveData<List<StoreItem>>()
    val responseWater: LiveData<List<StoreItem>>
        get() = _response

    init {
        getWater()
    }

    private fun getWater() = viewModelScope.launch {

        repository.getStoreData().let {response ->

            if (response.isSuccessful){
                _response.postValue(
                    listOf(response.body()!![0])
                )
            } else{
                Log.d("tag", "getWater Error: ${response.code()}")
            }
        }
    }
}