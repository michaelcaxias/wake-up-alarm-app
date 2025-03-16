package org.app.wakeupalarm.service

import org.app.wakeupalarm.domain.model.Alarm

/**
 * Implementação do agendador de alarmes para iOS
 * Versão simplificada para evitar erros de compilação
 */
class IOSAlarmScheduler : AlarmScheduler {
    
    init {
        println("iOS Alarm Scheduler initialized")
    }
    
    override fun scheduleAlarm(alarm: Alarm) {
        // Implementação simplificada para evitar erros de compilação
        println("iOS: Scheduling alarm: ${alarm.id} at ${alarm.timeInMinutes / 60}:${alarm.timeInMinutes % 60} - ${alarm.label}")
        println("iOS: Sound: ${alarm.soundUri}, Vibrate: ${alarm.vibrate}")
    }
    
    override fun cancelAlarm(alarmId: String) {
        // Implementação simplificada para evitar erros de compilação
        println("iOS: Cancelling alarm: $alarmId")
    }
    
    override fun cancelAllAlarms() {
        // Implementação simplificada para evitar erros de compilação
        println("iOS: Cancelling all alarms")
    }
    
    override fun isAlarmScheduled(alarmId: String): Boolean {
        // Implementação simplificada para evitar erros de compilação
        println("iOS: Checking if alarm is scheduled: $alarmId")
        return false
    }
}

/**
 * Função para obter a implementação do agendador de alarmes para iOS
 */
actual fun getAlarmScheduler(): AlarmScheduler {
    return IOSAlarmScheduler()
}
