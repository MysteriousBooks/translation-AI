package com.translation.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.translation.app.util.Languages

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelector(
        selectedCode: String,
        onLanguageSelected: (String) -> Unit,
        label: String = "选择语言",
        modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = Languages.find { it.first == selectedCode }?.second ?: selectedCode

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
                value = selectedName,
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Languages.forEach { (code, name) ->
                DropdownMenuItem(text = { Text(name) }, onClick = {
                    onLanguageSelected(code)
                    expanded = false
                })
            }
        }
    }
}
