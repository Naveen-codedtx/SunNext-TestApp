package com.example.sunnxt_testapplication.data.repository

import com.example.sunnxt_testapplication.data.model.Language
import com.example.sunnxt_testapplication.data.model.toDomain
import com.example.sunnxt_testapplication.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface LanguageRepository {
    suspend fun getLanguages(): Result<List<Language>>
}

class LanguageRepositoryImpl(
    private val apiService: com.example.sunnxt_testapplication.data.remote.LanguageApiService =
        RetrofitClient.languageApiService
) : LanguageRepository {

    override suspend fun getLanguages(): Result<List<Language>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = apiService.getLanguages()
            response.languages.firstOrNull()?.terms?.map { it.toDomain() } ?: emptyList()
        }
    }
}
