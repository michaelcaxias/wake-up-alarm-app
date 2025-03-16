package org.app.wakeupalarm.domain.repository

import kotlinx.coroutines.flow.Flow
import org.app.wakeupalarm.domain.model.Alarm

/**
 * Interface do repositório para gerenciamento de alarmes
 */
interface AlarmRepository {
    /**
     * Obtém todos os alarmes como um Flow
     */
    fun getAlarms(): Flow<List<Alarm>>
    
    /**
     * Obtém um alarme específico pelo ID
     */
    suspend fun getAlarmById(id: String): Alarm?
    
    /**
     * Salva um alarme (novo ou atualização)
     */
    suspend fun saveAlarm(alarm: Alarm)
    
    /**
     * Remove um alarme pelo ID
     */
    suspend fun deleteAlarm(id: String)
    
    /**
     * Atualiza o status de um alarme (ativado/desativado)
     */
    suspend fun updateAlarmStatus(id: String, isEnabled: Boolean)
}
