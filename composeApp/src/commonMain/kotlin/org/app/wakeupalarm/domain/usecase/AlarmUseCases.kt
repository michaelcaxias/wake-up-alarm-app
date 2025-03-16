package org.app.wakeupalarm.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.app.wakeupalarm.domain.model.Alarm
import org.app.wakeupalarm.domain.repository.AlarmRepository

/**
 * Caso de uso para obter todos os alarmes
 */
class GetAlarmsUseCase(private val repository: AlarmRepository) {
    operator fun invoke(): Flow<List<Alarm>> = repository.getAlarms()
}

/**
 * Caso de uso para obter um alarme específico pelo ID
 */
class GetAlarmByIdUseCase(private val repository: AlarmRepository) {
    suspend operator fun invoke(id: String): Alarm? = repository.getAlarmById(id)
}

/**
 * Caso de uso para salvar um alarme
 */
class SaveAlarmUseCase(private val repository: AlarmRepository) {
    suspend operator fun invoke(alarm: Alarm) = repository.saveAlarm(alarm)
}

/**
 * Caso de uso para remover um alarme
 */
class DeleteAlarmUseCase(private val repository: AlarmRepository) {
    suspend operator fun invoke(id: String) = repository.deleteAlarm(id)
}

/**
 * Caso de uso para atualizar o status de um alarme
 */
class UpdateAlarmStatusUseCase(private val repository: AlarmRepository) {
    suspend operator fun invoke(id: String, isEnabled: Boolean) = 
        repository.updateAlarmStatus(id, isEnabled)
}
