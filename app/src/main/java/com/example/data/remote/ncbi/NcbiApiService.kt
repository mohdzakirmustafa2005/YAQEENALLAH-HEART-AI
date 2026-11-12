package com.example.data.remote.ncbi

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NcbiApiService {
    @GET("esearch.fcgi")
    suspend fun searchPubMed(
        @Query("db") db: String = "pubmed",
        @Query("term") term: String,
        @Query("retmode") retmode: String = "json",
        @Query("retmax") retmax: Int = 5,
        @Query("email") email: String? = null,
        @Query("api_key") apiKey: String? = null
    ): Response<ESearchResponse>

    @GET("esummary.fcgi")
    suspend fun getSummaryRaw(
        @Query("db") db: String = "pubmed",
        @Query("id") idList: String,
        @Query("retmode") retmode: String = "json",
        @Query("email") email: String? = null,
        @Query("api_key") apiKey: String? = null
    ): Response<ResponseBody>
}
