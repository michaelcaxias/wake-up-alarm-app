package org.app.wakeupalarm

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.app.wakeupalarm.domain.model.DayOfWeek
import org.app.wakeupalarm.presentation.alarm.AlarmListScreen
import org.app.wakeupalarm.presentation.alarm.state.AlarmListState
import org.app.wakeupalarm.presentation.alarm.state.AlarmUiModel
import org.app.wakeupalarm.presentation.alarm.viewmodel.AlarmListViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Implementação simples do ViewModel para demonstração
 */
class SimpleAlarmListViewModel : AlarmListViewModel {
    private val _state = MutableStateFlow(
        AlarmListState(
            alarms = listOf(
                AlarmUiModel(
                    id = "1",
                    timeFormatted = "07:00",
                    label = "Acordar",
                    isEnabled = true,
                    repeatDaysFormatted = "Dias úteis"
                ),
                AlarmUiModel(
                    id = "2",
                    timeFormatted = "08:30",
                    label = "Tomar remédio",
                    isEnabled = true,
                    repeatDaysFormatted = "Seg, Qua, Sex"
                ),
                AlarmUiModel(
                    id = "3",
                    timeFormatted = "09:45",
                    label = "Fim de semana",
                    isEnabled = true,
                    repeatDaysFormatted = "Fins de semana"
                )
            ),
            isLoading = false
        )
    )
    
    override val state: StateFlow<AlarmListState> = _state
    
    override fun toggleAlarmStatus(id: String, isEnabled: Boolean) {
        val currentAlarms = _state.value.alarms.toMutableList()
        val index = currentAlarms.indexOfFirst { it.id == id }
        if (index >= 0) {
            val alarm = currentAlarms[index]
            currentAlarms[index] = alarm.copy(isEnabled = isEnabled)
            _state.value = _state.value.copy(alarms = currentAlarms)
        }
    }
    
    override fun deleteAlarm(id: String) {
        val currentAlarms = _state.value.alarms.toMutableList()
        currentAlarms.removeAll { it.id == id }
        _state.value = _state.value.copy(alarms = currentAlarms)
    }
    
    override fun refresh() {
        // Nada a fazer na implementação simples
    }
}

/**
 * Componente principal do aplicativo
 */
@Composable
@Preview
fun App() {
    // Usando um ViewModel simples para demonstração
    val viewModel = remember { SimpleAlarmListViewModel() }
    
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AlarmListScreen(
                viewModel = viewModel,
                onAddAlarm = { /* Implementar navegação para tela de adição */ },
                onEditAlarm = { /* Implementar navegação para tela de edição */ }
            )
        }
    }
}