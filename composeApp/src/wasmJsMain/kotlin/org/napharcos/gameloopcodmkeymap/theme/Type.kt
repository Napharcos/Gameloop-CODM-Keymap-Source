package org.napharcos.gameloopcodmkeymap.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import gameloopcodmkeymap.composeapp.generated.resources.Res
import gameloopcodmkeymap.composeapp.generated.resources.*
import org.jetbrains.compose.resources.Font

@Composable
fun JosefinSans() = FontFamily(
    Font(Res.font.JosefinSans_Thin, FontWeight.Thin),
    Font(Res.font.JosefinSans_ThinItalic, FontWeight.Thin, FontStyle.Italic),
    Font(Res.font.JosefinSans_ExtraLight, FontWeight.ExtraLight),
    Font(Res.font.JosefinSans_ExtraLightItalic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(Res.font.JosefinSans_Light, FontWeight.Light),
    Font(Res.font.JosefinSans_LightItalic, FontWeight.Light, FontStyle.Italic),
    Font(Res.font.JosefinSans_Regular, FontWeight.Normal),
    Font(Res.font.JosefinSans_Italic, FontWeight.Normal, FontStyle.Italic),
    Font(Res.font.JosefinSans_Medium, FontWeight.Medium),
    Font(Res.font.JosefinSans_MediumItalic, FontWeight.Medium, FontStyle.Italic),
    Font(Res.font.JosefinSans_SemiBold, FontWeight.SemiBold),
    Font(Res.font.JosefinSans_SemiBoldItalic, FontWeight.SemiBold, FontStyle.Italic),
    Font(Res.font.JosefinSans_Bold, FontWeight.Bold)
)

@Composable
fun AppTypography() = Typography().run {
    val fontFamily = JosefinSans()

    copy(
        displayLarge = displayLarge.copy(fontFamily = fontFamily),
        displayMedium = displayMedium.copy(fontFamily = fontFamily),
        displaySmall = displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = titleLarge.copy(fontFamily = fontFamily),
        titleMedium = titleMedium.copy(fontFamily = fontFamily),
        titleSmall = titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = bodySmall.copy(fontFamily = fontFamily),
        labelLarge = labelLarge.copy(fontFamily = fontFamily),
        labelMedium = labelMedium.copy(fontFamily = fontFamily),
        labelSmall = labelSmall.copy(fontFamily = fontFamily),
    )
}