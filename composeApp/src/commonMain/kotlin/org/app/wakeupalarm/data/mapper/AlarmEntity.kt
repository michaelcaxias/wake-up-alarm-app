package org.app.wakeupalarm.data.mapper

/**
 * Entidade de dados para representação de um alarme na camada de dados
 */
data class AlarmEntity(
    val id: String,
    val timeInMinutes: Int,
    val label: String,
    val isEnabled: Boolean,
    val repeatDays: List<Int>,
    val soundUri: String,
    val vibrate: Boolean
)
