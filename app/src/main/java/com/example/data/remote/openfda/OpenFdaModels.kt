package com.example.data.remote.openfda

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class OpenFdaDrugEventResponse(
    @Json(name = "meta") val meta: FdaMeta? = null,
    @Json(name = "results") val results: List<FdaEventResult> = emptyList()
)

@JsonClass(generateAdapter = true)
data class FdaMeta(
    @Json(name = "disclaimer") val disclaimer: String? = null,
    @Json(name = "last_updated") val lastUpdated: String? = null
)

@JsonClass(generateAdapter = true)
data class FdaEventResult(
    @Json(name = "safetyreportid") val safetyreportid: String = "",
    @Json(name = "receivedate") val receivedate: String? = null,
    @Json(name = "patient") val patient: FdaPatient? = null
)

@JsonClass(generateAdapter = true)
data class FdaPatient(
    @Json(name = "reaction") val reaction: List<FdaReaction> = emptyList()
)

@JsonClass(generateAdapter = true)
data class FdaReaction(
    @Json(name = "reactionmeddrapt") val reactionmeddrapt: String? = null
)

data class FormattedFdaReport(
    val reportId: String,
    val receiveDate: String,
    val reportedReactions: List<String>,
    val disclaimer: String,
    val sourceDate: String,
    val cacheStatus: String = "LIVE RETRIEVED"
)

interface OpenFdaApiService {
    @GET("drug/event.json")
    suspend fun searchAdverseEvents(
        @Query("search") search: String,
        @Query("limit") limit: Int = 3,
        @Query("api_key") apiKey: String? = null
    ): Response<OpenFdaDrugEventResponse>
}
