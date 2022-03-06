package com.example.treenity_constraint.ui.mypage.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.treenity_constraint.data.model.mypage.tree.MyTreeResponse
import com.example.treenity_constraint.data.repository.mypage.TreeListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TreeListViewModel @Inject constructor(val repository: TreeListRepository): ViewModel() {

    var liveData: MutableLiveData<MyTreeResponse> = MutableLiveData()

    fun getLiveDataObserver(): MutableLiveData<MyTreeResponse> {
        return liveData
    }

    fun loadListOfData() {
        repository.makeAPICall(liveData)
    }
}