package com.example.findmywork.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.findmywork.data.model.ServiceOffer
import com.example.findmywork.data.model.ServicePackage
import com.example.findmywork.data.formatInr
import com.example.findmywork.ui.theme.FMWBorder
import com.example.findmywork.ui.theme.FMWDanger
import com.example.findmywork.ui.theme.FMWNavy
import com.example.findmywork.ui.theme.FMWTextPrimary
import com.example.findmywork.ui.theme.FMWTextSecondary

/**
 * Read-only display card for a service package (name, description, ₹ price, validity).
 */
@Composable
fun ServicePackageCard(
    pkg: ServicePackage,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(1.dp, FMWBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pkg.name.ifBlank { "Untitled package" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FMWTextPrimary
                )
                if (pkg.description.isNotBlank()) {
                    Text(
                        text = pkg.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = FMWTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatInr(pkg.price),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FMWNavy
                    )
                    Text(
                        text = "  ·  ${pkg.validityDays} days validity",
                        style = MaterialTheme.typography.bodySmall,
                        color = FMWTextSecondary
                    )
                }
            }
            IconButton(onClick = onEdit, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit ${pkg.name}",
                    tint = FMWNavy,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = "Delete ${pkg.name}",
                    tint = FMWDanger,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Read-only display card for a service offer (title, discount, description).
 */
@Composable
fun ServiceOfferCard(
    offer: ServiceOffer,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(1.dp, FMWBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = offer.title.ifBlank { "Untitled offer" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FMWTextPrimary
                )
                Text(
                    text = if (offer.discountPercent > 0) "${offer.discountPercent}% off" else "Special offer",
                    style = MaterialTheme.typography.bodySmall,
                    color = FMWNavy,
                    fontWeight = FontWeight.Medium
                )
                if (offer.description.isNotBlank()) {
                    Text(
                        text = offer.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = FMWTextSecondary
                    )
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = "Delete ${offer.title}",
                    tint = FMWDanger,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Inline add/edit form for a service package. When [initial] is non-null the editor
 * pre-fills and saving updates the existing package (id preserved).
 */
@Composable
fun PackageEditor(
    initial: ServicePackage?,
    onSave: (ServicePackage) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var price by remember { mutableStateOf(if ((initial?.price ?: 0.0) > 0) initial!!.price.toString() else "") }
    var validity by remember { mutableStateOf(initial?.validityDays?.toString() ?: "30") }

    Column(modifier = modifier.fillMaxWidth()) {
        FMWEditField(
            value = name,
            onValueChange = { name = it },
            label = "Package name",
            placeholder = "e.g. Deep Clean 2BHK"
        )
        Spacer(modifier = Modifier.height(8.dp))
        FMWEditField(
            value = description,
            onValueChange = { description = it },
            label = "Description (optional)",
            placeholder = "What's included?",
            singleLine = false,
            minLines = 2,
            maxLines = 3
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            FMWEditField(
                value = price,
                onValueChange = { price = it.filter { c -> c.isDigit() || c == '.' } },
                label = "Price (₹)",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            FMWEditField(
                value = validity,
                onValueChange = { validity = it.filter { c -> c.isDigit() } },
                label = "Validity (days)",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = {
                    onSave(
                        ServicePackage(
                            id = initial?.id ?: java.util.UUID.randomUUID().toString(),
                            name = name.trim(),
                            description = description.trim(),
                            price = price.toDoubleOrNull() ?: 0.0,
                            validityDays = validity.toIntOrNull() ?: 30
                        )
                    )
                },
                enabled = name.isNotBlank() && (price.toDoubleOrNull() ?: 0.0) > 0,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FMWNavy,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(if (initial == null) "Add Package" else "Save", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/**
 * Inline add form for a service offer (title + discount %).
 */
@Composable
fun OfferEditor(
    onSave: (ServiceOffer) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        FMWEditField(
            value = title,
            onValueChange = { title = it },
            label = "Offer title",
            placeholder = "e.g. First booking 10% off"
        )
        Spacer(modifier = Modifier.height(8.dp))
        FMWEditField(
            value = discount,
            onValueChange = { discount = it.filter { c -> c.isDigit() } },
            label = "Discount %",
            keyboardType = KeyboardType.Number
        )
        Spacer(modifier = Modifier.height(8.dp))
        FMWEditField(
            value = description,
            onValueChange = { description = it },
            label = "Description (optional)",
            singleLine = false,
            minLines = 2,
            maxLines = 3
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = {
                    onSave(
                        ServiceOffer(
                            id = java.util.UUID.randomUUID().toString(),
                            title = title.trim(),
                            discountPercent = discount.toIntOrNull() ?: 0,
                            description = description.trim()
                        )
                    )
                },
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FMWNavy,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Add Offer", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
