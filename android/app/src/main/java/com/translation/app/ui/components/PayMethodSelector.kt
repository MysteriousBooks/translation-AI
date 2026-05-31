package com.translation.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.translation.app.util.PayType

@Composable
fun PayMethodSelector(selectedPayType: Int, onPayTypeSelected: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        PayType.entries.forEach { payType ->
            val selected = payType.value == selectedPayType
            Surface(
                    modifier = Modifier.weight(1f).clickable { onPayTypeSelected(payType.value) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    border = if (selected) null else CardDefaults.outlinedCardBorder()
            ) {
                Text(
                        text = payType.label,
                        modifier = Modifier.padding(vertical = 12.dp).align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
