package org.app.wakeupalarm.service

import org.app.wakeupalarm.domain.model.Alarm

/**
 * Implementação do agendador de alarmes para JVM (Desktop)
 * Esta é uma implementação simplificada que apenas registra os alarmes no console
 */
class JvmAlarmScheduler : AlarmScheduler {
    
    override fun scheduleAlarm(alarm: Alarm) {
        println("JVM: Scheduling alarm: ${alarm.id} at ${alarm.timeInMinutes / 60}:${alarm.timeInMinutes % 60} - ${alarm.label}")
    }
    
    override fun cancelAlarm(alarmId: String) {
        println("JVM: Cancelling alarm: $alarmId")
    }
    
    override fun cancelAllAlarms() {
        println("JVM: Cancelling all alarms")
    }
    
    override fun isAlarmScheduled(alarmId: String): Boolean {
        println("JVM: Checking if alarm is scheduled: $alarmId")
        return false
    }
}

/**
 * Função para obter a implementação do agendador de alarmes para JVM
 */
actual fun getAlarmScheduler(): AlarmScheduler {
    return JvmAlarmScheduler()
}
