package org.app.wakeupalarm.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Componente para seleção de hora
 */
@Composable
fun TimePickerComponent(
    timeInMinutes: Int,
    onTimeChanged: (Int) -> Unit
) {
    val hours = timeInMinutes / 60
    val minutes = timeInMinutes % 60
    
    var selectedHours by remember { mutableStateOf(hours) }
    var selectedMinutes by remember { mutableStateOf(minutes) }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Horário do Alarme",
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Seletor de Horas
            NumberSelector(
                value = selectedHours,
                range = 0..23,
                onValueChange = { newHours ->
                    selectedHours = newHours
                    onTimeChanged(newHours * 60 + selectedMinutes)
                }
            )
            
            Text(
                text = ":",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            
            // Seletor de Minutos
            NumberSelector(
                value = selectedMinutes,
                range = 0..59,
                onValueChange = { newMinutes ->
                    selectedMinutes = newMinutes
                    onTimeChanged(selectedHours * 60 + newMinutes)
                }
            )
        }
    }
}

/**
 * Componente para seleção de número (horas ou minutos)
 */
@Composable
private fun NumberSelector(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botão para incrementar
        androidx.compose.material.IconButton(
            onClick = {
                val newValue = if (value >= range.last) range.first else value + 1
                onValueChange(newValue)
            }
        ) {
            Text(
                text = "▲",
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Valor atual
        Text(
            text = String.format("%02d", value),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Botão para decrementar
        androidx.compose.material.IconButton(
            onClick = {
                val newValue = if (value <= range.first) range.last else value - 1
                onValueChange(newValue)
            }
        ) {
            Text(
                text = "▼",
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
