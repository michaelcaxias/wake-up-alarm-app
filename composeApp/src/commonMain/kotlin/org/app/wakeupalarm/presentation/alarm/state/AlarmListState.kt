package org.app.wakeupalarm.presentation.alarm.state

/**
 * Estado da UI para a lista de alarmes
 */
data class AlarmListState(
    val alarms: List<AlarmUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Modelo de UI para representação de um alarme na interface
 */
data class AlarmUiModel(
    val id: String,
    val timeFormatted: String,
    val label: String,
    val isEnabled: Boolean,
    val repeatDaysFormatted: String
)
