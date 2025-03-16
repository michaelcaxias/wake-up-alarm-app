package org.app.wakeupalarm.presentation.alarm.viewmodel

import kotlinx.coroutines.flow.StateFlow
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
     * Atualiza a lista de alarmes
     */
    fun refresh()
}
