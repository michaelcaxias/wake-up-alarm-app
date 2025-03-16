package org.app.wakeupalarm.util

import org.app.wakeupalarm.domain.model.DayOfWeek
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Implementação Android para formatação de tempo
 */
actual class TimeFormatter {
    /**
     * Formata minutos desde a meia-noite para um formato de hora legível
     */
    actual fun formatMinutesToTime(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return String.format("%02d:%02d", hours, mins)
    }
    
    /**
     * Formata um dia da semana para sua representação localizada
     */
    actual fun formatDayOfWeek(day: DayOfWeek): String {
        return when (day) {
            DayOfWeek.SUNDAY -> "Dom"
            DayOfWeek.MONDAY -> "Seg"
            DayOfWeek.TUESDAY -> "Ter"
            DayOfWeek.WEDNESDAY -> "Qua"
            DayOfWeek.THURSDAY -> "Qui"
            DayOfWeek.FRIDAY -> "Sex"
            DayOfWeek.SATURDAY -> "Sáb"
        }
    }
    
    /**
     * Método auxiliar para obter o nome completo do dia da semana
     */
    fun getFullDayName(day: DayOfWeek): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, when (day) {
            DayOfWeek.SUNDAY -> Calendar.SUNDAY
            DayOfWeek.MONDAY -> Calendar.MONDAY
            DayOfWeek.TUESDAY -> Calendar.TUESDAY
            DayOfWeek.WEDNESDAY -> Calendar.WEDNESDAY
            DayOfWeek.THURSDAY -> Calendar.THURSDAY
            DayOfWeek.FRIDAY -> Calendar.FRIDAY
            DayOfWeek.SATURDAY -> Calendar.SATURDAY
        })
        
        val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        return dateFormat.format(calendar.time).capitalize(Locale.getDefault())
    }
}
