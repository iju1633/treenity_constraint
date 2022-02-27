package com.example.treenity_constraint.data.repository.store

import com.example.treenity_constraint.data.api.MyPageApiService
import javax.inject.Inject

class StoreRepository
@Inject
constructor(private val apiService: MyPageApiService) {
    suspend fun getStoreData() = apiService.getStoreData()
}