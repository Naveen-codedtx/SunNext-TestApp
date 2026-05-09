package com.example.sunnxt_testapplication.data.remote

import com.example.sunnxt_testapplication.data.model.LanguageResponse
import retrofit2.http.GET

interface LanguageApiService {
    @GET("custom/sunott/v1/languages/")
    suspend fun getLanguages(): LanguageResponse
}
