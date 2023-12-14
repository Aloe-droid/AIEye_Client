package com.example.client.retrofit

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @field:SerializedName("isSucceeded") val isSucceeded: Boolean,
    @field:SerializedName("errors") val errorMessage: List<String>
)