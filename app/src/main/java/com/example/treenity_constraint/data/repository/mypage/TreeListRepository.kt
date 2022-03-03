package com.example.treenity_constraint.data.repository.mypage

import androidx.lifecycle.MutableLiveData
import com.example.treenity_constraint.data.api.MyPageApiService
import com.example.treenity_constraint.data.model.mypage.tree.MyTreeResponse
import com.example.treenity_constraint.di.MyPageNetworkModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class TreeListRepository @Inject constructor(val retroInstance: MyPageApiService) {

    // code for recyclerview
    fun makeAPICall(liveTreeList: MutableLiveData<MyTreeResponse>) {
        val retroInstance = MyPageNetworkModule.provideRetrofitInstance()
        val call  = retroInstance.getTreeData()

        call.enqueue(object : Callback<MyTreeResponse> { // 비동기 처리
            override fun onFailure(call: Call<MyTreeResponse>, t: Throwable) {
                liveTreeList.postValue(null)
            }

            override fun onResponse(
                call: Call<MyTreeResponse>,
                response: Response<MyTreeResponse>
            ) {
                liveTreeList.postValue(response.body())
            }
        })
    }
}