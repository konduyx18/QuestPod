package com.questpod.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.font.FontStyle

// Primary color: Deep purple (#673AB7)
val PrimaryLight = Color(0xFF673AB7)
val PrimaryDark = Color(0xFF9575CD)
val PrimaryVariantLight = Color(0xFF512DA8)
val PrimaryVariantDark = Color(0xFF7E57C2)

// Secondary color: Teal (#009688)
val SecondaryLight = Color(0xFF009688)
val SecondaryDark = Color(0xFF4DB6AC)
val SecondaryVariantLight = Color(0xFF00796B)
val SecondaryVariantDark = Color(0xFF26A69A)

// Background colors
val BackgroundLight = Color(0xFFFAFAFA)
val BackgroundDark = Color(0xFF121212)

// Surface colors
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF1E1E1E)

// Error colors
val ErrorLight = Color(0xFFB00020)
val ErrorDark = Color(0xFFCF6679)

// Tertiary colors for accents
val TertiaryLight = Color(0xFFFF5722)
val TertiaryDark = Color(0xFFFF8A65)

// Font provider for Google Fonts
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Nunito font for headings
private val nunitoFont = GoogleFont("Nunito")
val NunitoFontFamily = FontFamily(
    Font(googleFont = nunitoFont, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = nunitoFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = nunitoFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = nunitoFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = nunitoFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = nunitoFont, fontProvider = provider, weight = FontWeight.ExtraBold),
    Font(googleFont = nunitoFont, fontProvider = provider, weight = FontWeight.Normal, style = FontStyle.Italic)
)

// Roboto font for body text
private val robotoFont = GoogleFont("Roboto")
val RobotoFontFamily = FontFamily(
    Font(googleFont = robotoFont, fontProvider = provider, weight = FontWeight.Light),
    Font(googleFont = robotoFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = robotoFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = robotoFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = robotoFont, fontProvider = provider, weight = FontWeight.Normal, style = FontStyle.Italic)
)

// Elevation values for different surface types
data class Elevations(
    val card: Int = 2,
    val dialog: Int = 6,
    val modalSheet: Int = 12,
    val menu: Int = 4,
    val fab: Int = 8,
    val drawer: Int = 16
)

// Local composition for elevations
val LocalElevations = staticCompositionLocalOf { Elevations() }

// Light theme color scheme
private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = Color.White,
    primaryContainer = PrimaryVariantLight,
    onPrimaryContainer = Color.White,
    secondary = SecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = SecondaryVariantLight,
    onSecondaryContainer = Color.White,
    tertiary = TertiaryLight,
    onTertiary = Color.White,
    background = BackgroundLight,
    onBackground = Color.Black,
    surface = SurfaceLight,
    onSurface = Color.Black,
    error = ErrorLight,
    onError = Color.White
)

// Dark theme color scheme
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = Color.Black,
    primaryContainer = PrimaryVariantDark,
    onPrimaryContainer = Color.Black,
    secondary = SecondaryDark,
    onSecondary = Color.Black,
    secondaryContainer = SecondaryVariantDark,
    onSecondaryContainer = Color.Black,
    tertiary = TertiaryDark,
    onTertiary = Color.Black,
    background = BackgroundDark,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    error = ErrorDark,
    onError = Color.Black
)

// Typography
val QuestPodTypography = Typography(
    // Display styles - Using Nunito for headings
    displayLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    
    // Headline styles - Using Nunito for headings
    headlineLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    
    // Title styles - Using Nunito for headings
    titleLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    // Body styles - Using Roboto for body text
    bodyLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp, // Larger for better story readability
        lineHeight = 28.sp, // Increased line height for readability
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp, // Larger for better story readability
        lineHeight = 24.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp, // Larger for better story readability
        lineHeight = 20.sp,
        letterSpacing = 0.4.sp
    ),
    
    // Label styles - Using Roboto for labels
    labelLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// Shapes - More specific shape definitions
val QuestPodShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp), // For input fields and chips
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp), // For cards and buttons
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

/**
 * QuestPod theme composable that applies Material 3 styling with custom colors, typography, and shapes.
 *
 * @param darkTheme Whether to use dark theme. Defaults to system setting.
 * @param dynamicColor Whether to use dynamic color adaptation based on wallpaper (Android 12+).
 * @param content The content to be styled.
 */
@Composable
fun QuestPodTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val elevations = Elevations()
    
    // Use dynamic color on Android 12+ when enabled
    val colorScheme = when {
        dynamicColor -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalElevations provides elevations) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = QuestPodTypography,
            shapes = QuestPodShapes,
            content = content
        )
    }
}

/**
 * Access the current elevations configuration from composables.
 */
object QuestPodTheme {
    val elevations: Elevations
        @Composable
        get() = LocalElevations.current
}
