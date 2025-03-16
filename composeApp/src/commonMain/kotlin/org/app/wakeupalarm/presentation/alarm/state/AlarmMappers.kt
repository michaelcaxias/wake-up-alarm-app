package org.app.wakeupalarm.presentation.alarm.state

import org.app.wakeupalarm.domain.model.Alarm
import org.app.wakeupalarm.domain.model.DayOfWeek
import org.app.wakeupalarm.util.TimeFormatter

/**
 * Mapeia um modelo de domínio para um modelo de UI
 */
fun Alarm.toUiModel(timeFormatter: TimeFormatter): AlarmUiModel = AlarmUiModel(
    id = id,
    timeFormatted = timeFormatter.formatMinutesToTime(timeInMinutes),
    label = label,
    isEnabled = isEnabled,
    repeatDaysFormatted = formatRepeatDays(repeatDays, timeFormatter)
)

/**
 * Formata os dias de repetição para exibição na UI
 */
fun formatRepeatDays(days: List<DayOfWeek>, timeFormatter: TimeFormatter): String {
    if (days.isEmpty()) return "Uma vez"
    
    if (days.size == 7) return "Todos os dias"
    
    if (days.containsAll(listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, 
                               DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)) &&
        !days.contains(DayOfWeek.SATURDAY) && !days.contains(DayOfWeek.SUNDAY)) {
        return "Dias úteis"
    }
    
    if (days.containsAll(listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)) && 
        days.size == 2) {
        return "Fins de semana"
    }
    
    return days.joinToString(", ") { timeFormatter.formatDayOfWeek(it) }
}
