package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.clinicaltrials.FormattedClinicalStudy
import com.example.data.remote.ncbi.PubMedArticle
import com.example.data.remote.openfda.FormattedFdaReport
import com.example.data.remote.who_india.OfficialGuidelineDoc
import com.example.ui.theme.*

@Composable
fun PubMedArticleCard(
    article: PubMedArticle,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pubmed_card_${article.pmid}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MedicalNavyBorder))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusBadge(status = article.cacheStatus)
                Text(
                    text = "PMID: ${article.pmid}",
                    color = PulseCyanLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = article.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${article.authors} — ${article.journal} (${article.pubDate})",
                fontSize = 12.sp,
                color = TextSecondaryDark,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.articleUrl))
                        context.startActivity(intent)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Open PubMed NCBI",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PulseCyan
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = "Open Link",
                    tint = PulseCyan,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun ClinicalStudyCard(
    study: FormattedClinicalStudy,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("trial_card_${study.nctId}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MedicalNavyBorder))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = Color(0xFF1E3A8A).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "CLINICAL STUDY (${study.status})",
                        color = Color(0xFF93C5FD),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = study.nctId,
                    color = TextTertiaryDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = study.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Sponsor: ${study.sponsor}",
                fontSize = 11.sp,
                color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = study.summary,
                fontSize = 12.sp,
                color = TextSecondaryDark,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(study.officialUrl))
                        context.startActivity(intent)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "View ClinicalTrials.gov Protocol",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PulseCyan
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = "Open Link",
                    tint = PulseCyan,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun OpenFdaReportCard(
    report: FormattedFdaReport,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("fda_card_${report.reportId}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MedicalNavyBorder))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = WarningOrange.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "openFDA ADVERSE-EVENT",
                        color = WarningOrange,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = report.reportId,
                    color = TextTertiaryDark,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Reported Reactions: ${report.reportedReactions.joinToString(", ")}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Source Period: ${report.sourceDate} (Received: ${report.receiveDate})",
                fontSize = 11.sp,
                color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                color = MedicalNavySurface,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "Regulatory Note: ${report.disclaimer} Spontaneous reports do not prove medication causality and must not be used to self-adjust treatment.",
                    fontSize = 11.sp,
                    color = WarningAmber,
                    modifier = Modifier.padding(8.dp),
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun OfficialGuidelineCard(
    doc: OfficialGuidelineDoc,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("guideline_card_${doc.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MedicalNavyCard),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(EmeraldHealth.copy(alpha = 0.4f)))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = EmeraldHealth.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = doc.organization,
                        color = EmeraldHealthLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = doc.publicationDate,
                    color = TextTertiaryDark,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = doc.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = doc.summary,
                fontSize = 12.sp,
                color = TextSecondaryDark,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(doc.officialUrl))
                        context.startActivity(intent)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Official Publication Source",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = EmeraldHealthLight
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = "Open Link",
                    tint = EmeraldHealthLight,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status) {
        "LIVE RETRIEVED" -> Pair(EmeraldHealth.copy(alpha = 0.25f), EmeraldHealthLight)
        "CACHED" -> Pair(PulseCyan.copy(alpha = 0.2f), PulseCyanLight)
        "STALE" -> Pair(WarningAmber.copy(alpha = 0.25f), WarningAmber)
        else -> Pair(MedicalNavySurface, TextSecondaryDark)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
