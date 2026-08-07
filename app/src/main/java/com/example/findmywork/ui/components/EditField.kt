package com.example.findmywork.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.findmywork.ui.theme.FMWBorder
import com.example.findmywork.ui.theme.FMWDanger
import com.example.findmywork.ui.theme.FMWNavy
import com.example.findmywork.ui.theme.FMWSuccess
import com.example.findmywork.ui.theme.FMWTextPrimary
import com.example.findmywork.ui.theme.labelCaption

/**
 * Branded outlined text field with validation states:
 * - error: danger border + supporting error text + danger icon
 * - success: green check icon once [success] is true and the field is non-empty
 * Validation is triggered via [onBlur] (fires only after the field has been focused once).
 */
@Composable
fun FMWEditField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    error: String? = null,
    success: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    supportingText: (@Composable () -> Unit)? = null,
    onBlur: () -> Unit = {}
) {
    val hasError = error != null
    var wasFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { state ->
                if (state.isFocused) {
                    wasFocused = true
                } else if (wasFocused) {
                    onBlur()
                }
            },
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        isError = hasError,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon = {
            when {
                hasError -> Icon(
                    imageVector = Icons.Rounded.ErrorOutline,
                    contentDescription = error,
                    tint = FMWDanger
                )
                success && value.isNotBlank() -> Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = FMWSuccess
                )
            }
        },
        supportingText = {
            supportingText?.invoke()
            if (hasError) {
                Text(text = error ?: "", color = FMWDanger, style = MaterialTheme.typography.labelCaption)
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (hasError) FMWDanger else FMWNavy,
            unfocusedBorderColor = if (hasError) FMWDanger else FMWBorder,
            errorBorderColor = FMWDanger,
            focusedTextColor = FMWTextPrimary,
            cursorColor = FMWNavy
        )
    )
}

/**
 * Branded dropdown backed by Material3's ExposedDropdownMenu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FMWDropdownField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    placeholder: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it) } },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FMWNavy,
                unfocusedBorderColor = FMWBorder,
                cursorColor = FMWNavy
            ),
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
