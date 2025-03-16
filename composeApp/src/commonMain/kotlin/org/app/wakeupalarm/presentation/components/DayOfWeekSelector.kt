package org.app.wakeupalarm.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.app.wakeupalarm.domain.model.DayOfWeek

/**
 * Componente para seleção dos dias da semana
 */
@Composable
fun DayOfWeekSelector(
    selectedDays: List<DayOfWeek>,
    onDaysSelected: (List<DayOfWeek>) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Domingo
        DayButton(
            day = DayOfWeek.SUNDAY,
            label = "D",
            isSelected = selectedDays.contains(DayOfWeek.SUNDAY),
            onClick = {
                val newList = selectedDays.toMutableList()
                if (newList.contains(DayOfWeek.SUNDAY)) {
                    newList.remove(DayOfWeek.SUNDAY)
                } else {
                    newList.add(DayOfWeek.SUNDAY)
                }
                onDaysSelected(newList)
            }
        )
        
        // Segunda
        DayButton(
            day = DayOfWeek.MONDAY,
            label = "S",
            isSelected = selectedDays.contains(DayOfWeek.MONDAY),
            onClick = {
                val newList = selectedDays.toMutableList()
                if (newList.contains(DayOfWeek.MONDAY)) {
                    newList.remove(DayOfWeek.MONDAY)
                } else {
                    newList.add(DayOfWeek.MONDAY)
                }
                onDaysSelected(newList)
            }
        )
        
        // Terça
        DayButton(
            day = DayOfWeek.TUESDAY,
            label = "T",
            isSelected = selectedDays.contains(DayOfWeek.TUESDAY),
            onClick = {
                val newList = selectedDays.toMutableList()
                if (newList.contains(DayOfWeek.TUESDAY)) {
                    newList.remove(DayOfWeek.TUESDAY)
                } else {
                    newList.add(DayOfWeek.TUESDAY)
                }
                onDaysSelected(newList)
            }
        )
        
        // Quarta
        DayButton(
            day = DayOfWeek.WEDNESDAY,
            label = "Q",
            isSelected = selectedDays.contains(DayOfWeek.WEDNESDAY),
            onClick = {
                val newList = selectedDays.toMutableList()
                if (newList.contains(DayOfWeek.WEDNESDAY)) {
                    newList.remove(DayOfWeek.WEDNESDAY)
                } else {
                    newList.add(DayOfWeek.WEDNESDAY)
                }
                onDaysSelected(newList)
            }
        )
        
        // Quinta
        DayButton(
            day = DayOfWeek.THURSDAY,
            label = "Q",
            isSelected = selectedDays.contains(DayOfWeek.THURSDAY),
            onClick = {
                val newList = selectedDays.toMutableList()
                if (newList.contains(DayOfWeek.THURSDAY)) {
                    newList.remove(DayOfWeek.THURSDAY)
                } else {
                    newList.add(DayOfWeek.THURSDAY)
                }
                onDaysSelected(newList)
            }
        )
        
        // Sexta
        DayButton(
            day = DayOfWeek.FRIDAY,
            label = "S",
            isSelected = selectedDays.contains(DayOfWeek.FRIDAY),
            onClick = {
                val newList = selectedDays.toMutableList()
                if (newList.contains(DayOfWeek.FRIDAY)) {
                    newList.remove(DayOfWeek.FRIDAY)
                } else {
                    newList.add(DayOfWeek.FRIDAY)
                }
                onDaysSelected(newList)
            }
        )
        
        // Sábado
        DayButton(
            day = DayOfWeek.SATURDAY,
            label = "S",
            isSelected = selectedDays.contains(DayOfWeek.SATURDAY),
            onClick = {
                val newList = selectedDays.toMutableList()
                if (newList.contains(DayOfWeek.SATURDAY)) {
                    newList.remove(DayOfWeek.SATURDAY)
                } else {
                    newList.add(DayOfWeek.SATURDAY)
                }
                onDaysSelected(newList)
            }
        )
    }
}

/**
 * Botão para seleção de dia da semana
 */
@Composable
private fun DayButton(
    day: DayOfWeek,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Substituindo Surface com onClick (API experimental) por Box com clickable modifier
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) MaterialTheme.colors.primary 
                else MaterialTheme.colors.surface
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(8.dp),
            color = if (isSelected) MaterialTheme.colors.onPrimary 
                   else MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}
