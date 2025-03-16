package org.app.wakeupalarm.presentation.alarm.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.app.wakeupalarm.domain.model.Alarm
import org.app.wakeupalarm.domain.model.DayOfWeek
import org.app.wakeupalarm.domain.usecase.GetAlarmByIdUseCase
import org.app.wakeupalarm.domain.usecase.SaveAlarmUseCase
import java.util.UUID

/**
 * Estado da UI para a tela de edição de alarme
 */
data class AlarmEditState(
    val id: String = "",
    val timeInMinutes: Int = 8 * 60, // 8:00 AM por padrão
    val label: String = "",
    val isEnabled: Boolean = true,
    val repeatDays: List<DayOfWeek> = emptyList(),
    val soundUri: String = "",
    val vibrate: Boolean = true,
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para a tela de edição/adição de alarme
 */
class AlarmEditViewModel(
    private val getAlarmByIdUseCase: GetAlarmByIdUseCase,
    private val saveAlarmUseCase: SaveAlarmUseCase,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    private val alarmId: String? = null
) {
    private val _state = MutableStateFlow(AlarmEditState(isLoading = alarmId != null))
    val state: StateFlow<AlarmEditState> = _state.asStateFlow()
    
    init {
        if (alarmId != null) {
            loadAlarm(alarmId)
        }
    }
    
    private fun loadAlarm(id: String) {
        coroutineScope.launch {
            try {
                val alarm = getAlarmByIdUseCase(id)
                if (alarm != null) {
                    _state.value = AlarmEditState(
                        id = alarm.id,
                        timeInMinutes = alarm.timeInMinutes,
                        label = alarm.label,
                        isEnabled = alarm.isEnabled,
                        repeatDays = alarm.repeatDays,
                        soundUri = alarm.soundUri,
                        vibrate = alarm.vibrate,
                        isEditMode = true,
                        isLoading = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        error = "Alarme não encontrado",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }
    
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
        coroutineScope.launch {
            try {
                val currentState = _state.value
                val alarm = Alarm(
                    id = if (currentState.isEditMode) currentState.id else UUID.randomUUID().toString(),
                    timeInMinutes = currentState.timeInMinutes,
                    label = currentState.label,
                    isEnabled = currentState.isEnabled,
                    repeatDays = currentState.repeatDays,
                    soundUri = currentState.soundUri,
                    vibrate = currentState.vibrate
                )
                saveAlarmUseCase(alarm)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
}
