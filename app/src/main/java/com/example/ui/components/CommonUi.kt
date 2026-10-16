package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AcademicSubject
import com.example.data.model.AiCouncilMember
import com.example.data.remote.GeminiApiClient

@Composable
fun AppHeader(
    title: String = "OmniStudy AI",
    subtitle: String = "Multi-AI Academic Council & Study Suite",
    onStatusClick: (() -> Unit)? = null
) {
    val isCloudConnected = GeminiApiClient.isConfigured()

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "App Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Connection indicator pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isCloudConnected) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                modifier = Modifier
                    .clickable(enabled = onStatusClick != null) { onStatusClick?.invoke() }
                    .testTag("cloud_status_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isCloudConnected) Color(0xFF16A34A) else Color(0xFFD97706))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCloudConnected) "Gemini Cloud" else "Local AI Suite",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCloudConnected) Color(0xFF166534) else Color(0xFF92400E)
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectSelector(
    selectedSubject: AcademicSubject,
    onSubjectSelected: (AcademicSubject) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag("subject_selector_row"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
    ) {
        items(AcademicSubject.entries) { subject ->
            val isSelected = subject == selectedSubject
            FilterChip(
                selected = isSelected,
                onClick = { onSubjectSelected(subject) },
                label = { Text(subject.title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                leadingIcon = {
                    Icon(
                        imageVector = subject.icon,
                        contentDescription = subject.title,
                        modifier = Modifier.size(16.dp),
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color(subject.color)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.testTag("subject_chip_${subject.id}")
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CouncilMembersChips(
    selectedMembers: Set<AiCouncilMember>,
    onToggleMember: (AiCouncilMember) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Active AI Council Members (${selectedMembers.size}/${AiCouncilMember.entries.size})",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AiCouncilMember.entries.forEach { member ->
                val isSelected = selectedMembers.contains(member)
                FilterChip(
                    selected = isSelected,
                    onClick = { onToggleMember(member) },
                    label = {
                        Text(
                            text = member.displayName,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.Check else member.icon,
                            contentDescription = member.displayName,
                            modifier = Modifier.size(14.dp),
                            tint = if (isSelected) Color.White else Color(member.primaryColor)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(member.primaryColor),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("council_member_chip_${member.id}")
                )
            }
        }
    }
}

@Composable
fun FormattedAcademicText(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(modifier = modifier) {
        val lines = text.lines()
        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("### ") -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = trimmed.removePrefix("### "),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                trimmed.startsWith("## ") -> {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = trimmed.removePrefix("## "),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
                trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("• ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(
                            text = trimmed.substring(2).replace("**", ""),
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor
                        )
                    }
                }
                trimmed.isNotBlank() -> {
                    Text(
                        text = trimmed.replace("**", ""),
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor,
                        modifier = Modifier.padding(vertical = 3.dp)
                    )
                }
                else -> {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
