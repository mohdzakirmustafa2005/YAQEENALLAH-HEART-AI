package com.example.data.remote.clinicaltrials

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class ClinicalTrialsResponse(
    @Json(name = "studies") val studies: List<StudyItem> = emptyList(),
    @Json(name = "totalCount") val totalCount: Int? = null
)

@JsonClass(generateAdapter = true)
data class StudyItem(
    @Json(name = "protocolSection") val protocolSection: ProtocolSection? = null
)

@JsonClass(generateAdapter = true)
data class ProtocolSection(
    @Json(name = "identificationModule") val identificationModule: IdentificationModule? = null,
    @Json(name = "statusModule") val statusModule: StatusModule? = null,
    @Json(name = "sponsorCollaboratorsModule") val sponsorCollaboratorsModule: SponsorModule? = null,
    @Json(name = "conditionsModule") val conditionsModule: ConditionsModule? = null,
    @Json(name = "descriptionModule") val descriptionModule: DescriptionModule? = null
)

@JsonClass(generateAdapter = true)
data class IdentificationModule(
    @Json(name = "nctId") val nctId: String = "",
    @Json(name = "briefTitle") val briefTitle: String = "",
    @Json(name = "officialTitle") val officialTitle: String? = null
)

@JsonClass(generateAdapter = true)
data class StatusModule(
    @Json(name = "overallStatus") val overallStatus: String = "UNKNOWN"
)

@JsonClass(generateAdapter = true)
data class SponsorModule(
    @Json(name = "leadSponsor") val leadSponsor: LeadSponsor? = null
)

@JsonClass(generateAdapter = true)
data class LeadSponsor(
    @Json(name = "name") val name: String = ""
)

@JsonClass(generateAdapter = true)
data class ConditionsModule(
    @Json(name = "conditions") val conditions: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class DescriptionModule(
    @Json(name = "briefSummary") val briefSummary: String? = null
)

data class FormattedClinicalStudy(
    val nctId: String,
    val title: String,
    val status: String,
    val sponsor: String,
    val conditions: String,
    val summary: String,
    val officialUrl: String,
    val cacheStatus: String = "LIVE RETRIEVED"
)

interface ClinicalTrialsApiService {
    @GET("studies")
    suspend fun searchStudies(
        @Query("query.cond") condition: String,
        @Query("pageSize") pageSize: Int = 5
    ): Response<ClinicalTrialsResponse>
}
