package com.example.findmywork.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.findmywork.data.repository.FirestoreRepository
import com.example.findmywork.ui.components.marketplace.CategoryChipRow
import com.example.findmywork.ui.components.marketplace.MarketplaceEmptyState
import com.example.findmywork.ui.components.marketplace.WorkerCard
import com.example.findmywork.ui.theme.*

@Composable
fun CategoryWorkersScreen(
    categoryId: String,
    firestoreRepository: FirestoreRepository,
    onNavigateBack: () -> Unit,
    onNavigateToWorkerDetail: (String) -> Unit,
    onNavigateToBookingFlow: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val workers by firestoreRepository.getWorkersByCategoryFlow(categoryId).collectAsState(initial = emptyList())
    var selectedFilter by remember { mutableStateOf("All") }

    val filterChips = listOf("All", "Top Rated 4.8+", "Available Today", "Budget Friendly")

    val filteredWorkers = remember(workers, selectedFilter) {
        when (selectedFilter) {
            "Top Rated 4.8+" -> workers.filter {
                val rating = if (it.ratingCount > 0) it.ratingSum / it.ratingCount else 4.8
                rating >= 4.8
            }
            "Available Today" -> workers.filter { it.isOnline }
            "Budget Friendly" -> workers.sortedBy { it.pricing ?: 500.0 }
            else -> workers
        }
    }

    val displayCategoryTitle = categoryId.replace("_", " ").lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FMWSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = FMWTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "$displayCategoryTitle Experts",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = FMWTextPrimary
                        )
                        Text(
                            text = "${workers.size} verified professionals available",
                            style = MaterialTheme.typography.bodySmall,
                            color = FMWTextSecondary
                        )
                    }
                }

                CategoryChipRow(
                    categories = filterChips,
                    selectedCategory = selectedFilter,
                    onSelectCategory = { selectedFilter = it },
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                HorizontalDivider(color = FMWBorder.copy(alpha = 0.5f))
            }
        },
        containerColor = FMWBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredWorkers.isEmpty()) {
                item {
                    MarketplaceEmptyState(
                        title = "No Workers Available",
                        message = "No professionals currently matched your filter for $displayCategoryTitle. Try selecting 'All'.",
                        actionText = "Reset Filter",
                        onAction = { selectedFilter = "All" }
                    )
                }
            } else {
                items(filteredWorkers) { worker ->
                    val primarySkill = worker.skills.firstOrNull() ?: "$displayCategoryTitle Specialist"
                    val price = worker.pricing?.let { "₹${it.toInt()} / service" } ?: "₹500 / service"

                    WorkerCard(
                        worker = worker,
                        professionTitle = primarySkill,
                        distanceText = "2.1 km away",
                        priceText = price,
                        onCardClick = { onNavigateToWorkerDetail(worker.id) },
                        onBookClick = { onNavigateToBookingFlow(worker.id) }
                    )
                }
            }
        }
    }
}
