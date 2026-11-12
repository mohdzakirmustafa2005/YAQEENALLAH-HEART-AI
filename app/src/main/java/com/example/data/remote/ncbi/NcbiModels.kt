package com.example.data.remote.ncbi

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ESearchResponse(
    @Json(name = "esearchresult") val esearchresult: ESearchResult? = null
)

@JsonClass(generateAdapter = true)
data class ESearchResult(
    @Json(name = "count") val count: String? = "0",
    @Json(name = "idlist") val idlist: List<String> = emptyList()
)

data class PubMedArticle(
    val pmid: String,
    val title: String,
    val authors: String,
    val journal: String,
    val pubDate: String,
    val doi: String?,
    val articleUrl: String,
    val cacheStatus: String = "LIVE RETRIEVED",
    val retrievalTimestamp: Long = System.currentTimeMillis()
)
