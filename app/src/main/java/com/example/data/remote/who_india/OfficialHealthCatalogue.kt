package com.example.data.remote.who_india

data class OfficialGuidelineDoc(
    val id: String,
    val organization: String, // WHO, ICMR, MoHFW, NHA ABDM
    val title: String,
    val category: String, // Prevention, Hypertension, ACS, Interoperability
    val publicationDate: String,
    val summary: String,
    val officialUrl: String,
    val lastRetrieved: String = "2026-11-12",
    val statusLabel: String = "OFFICIAL GUIDELINE"
)

object OfficialHealthCatalogue {
    val GUIDELINES = listOf(
        OfficialGuidelineDoc(
            id = "WHO-HEARTS-01",
            organization = "World Health Organization (WHO)",
            title = "HEARTS Technical Package for Cardiovascular Disease Management in Primary Health Care",
            category = "CVD Prevention & Risk Management",
            publicationDate = "2020 (Updated 2024)",
            summary = "Global protocol modules: Healthy-lifestyle counselling, Evidence-based treatment protocols, Access to essential medicines, Risk-based CVD management, Team-based care, and Systems for monitoring.",
            officialUrl = "https://www.who.int/publications/i/item/9789240001367"
        ),
        OfficialGuidelineDoc(
            id = "WHO-HTN-02",
            organization = "World Health Organization (WHO)",
            title = "Guideline for the Pharmacological Treatment of Hypertension in Adults",
            category = "Hypertension",
            publicationDate = "2021",
            summary = "Provides evidence-based recommendations on treatment initiation thresholds, intervals for follow-up, BP targets, and task-sharing models in primary care settings.",
            officialUrl = "https://www.who.int/publications/i/item/9789240033986"
        ),
        OfficialGuidelineDoc(
            id = "ICMR-ACS-01",
            organization = "Indian Council of Medical Research (ICMR)",
            title = "Standard Treatment Workflows: Acute Coronary Syndrome & STEMI Management in India",
            category = "Acute Coronary Care",
            publicationDate = "2022",
            summary = "Defines hub-and-spoke emergency reperfusion protocols, pharmaco-invasive strategies, pre-hospital ECG transmission, and emergency contact triage across Indian districts.",
            officialUrl = "https://main.icmr.nic.in/content/standard-treatment-workflows"
        ),
        OfficialGuidelineDoc(
            id = "MOHFW-NCD-01",
            organization = "Ministry of Health & Family Welfare (MoHFW), Govt of India",
            title = "National Multisectoral Action Plan for Prevention and Control of CVD & Diabetes",
            category = "National Health Mission",
            publicationDate = "2023",
            summary = "Strategic framework for non-communicable disease screening at Ayushman Arogya Mandirs, referral linkage, and longitudinal monitoring of high-risk hypertensive individuals.",
            officialUrl = "https://mohfw.gov.in/"
        ),
        OfficialGuidelineDoc(
            id = "NHA-ABDM-FHIR-01",
            organization = "National Health Authority (NHA) / ABDM",
            title = "Ayushman Bharat Digital Mission (ABDM) FHIR R4 Cardiovascular Profile Specification",
            category = "Health Interoperability",
            publicationDate = "2024",
            summary = "Indian standard implementation guide for interoperable exchange of diagnostic reports, discharge summaries, and vital observations via Ayushman Bharat Health Account (ABHA).",
            officialUrl = "https://abdm.gov.in/"
        )
    )
}
