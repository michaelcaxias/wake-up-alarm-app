package org.app.wakeupalarm.presentation.alarm.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.app.wakeupalarm.domain.model.DayOfWeek
import java.util.UUID

/**
 * ViewModel simplificado para a tela de edição de alarme
 */
class SimpleAlarmEditViewModel {
    private val _state = MutableStateFlow(
        AlarmEditState(
            id = UUID.randomUUID().toString(),
            timeInMinutes = 7 * 60,
            label = "New Alarm",
            repeatDays = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
            soundUri = "default",
            vibrate = true,
            isEditMode = false,
            isLoading = false
        )
    )
    
    val state: StateFlow<AlarmEditState> = _state.asStateFlow()
    
    fun updateTime(timeInMinutes: Int) {
        _state.value = _state.value.copy(timeInMinutes = timeInMinutes)
    }
    
    fun updateLabel(label: String) {
        _state.value = _state.value.copy(label = label)
    }
    
    fun updateRepeatDays(repeatDays: List<DayOfWeek>) {
        _state.value = _state.value.copy(repeatDays = repeatDays)
    }
    
    fun updateSound(soundUri: String) {
        _state.value = _state.value.copy(soundUri = soundUri)
    }
    
    fun updateVibrate(vibrate: Boolean) {
        _state.value = _state.value.copy(vibrate = vibrate)
    }
    
    fun saveAlarm() {
        // Simulação de salvamento de alarme
        // Em um caso real, chamaria o usecase para salvar no repositório
    }
    
    fun loadAlarm(id: String) {
        // Simulação de carregamento de alarme
        // Em um caso real, chamaria o usecase para carregar do repositório
    }
}
