package com.example.treenity_constraint.data.model.store

import com.google.gson.annotations.SerializedName

data class StoreItem(
    @SerializedName("cost") val cost: Int,
    @SerializedName("description") val description: String,
    @SerializedName("imagePath") val imagePath: String,
    @SerializedName("itemId") val itemId: Int,
    //val itemType: String,
    @SerializedName("name") val name: String
)