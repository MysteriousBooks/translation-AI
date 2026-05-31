package com.translation.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
        primary = Primary,
        onPrimary = Surface,
        primaryContainer = PrimaryLight,
        onPrimaryContainer = PrimaryDark,
        secondary = Primary,
        onSecondary = Surface,
        error = Error,
        onError = Surface,
        background = Background,
        onBackground = TextPrimary,
        surface = Surface,
        onSurface = TextPrimary,
        surfaceVariant = Background,
        onSurfaceVariant = TextSecondary,
        outline = Divider
)

@Composable
fun TranslationAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
            colorScheme = LightColorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
    )
}
