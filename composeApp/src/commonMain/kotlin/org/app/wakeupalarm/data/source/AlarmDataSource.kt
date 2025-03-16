package org.app.wakeupalarm.data.source

import kotlinx.coroutines.flow.Flow
import org.app.wakeupalarm.data.mapper.AlarmEntity

/**
 * Interface para fonte de dados de alarmes
 * Será implementada de forma específica para cada plataforma
 */
expect interface AlarmLocalDataSource {
    /**
     * Obtém todos os alarmes como um Flow
     */
    fun getAlarms(): Flow<List<AlarmEntity>>
    
    /**
     * Obtém um alarme específico pelo ID
     */
    suspend fun getAlarmById(id: String): AlarmEntity?
    
    /**
     * Salva um alarme (novo ou atualização)
     */
    suspend fun saveAlarm(alarm: AlarmEntity)
    
    /**
     * Remove um alarme pelo ID
     */
    suspend fun deleteAlarm(id: String)
    
    /**
     * Atualiza o status de um alarme (ativado/desativado)
     */
    suspend fun updateAlarmStatus(id: String, isEnabled: Boolean)
}
