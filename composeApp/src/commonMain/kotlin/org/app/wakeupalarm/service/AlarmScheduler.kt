package org.app.wakeupalarm.service

import org.app.wakeupalarm.domain.model.Alarm

/**
 * Interface para o agendador de alarmes
 * Esta interface define as operações comuns para agendar e cancelar alarmes
 * Implementações específicas para cada plataforma serão fornecidas
 */
interface AlarmScheduler {
    /**
     * Agenda um alarme para tocar no horário especificado
     * @param alarm O alarme a ser agendado
     */
    fun scheduleAlarm(alarm: Alarm)
    
    /**
     * Cancela um alarme agendado
     * @param alarmId O ID do alarme a ser cancelado
     */
    fun cancelAlarm(alarmId: String)
    
    /**
     * Cancela todos os alarmes agendados
     */
    fun cancelAllAlarms()
    
    /**
     * Verifica se um alarme está agendado
     * @param alarmId O ID do alarme a verificar
     * @return true se o alarme estiver agendado, false caso contrário
     */
    fun isAlarmScheduled(alarmId: String): Boolean
}

/**
 * Função para obter a implementação específica da plataforma do agendador de alarmes
 * @return Uma instância de AlarmScheduler específica para a plataforma
 */
expect fun getAlarmScheduler(): AlarmScheduler
