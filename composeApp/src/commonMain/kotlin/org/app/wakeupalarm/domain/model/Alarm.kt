package org.app.wakeupalarm.domain.model

/**
 * Representa um alarme no domínio da aplicação
 */
data class Alarm(
    val id: String,
    val timeInMinutes: Int,  // Minutos desde meia-noite
    val label: String,
    val isEnabled: Boolean,
    val repeatDays: List<DayOfWeek>,
    val soundUri: String,
    val vibrate: Boolean
)

/**
 * Dias da semana para repetição do alarme
 */
enum class DayOfWeek {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
}
