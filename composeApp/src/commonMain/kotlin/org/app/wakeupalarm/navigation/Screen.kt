package org.app.wakeupalarm.navigation

/**
 * Definição das telas do aplicativo para navegação
 */
sealed class Screen {
    object AlarmList : Screen()
    data class AlarmEdit(val alarmId: String?) : Screen()
}
