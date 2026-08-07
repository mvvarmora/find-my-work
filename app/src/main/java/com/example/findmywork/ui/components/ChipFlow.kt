package com.example.findmywork.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.findmywork.ui.theme.FMWAmberSoft
import com.example.findmywork.ui.theme.FMWBorder
import com.example.findmywork.ui.theme.FMWNavy
import com.example.findmywork.ui.theme.FMWPrimaryLight
import com.example.findmywork.ui.theme.FMWTextPrimary
import com.example.findmywork.ui.theme.FMWTextSecondary

/**
 * FlowRow of multi-select FilterChips. Selected chips render on a navy background
 * with white text; unselected chips use the light navy tint.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FMWChipFlow(
    items: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    emptyText: String = "Nothing selected yet"
) {
    if (items.isEmpty()) {
        Text(
            text = emptyText,
            style = MaterialTheme.typography.bodySmall,
            color = FMWTextSecondary
        )
        return
    }
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items.forEach { item ->
            val isSelected = item in selected
            FilterChip(
                selected = isSelected,
                onClick = { onToggle(item) },
                label = {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = FMWPrimaryLight,
                    labelColor = FMWTextPrimary,
                    selectedContainerColor = FMWNavy,
                    selectedLabelColor = onPrimary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = FMWBorder,
                    selectedBorderColor = FMWNavy
                )
            )
        }
    }
}

/**
 * FlowRow of chips that can be removed (InputChip with a delete affordance) plus a
 * compact inline add-row (text field + add button). Add/remove animate via animateContentSize.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FMWAddableChipFlow(
    items: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
    emptyText: String = "No items added yet",
    addHint: String = "Add item"
) {
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    Column(modifier = modifier.fillMaxWidth().animateContentSize()) {
        if (items.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items.forEach { item ->
                    InputChip(
                        selected = false,
                        onClick = {},
                        label = {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { onRemove(item) },
                                modifier = Modifier.size(InputChipDefaults.IconSize)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Remove $item",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        colors = InputChipDefaults.inputChipColors(
                            containerColor = FMWAmberSoft,
                            labelColor = FMWTextPrimary
                        ),
                        border = InputChipDefaults.inputChipBorder(
                            enabled = true,
                            selected = false,
                            borderColor = FMWBorder
                        )
                    )
                }
            }
        } else {
            Text(
                text = emptyText,
                style = MaterialTheme.typography.bodySmall,
                color = FMWTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        var draft by remember { mutableStateOf("") }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                singleLine = true,
                placeholder = { Text(addHint) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FMWNavy,
                    unfocusedBorderColor = FMWBorder
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val trimmed = draft.trim()
                    if (trimmed.isNotEmpty()) {
                        onAdd(trimmed)
                        draft = ""
                    }
                },
                enabled = draft.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FMWNavy,
                    contentColor = onPrimary
                ),
                modifier = Modifier.size(width = 72.dp, height = 52.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
