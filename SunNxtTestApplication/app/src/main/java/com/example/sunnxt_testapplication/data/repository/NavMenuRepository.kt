package com.example.sunnxt_testapplication.data.repository

import com.example.sunnxt_testapplication.data.model.NavMenuItem
import com.example.sunnxt_testapplication.data.model.toDomain
import com.example.sunnxt_testapplication.data.remote.NavMenuApiService
import com.example.sunnxt_testapplication.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface NavMenuRepository {
    suspend fun getNavMenu(): Result<List<NavMenuItem>>
}

class NavMenuRepositoryImpl(
    private val apiService: NavMenuApiService = RetrofitClient.navMenuApiService
) : NavMenuRepository {
    override suspend fun getNavMenu(): Result<List<NavMenuItem>> = withContext(Dispatchers.IO) {
        runCatching {
            apiService.getNavMenu(
                group = "navMenuPortal",
                language = "tamil,telegu"
            ).results.map { it.toDomain() }
        }
    }
}
