package org.app.wakeupalarm.presentation.theme

import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color

/**
 * Paleta de cores escuras para o tema do aplicativo
 */
val DarkColorPalette = darkColors(
    primary = Color(0xFF4A5CFF),
    primaryVariant = Color(0xFF3F51B5),
    secondary = Color(0xFFFF4081),
    background = Color(0xFF1F2033),
    surface = Color(0xFF2A2B3D),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

/**
 * Cores específicas para componentes da interface
 */
object AppColors {
    // Cor da barra de navegação inferior
    val BottomNavBackground = Color(0xFF252838)
    
    // Cor dos ícones na barra de navegação
    val IconTint = Color(0xFF808080)
    
    // Cores para o efeito de brilho do botão de adicionar
    val AddButtonGlow = Color(0xFF5C6AFF)
    val AddButtonBackground = Color(0xFF5C6AFF)
}
