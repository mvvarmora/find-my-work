package com.example.findmywork.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
@Composable
fun StatusChip(status: String) {
    val (color, label) = when (status) {
        "PENDING" -> Pair(MaterialTheme.colorScheme.tertiary, "Pending")
        "ACCEPTED" -> Pair(MaterialTheme.colorScheme.primary, "Accepted")
        "ON_THE_WAY" -> Pair(MaterialTheme.colorScheme.primary, "On The Way")
        "ARRIVED" -> Pair(MaterialTheme.colorScheme.secondary, "Arrived")
        "STARTED" -> Pair(MaterialTheme.colorScheme.secondary, "In Progress")
        "COMPLETED" -> Pair(MaterialTheme.colorScheme.secondary, "Completed")
        "RATED" -> Pair(MaterialTheme.colorScheme.secondary, "Completed")
        "CANCELLED" -> Pair(MaterialTheme.colorScheme.error, "Cancelled")
        else -> Pair(MaterialTheme.colorScheme.onSurface, status)
    }

    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(100.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}
