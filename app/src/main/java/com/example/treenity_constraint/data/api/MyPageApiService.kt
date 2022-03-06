package com.example.treenity_constraint.data.api

import com.example.treenity_constraint.data.model.mypage.tree.MyTreeResponse
import com.example.treenity_constraint.data.model.mypage.user.User
import com.example.treenity_constraint.data.model.mypage.walklog.WalkLog
import com.example.treenity_constraint.data.model.store.PostItem
import com.example.treenity_constraint.data.model.store.StoreResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface MyPageApiService { // 로그인 액티비티 개발 완료 됨에 따라 1을 {id}로 대체할 것!

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

    // 상점에서 아이템 구매했을 때 POST
    @Headers("Content-Type: application/json")
    @FormUrlEncoded
    @POST("users/1/items")
    fun pushTreeItem(@Field("itemId") id: Int) : Call<PostItem>

    // 환경설정 페이지에서 이름 바꿨을 때 POST
    @Headers("Content-Type: application/json")
    @FormUrlEncoded
    @POST("users/1")
    fun changeName(@Field("userId") id: Int, @Field("username") name: String) : Call<User>
}