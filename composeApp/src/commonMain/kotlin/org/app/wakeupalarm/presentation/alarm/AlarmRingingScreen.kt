package org.app.wakeupalarm.presentation.alarm

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

/**
 * Tela exibida quando o alarme está tocando
 * 
 * @param alarmLabel Rótulo do alarme que está tocando
 * @param onDismiss Callback chamado quando o usuário deseja parar o alarme
 */
@Composable
fun AlarmRingingScreen(
    alarmLabel: String,
    onDismiss: () -> Unit
) {
    // Cores e estilos consistentes com o resto do aplicativo
    val darkBackground = Color(0xFF1F2033)
    val accentColor = Color(0xFF4A5CFF)
    val gradientColors = listOf(
        Color(0xFF4A5CFF),
        Color(0xFF6B8CFF)
    )
    
    // Estado para a hora atual
    var currentTime by remember { mutableStateOf("") }
    
    // Atualizar a hora a cada segundo
    LaunchedEffect(Unit) {
        while (true) {
            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            currentTime = dateFormat.format(Date())
            delay(1000) // Atualizar a cada segundo
        }
    }
    
    // Animação de pulsação para o botão
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título do alarme
            Text(
                text = alarmLabel,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Hora atual
            Text(
                text = currentTime,
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 64.dp)
            )
            
            // Botão para parar o alarme
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(gradientColors)
                    )
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Stop Alarm",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Tap to stop",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Extensão para tornar um Modifier clicável
 */
fun Modifier.clickable(onClick: () -> Unit): Modifier = this.then(
    Modifier.padding(12.dp)
)
