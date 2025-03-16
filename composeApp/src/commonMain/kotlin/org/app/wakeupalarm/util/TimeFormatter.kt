package org.app.wakeupalarm.util

import org.app.wakeupalarm.domain.model.DayOfWeek

/**
 * Classe utilitária para formatação de tempo
 * Implementação específica para cada plataforma
 */
expect class TimeFormatter() {
    /**
     * Formata minutos desde a meia-noite para um formato de hora legível
     */
    fun formatMinutesToTime(minutes: Int): String
    
    /**
     * Formata um dia da semana para sua representação localizada
     */
    fun formatDayOfWeek(day: DayOfWeek): String
}
