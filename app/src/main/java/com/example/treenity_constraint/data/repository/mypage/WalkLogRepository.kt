package com.example.treenity_constraint.data.repository.mypage

import com.example.treenity_constraint.data.api.MyPageApiService
import javax.inject.Inject

class WalkLogRepository constructor(private val apiService: MyPageApiService){

    suspend fun getWalkLogs() = apiService.getWalkLogs()
}