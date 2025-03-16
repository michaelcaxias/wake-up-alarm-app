package org.app.wakeupalarm.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.wakeupalarm.presentation.alarm.state.AlarmUiModel

/**
 * Card que representa um alarme na lista
 * 
 * @param alarm Dados do alarme para exibição
 * @param onToggle Callback chamado quando o estado do alarme é alterado
 * @param index Índice do alarme na lista (para estilo visual)
 * @param onEdit Callback chamado ao clicar no botão de edição
 * @param onDelete Callback chamado ao clicar no botão de exclusão
 */
@Composable
fun AlarmCard(
    alarm: AlarmUiModel, 
    onToggle: (Boolean) -> Unit, 
    index: Int,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    // Cores baseadas no status do alarme (ativo = rosa, inativo = cinza escuro)
    val colors = if (alarm.isEnabled) {
        listOf(Color(0xFFFF4081), Color(0xFFFF7B9C)) // Pink gradient para alarmes ativos
    } else {
        listOf(Color(0xFF2A2B3D), Color(0xFF2A2B3D)) // Dark gray para alarmes inativos
    }
    
    // Primeiro alarme sempre tem texto branco, outros alarmes têm texto com opacidade baseada no status
    val textColor = if (index == 0) Color.White else Color.White.copy(alpha = if (alarm.isEnabled) 1f else 0.5f)
    
    // Primeiro alarme tem texto maior
    val timeSize = if (index == 0) 48.sp else 40.sp
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = 0.dp,
        backgroundColor = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(colors),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = alarm.label,
                        fontSize = 16.sp,
                        color = textColor
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Botões de editar e remover
                        IconButton(
                            onClick = { onEdit() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Editar alarme",
                                tint = textColor.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        IconButton(
                            onClick = { onDelete() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Remover alarme",
                                tint = textColor.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        Switch(
                            checked = alarm.isEnabled,
                            onCheckedChange = onToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4A5CFF).copy(alpha = 0.5f),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = alarm.timeFormatted,
                            fontSize = timeSize,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        
                        // Formato 24h - sem necessidade de mostrar AM/PM
                    }
                    
                    // Mostrar tempo restante para todos os alarmes ativos e para o primeiro alarme
                    if (index == 0 || alarm.isEnabled) {
                        Text(
                            text = "8h 13m",
                            fontSize = 16.sp,
                            color = textColor
                        )
                    }
                }
                
                Text(
                    text = alarm.repeatDaysFormatted,
                    fontSize = 14.sp,
                    color = textColor.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = if (index == 0 || alarm.isEnabled) 8.dp else 0.dp)
                )
            }
        }
    }
}
