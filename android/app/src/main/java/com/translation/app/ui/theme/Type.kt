package com.translation.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

val Typography = Typography(
        headlineLarge = TextStyle(fontSize = 28.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
        headlineMedium = TextStyle(fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
        titleLarge = TextStyle(fontSize = 20.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
        titleMedium = TextStyle(fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
        bodyLarge = TextStyle(fontSize = 16.sp),
        bodyMedium = TextStyle(fontSize = 14.sp),
        bodySmall = TextStyle(fontSize = 12.sp),
        labelLarge = TextStyle(fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
        labelSmall = TextStyle(fontSize = 10.sp)
)
