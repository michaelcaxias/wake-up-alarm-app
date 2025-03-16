package org.app.wakeupalarm.data.mapper

import org.app.wakeupalarm.domain.model.Alarm
import org.app.wakeupalarm.domain.model.DayOfWeek

/**
 * Mapeia uma entidade de dados para um modelo de domínio
 */
fun AlarmEntity.toDomain(): Alarm = Alarm(
    id = id,
    timeInMinutes = timeInMinutes,
    label = label,
    isEnabled = isEnabled,
    repeatDays = repeatDays.map { DayOfWeek.values()[it] },
    soundUri = soundUri,
    vibrate = vibrate
)

/**
 * Mapeia um modelo de domínio para uma entidade de dados
 */
fun Alarm.toEntity(): AlarmEntity = AlarmEntity(
    id = id,
    timeInMinutes = timeInMinutes,
    label = label,
    isEnabled = isEnabled,
    repeatDays = repeatDays.map { it.ordinal },
    soundUri = soundUri,
    vibrate = vibrate
)
