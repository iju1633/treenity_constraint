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
class SeedsViewModel
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

            if (response.isSuccessful){ // 아이템은 3개만 보여줄 것

                for(i in 1 until response.body()!!.size)
                    seeds.add(response.body()!![i])

                _response.postValue(seeds)

            } else{
                Log.d("tag", "getWater Error: ${response.code()}")
            }
        }
    }
}