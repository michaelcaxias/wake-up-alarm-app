package org.app.wakeupalarm.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.app.wakeupalarm.presentation.alarm.viewmodel.AlarmListViewModel

/**
 * Componente que exibe a lista de alarmes
 * 
 * @param viewModel ViewModel que contém os dados dos alarmes
 * @param onEditAlarm Callback quando um alarme é selecionado para edição
 */
@Composable
fun AlarmList(viewModel: AlarmListViewModel, onEditAlarm: (String) -> Unit = {}) {
    val state by viewModel.state.collectAsState()
    
    Column(modifier = Modifier.fillMaxWidth()) {
        state.alarms.forEachIndexed { index, alarm ->
            AlarmCard(
                alarm = alarm,
                onToggle = { viewModel.toggleAlarmStatus(alarm.id, it) },
                index = index,
                onEdit = { 
                    // Navegar para a tela de edição do alarme selecionado usando o callback
                    onEditAlarm(alarm.id)
                },
                onDelete = { viewModel.deleteAlarm(alarm.id) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
