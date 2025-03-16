package org.app.wakeupalarm.presentation.alarm.viewmodel

import kotlinx.coroutines.flow.StateFlow
import org.app.wakeupalarm.domain.model.Alarm
import org.app.wakeupalarm.domain.model.DayOfWeek
import org.app.wakeupalarm.presentation.alarm.state.AlarmListState

/**
 * Interface para o ViewModel da tela de lista de alarmes
 */
interface AlarmListViewModel {
    /**
     * Estado atual da lista de alarmes
     */
    val state: StateFlow<AlarmListState>
    
    /**
     * Alterna o status de um alarme (ativado/desativado)
     */
    fun toggleAlarmStatus(id: String, isEnabled: Boolean)
    
    /**
     * Remove um alarme
     */
    fun deleteAlarm(id: String)
    
    /**
     * Atualiza um alarme existente
     */
    fun updateAlarm(alarm: Alarm)
    
    /**
     * Obtém os dias de repetição de um alarme
     */
    fun getAlarmRepeatDays(id: String): List<DayOfWeek>
    
    /**
     * Atualiza a lista de alarmes
     */
    fun refresh()
}
