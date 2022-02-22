package com.example.treenity_constraint.data.api

import com.example.treenity_constraint.data.model.mypage.tree.MyTreeResponse
import com.example.treenity_constraint.data.model.mypage.user.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface MyPageApiService {


    // User 부분
    @GET("users/1/my-page")
    suspend fun getUserData(): Response<User>

    // MyPageActivity
    @GET("users/1/trees")
    suspend fun getMyTrees(): Response<MyTreeResponse>

    // TreeListActivity
    @GET("users/1/trees")
    fun getTreeData() : Call<MyTreeResponse>
}