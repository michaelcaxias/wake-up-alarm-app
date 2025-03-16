package org.app.wakeupalarm.domain.usecase

import org.app.wakeupalarm.domain.model.Alarm
import org.app.wakeupalarm.service.AlarmScheduler

/**
 * Caso de uso para agendar um alarme
 * Coordena a lógica de negócios para agendar um alarme
 */
class ScheduleAlarmUseCase(private val alarmScheduler: AlarmScheduler) {
    
    /**
     * Agenda um alarme para tocar no horário especificado
     * @param alarm O alarme a ser agendado
     */
    fun execute(alarm: Alarm) {
        // Verificar se o alarme está habilitado
        if (alarm.isEnabled) {
            // Cancelar qualquer alarme existente com o mesmo ID
            alarmScheduler.cancelAlarm(alarm.id)
            
            // Agendar o novo alarme
            alarmScheduler.scheduleAlarm(alarm)
        } else {
            // Se o alarme estiver desabilitado, cancelá-lo
            alarmScheduler.cancelAlarm(alarm.id)
        }
    }
}
