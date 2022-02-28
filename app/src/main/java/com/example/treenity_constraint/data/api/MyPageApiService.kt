package com.example.treenity_constraint.data.api

import com.example.treenity_constraint.data.model.mypage.tree.MyTreeResponse
import com.example.treenity_constraint.data.model.mypage.user.User
import com.example.treenity_constraint.data.model.mypage.walklog.WalkLog
import com.example.treenity_constraint.data.model.mypage.walklog.WalkLogResponse
import com.example.treenity_constraint.data.model.store.StoreResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface MyPageApiService {

    // User 부분
    @GET("users/1")
    suspend fun getUserData(): Response<User>

    // MyPageActivity
    @GET("users/1/walk-logs")
    suspend fun getWalkLogs() : Response<List<WalkLog>>

    @GET("users/1/trees")
    suspend fun getMyTrees(): Response<MyTreeResponse>

    // TreeListActivity
    @GET("users/1/trees")
    fun getTreeData() : Call<MyTreeResponse>

    // StoreActivity
    @GET("items")
    suspend fun getStoreData() : Response<StoreResponse>

}