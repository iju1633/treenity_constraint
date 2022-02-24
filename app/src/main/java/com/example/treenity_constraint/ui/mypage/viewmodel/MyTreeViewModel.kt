package com.example.treenity_constraint.ui.mypage.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.treenity_constraint.data.model.mypage.tree.Item
import com.example.treenity_constraint.data.model.mypage.tree.MyTreeItem
import com.example.treenity_constraint.data.repository.mypage.MyTreeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyTreeViewModel
@Inject
constructor(private val repository: MyTreeRepository) : ViewModel() {

    private val _response = MutableLiveData<List<MyTreeItem>>()
    val responseMyTree: LiveData<List<MyTreeItem>>
        get() = _response

    init {
        getAllTrees()
    }

    private fun getAllTrees() = viewModelScope.launch {


        repository.getMyTrees().let {response ->
            // treeId는 1부터 시작하기에 겹치지 않기 위해 0번을 주었고, imagePath의 경우, pinterest 에서 다운다아 ifh 이미지 호스팅을 통해 이미지 주소를 만듦
            val lastItem = MyTreeItem(0, Item("Goto TreeList", "https://ifh.cc/g/eA7BXD.jpg"), 0, 0, "")

            if (response.isSuccessful){ // 아이템은 3개만 보여줄 것
                _response.postValue(listOf(response.body()!![0],
                                           response.body()!![1],
                                           response.body()!![2],
                                           lastItem
                                          )
                                   )
            } else{
                Log.d("tag", "getAllTrees Error: ${response.code()}")
            }
        }
    }
}