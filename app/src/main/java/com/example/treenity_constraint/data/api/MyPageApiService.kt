package com.example.treenity_constraint.data.api

import com.example.treenity_constraint.data.model.mypage.tree.MyTreeResponse
import com.example.treenity_constraint.data.model.mypage.user.User
import com.example.treenity_constraint.data.model.mypage.walklog.WalkLog
import com.example.treenity_constraint.data.model.store.PostItem
import com.example.treenity_constraint.data.model.store.StoreResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

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

    @Headers("Content-Type: application/json")
    @FormUrlEncoded
    @POST("users/1/items")
    fun pushTreeItem(@Field("itemId") id: Int) : Call<PostItem>

    @Headers("Content-Type: application/json")
    @FormUrlEncoded
    @POST("users/1")
    fun changeName(@Field("userId") id: Int, @Field("username") name: String) : Call<User>
}