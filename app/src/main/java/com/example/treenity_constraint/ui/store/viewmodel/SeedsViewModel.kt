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
class SeedsViewModel // 물의 경우, recyclerview 로 보여줄 필요가 없기에 viewModel 을 구분지어 개발
@Inject
constructor(private val repository: StoreRepository) : ViewModel() {

    private val _response = MutableLiveData<List<StoreItem>>()
    val responseSeeds: LiveData<List<StoreItem>>
        get() = _response

    init {
        getSeeds()
    }

    private fun getSeeds() = viewModelScope.launch {

        var seeds = ArrayList<StoreItem>()

        repository.getStoreData().let {response ->

            if (response.isSuccessful){

                for(i in 1 until response.body()!!.size) // 첫 번째 아이템은 물이기에 1부터 index 가 시작
                    seeds.add(response.body()!![i])

                _response.postValue(seeds)

            } else{
                Log.d("tag", "getWater Error: ${response.code()}")
            }
        }
    }
}