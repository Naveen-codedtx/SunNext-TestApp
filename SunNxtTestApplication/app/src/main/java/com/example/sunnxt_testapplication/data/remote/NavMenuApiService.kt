package com.example.sunnxt_testapplication.data.remote

import com.example.sunnxt_testapplication.data.model.NavMenuResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NavMenuApiService {
    @GET("content/v2/carousel/_info")
    suspend fun getNavMenu(
        @Query("group") group: String,
        @Query("language") language: String
    ): NavMenuResponse
}
