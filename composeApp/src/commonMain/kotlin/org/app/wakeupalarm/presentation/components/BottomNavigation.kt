package org.app.wakeupalarm.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.app.wakeupalarm.presentation.theme.AppColors

/**
 * Componente de navegação inferior com botão central destacado
 * 
 * @param onAddClick Callback chamado quando o botão de adicionar é clicado
 * @param onListClick Callback chamado quando o ícone de lista é clicado
 * @param onSettingsClick Callback chamado quando o ícone de configurações é clicado
 */
@Composable
fun BottomNavigationBar(
    onAddClick: () -> Unit,
    onListClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Barra de navegação com fundo cinza diferenciado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    color = AppColors.BottomNavBackground
                )
        ) {
            // Row com os ícones de navegação
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ícone de lista à esquerda (cinza)
                IconButton(
                    onClick = onListClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.List,
                        contentDescription = "List",
                        tint = AppColors.IconTint,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                // Espaço para o botão central
                Spacer(modifier = Modifier.width(48.dp))
                
                // Ícone de configurações à direita (cinza)
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = AppColors.IconTint,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
        
        // Botão central com efeito de brilho
        Box(
            modifier = Modifier
                .offset(y = (-25).dp)
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Efeito de brilho/glow azul
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AppColors.AddButtonGlow.copy(alpha = 0.6f),
                                AppColors.AddButtonGlow.copy(alpha = 0.0f)
                            )
                        ),
                        shape = CircleShape
                    )
            )
            
            // Botão azul central
            FloatingActionButton(
                onClick = onAddClick,
                backgroundColor = AppColors.AddButtonBackground,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add Alarm",
                    tint = Color.White
                )
            }
        }
    }
}
