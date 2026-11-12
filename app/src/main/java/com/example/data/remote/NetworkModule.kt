package com.example.data.remote

import com.example.data.remote.clinicaltrials.ClinicalTrialsApiService
import com.example.data.remote.gemini.GeminiApiService
import com.example.data.remote.ncbi.NcbiApiService
import com.example.data.remote.openfda.OpenFdaApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    val ncbiService: NcbiApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(NcbiApiService::class.java)
    }

    val clinicalTrialsService: ClinicalTrialsApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://clinicaltrials.gov/api/v2/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ClinicalTrialsApiService::class.java)
    }

    val openFdaService: OpenFdaApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.fda.gov/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OpenFdaApiService::class.java)
    }

    val geminiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }
}
