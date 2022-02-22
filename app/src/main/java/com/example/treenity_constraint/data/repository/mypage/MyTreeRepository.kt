package com.example.treenity_constraint.data.repository.mypage

import com.example.treenity_constraint.data.api.MyPageApiService
import javax.inject.Inject

class MyTreeRepository
@Inject
constructor(private val apiService: MyPageApiService) {
    suspend fun getMyTrees() = apiService.getMyTrees()
}