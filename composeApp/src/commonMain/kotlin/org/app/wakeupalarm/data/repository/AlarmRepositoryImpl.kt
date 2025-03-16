package org.app.wakeupalarm.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.app.wakeupalarm.data.mapper.toDomain
import org.app.wakeupalarm.data.mapper.toEntity
import org.app.wakeupalarm.data.source.AlarmLocalDataSource
import org.app.wakeupalarm.domain.model.Alarm
import org.app.wakeupalarm.domain.repository.AlarmRepository

/**
 * Implementação do repositório de alarmes
 */
class AlarmRepositoryImpl(
    private val localDataSource: AlarmLocalDataSource
) : AlarmRepository {
    
    override fun getAlarms(): Flow<List<Alarm>> = 
        localDataSource.getAlarms().map { alarms ->
            alarms.map { it.toDomain() }
        }
    
    override suspend fun getAlarmById(id: String): Alarm? =
        localDataSource.getAlarmById(id)?.toDomain()
    
    override suspend fun saveAlarm(alarm: Alarm) =
        localDataSource.saveAlarm(alarm.toEntity())
    
    override suspend fun deleteAlarm(id: String) =
        localDataSource.deleteAlarm(id)
    
    override suspend fun updateAlarmStatus(id: String, isEnabled: Boolean) =
        localDataSource.updateAlarmStatus(id, isEnabled)
}
