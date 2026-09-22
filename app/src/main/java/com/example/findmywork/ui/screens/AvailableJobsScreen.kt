package com.example.findmywork.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.ui.components.JobCard
import com.example.findmywork.ui.components.marketplace.MarketplaceEmptyState
import com.example.findmywork.ui.theme.FMWAmber
import com.example.findmywork.ui.theme.FMWBackground
import com.example.findmywork.ui.theme.FMWBorder
import com.example.findmywork.ui.theme.FMWNavy
import com.example.findmywork.ui.theme.FMWOrange
import com.example.findmywork.ui.theme.FMWPrimaryLight
import com.example.findmywork.ui.theme.FMWSuccess
import com.example.findmywork.ui.theme.FMWSurface
import com.example.findmywork.ui.theme.FMWTextPrimary
import com.example.findmywork.ui.theme.FMWTextSecondary

@Composable
fun AvailableJobsScreen(
    workerId: String?,
    firestoreRepository: FirestoreRepository,
    onNavigateToJobDetails: (String) -> Unit
) {
    val jobs by firestoreRepository.getAvailableJobsFlow()
        .collectAsState(initial = emptyList())

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Nearest (< 5 km)", "High Payout (> ₹500)", "Electrical")

    val filteredJobs = remember(jobs, selectedFilter) {
        when (selectedFilter) {
            "Nearest (< 5 km)" -> jobs.filter { it.distance <= 5.0 }
            "High Payout (> ₹500)" -> jobs.filter { it.totalAmount >= 500.0 }
            "Electrical" -> jobs.filter { it.subServiceName.contains("Wiring", ignoreCase = true) || it.subServiceName.contains("Fan", ignoreCase = true) || it.subServiceName.contains("Switch", ignoreCase = true) }
            else -> jobs
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FMWBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Screen Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Available Jobs",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = FMWTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(FMWSuccess)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${filteredJobs.size} nearby requests in Ahmedabad",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FMWTextSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(FMWOrange.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Work,
                        contentDescription = null,
                        tint = FMWOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "LIVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FMWOrange
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter chips row
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) FMWNavy else FMWSurface)
                        .border(
                            1.dp,
                            if (isSelected) FMWNavy else FMWBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = filter,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else FMWTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredJobs.isEmpty()) {
            MarketplaceEmptyState(
                title = "No Jobs In This Category",
                message = "There are no incoming requests matching '$selectedFilter' right now. Try switching back to 'All'.",
                icon = Icons.Rounded.Search,
                actionText = "Reset Filter",
                onAction = { selectedFilter = "All" }
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredJobs, key = { it.id }) { job ->
                    JobCard(
                        job = job,
                        onAccept = { onNavigateToJobDetails(job.id) },
                        onReject = { /* Reject action */ }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}
