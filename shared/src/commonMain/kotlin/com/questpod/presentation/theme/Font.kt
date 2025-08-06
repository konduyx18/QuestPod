package com.questpod.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont

/**
 * Font configuration for QuestPod.
 * Uses Google Fonts to provide Nunito for headings and Roboto for body text.
 */
object QuestPodFont {
    private val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )

    // Nunito font for headings
    private val nunitoFont = GoogleFont("Nunito")
    val nunitoFontFamily = FontFamily(
        Font(
            googleFont = nunitoFont,
            fontProvider = provider,
            weight = FontWeight.Light,
            style = FontStyle.Normal
        ),
        Font(
            googleFont = nunitoFont,
            fontProvider = provider,
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        ),
        Font(
            googleFont = nunitoFont,
            fontProvider = provider,
            weight = FontWeight.Medium,
            style = FontStyle.Normal
        ),
        Font(
            googleFont = nunitoFont,
            fontProvider = provider,
            weight = FontWeight.SemiBold,
            style = FontStyle.Normal
        ),
        Font(
            googleFont = nunitoFont,
            fontProvider = provider,
            weight = FontWeight.Bold,
            style = FontStyle.Normal
        )
    )

    // Roboto font for body text
    private val robotoFont = GoogleFont("Roboto")
    val robotoFontFamily = FontFamily(
        Font(
            googleFont = robotoFont,
            fontProvider = provider,
            weight = FontWeight.Light,
            style = FontStyle.Normal
        ),
        Font(
            googleFont = robotoFont,
            fontProvider = provider,
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        ),
        Font(
            googleFont = robotoFont,
            fontProvider = provider,
            weight = FontWeight.Medium,
            style = FontStyle.Normal
        ),
        Font(
            googleFont = robotoFont,
            fontProvider = provider,
            weight = FontWeight.Bold,
            style = FontStyle.Normal
        )
    )
}
