package org.app.wakeupalarm.util

import org.app.wakeupalarm.domain.model.DayOfWeek
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSCalendar
import platform.Foundation.NSDateComponents
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.NSDate

/**
 * Implementação iOS para formatação de tempo
 */
actual class TimeFormatter {
    /**
     * Formata minutos desde a meia-noite para um formato de hora legível
     */
    actual fun formatMinutesToTime(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return "${hours.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}"
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
     * Usando APIs nativas do iOS
     */
    fun getFullDayName(day: DayOfWeek): String {
        val calendar = NSCalendar.currentCalendar
        val components = NSDateComponents()
        components.setWeekday(when (day) {
            DayOfWeek.SUNDAY -> 1
            DayOfWeek.MONDAY -> 2
            DayOfWeek.TUESDAY -> 3
            DayOfWeek.WEDNESDAY -> 4
            DayOfWeek.THURSDAY -> 5
            DayOfWeek.FRIDAY -> 6
            DayOfWeek.SATURDAY -> 7
        }.toLong())
        
        val date = calendar.dateFromComponents(components) ?: NSDate()
        
        val formatter = NSDateFormatter()
        formatter.setDateFormat("EEEE")
        formatter.setLocale(NSLocale.currentLocale)
        
        return formatter.stringFromDate(date)
    }
}
