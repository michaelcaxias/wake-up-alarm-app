package org.app.wakeupalarm.data.source

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.app.wakeupalarm.data.mapper.AlarmEntity
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSJSONSerialization
import platform.Foundation.NSJSONWritingPrettyPrinted
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.dataUsingEncoding
import platform.Foundation.stringWithCString
import platform.Foundation.NSMutableDictionary
import platform.Foundation.NSMutableArray
import platform.Foundation.NSNumber

/**
 * Interface para fonte de dados de alarmes para iOS
 */
actual interface AlarmLocalDataSource {
    actual fun getAlarms(): Flow<List<AlarmEntity>>
    actual suspend fun getAlarmById(id: String): AlarmEntity?
    actual suspend fun saveAlarm(alarm: AlarmEntity)
    actual suspend fun deleteAlarm(id: String)
    actual suspend fun updateAlarmStatus(id: String, isEnabled: Boolean)
}

/**
 * Implementação da fonte de dados de alarmes usando NSUserDefaults
 * Esta é uma implementação simples para demonstração. Em um app real,
 * você provavelmente usaria CoreData ou outro mecanismo de persistência.
 */
class UserDefaultsAlarmDataSource : AlarmLocalDataSource {
    
    private val userDefaults = NSUserDefaults.standardUserDefaults
    private val alarmsFlow = MutableStateFlow<List<AlarmEntity>>(emptyList())
    
    init {
        loadAlarmsFromUserDefaults()
    }
    
    private fun loadAlarmsFromUserDefaults() {
        val alarmsJson = userDefaults.stringForKey(KEY_ALARMS) ?: "[]"
        val alarms = parseAlarmsFromJson(alarmsJson)
        alarmsFlow.value = alarms
    }
    
    private fun saveAlarmsToUserDefaults(alarms: List<AlarmEntity>) {
        val alarmsJson = convertAlarmsToJson(alarms)
        userDefaults.setObject(alarmsJson, KEY_ALARMS)
        userDefaults.synchronize()
        alarmsFlow.value = alarms
    }
    
    override fun getAlarms(): Flow<List<AlarmEntity>> = alarmsFlow
    
    override suspend fun getAlarmById(id: String): AlarmEntity? {
        return alarmsFlow.value.find { it.id == id }
    }
    
    override suspend fun saveAlarm(alarm: AlarmEntity) {
        val currentAlarms = alarmsFlow.value.toMutableList()
        val existingIndex = currentAlarms.indexOfFirst { it.id == alarm.id }
        
        if (existingIndex >= 0) {
            // Atualizar alarme existente
            currentAlarms[existingIndex] = alarm
        } else {
            // Adicionar novo alarme
            currentAlarms.add(alarm)
        }
        
        saveAlarmsToUserDefaults(currentAlarms)
    }
    
    override suspend fun deleteAlarm(id: String) {
        val currentAlarms = alarmsFlow.value.toMutableList()
        currentAlarms.removeAll { it.id == id }
        saveAlarmsToUserDefaults(currentAlarms)
    }
    
    override suspend fun updateAlarmStatus(id: String, isEnabled: Boolean) {
        val currentAlarms = alarmsFlow.value.toMutableList()
        val existingIndex = currentAlarms.indexOfFirst { it.id == id }
        
        if (existingIndex >= 0) {
            val alarm = currentAlarms[existingIndex]
            currentAlarms[existingIndex] = alarm.copy(isEnabled = isEnabled)
            saveAlarmsToUserDefaults(currentAlarms)
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    private fun parseAlarmsFromJson(json: String): List<AlarmEntity> {
        val result = mutableListOf<AlarmEntity>()
        try {
            val data = json.dataUsingEncoding(NSUTF8StringEncoding) ?: return emptyList()
            val jsonArray = NSJSONSerialization.JSONObjectWithData(data, 0, null) as? List<Map<Any?, Any?>> ?: return emptyList()
            
            for (alarmMap in jsonArray) {
                val repeatDaysArray = alarmMap["repeatDays"] as? List<Int> ?: emptyList()
                
                val alarm = AlarmEntity(
                    id = (alarmMap["id"] as? String) ?: "",
                    timeInMinutes = (alarmMap["timeInMinutes"] as? Int) ?: 0,
                    label = (alarmMap["label"] as? String) ?: "",
                    isEnabled = (alarmMap["isEnabled"] as? Boolean) ?: false,
                    repeatDays = repeatDaysArray,
                    soundUri = (alarmMap["soundUri"] as? String) ?: "",
                    vibrate = (alarmMap["vibrate"] as? Boolean) ?: false
                )
                result.add(alarm)
            }
        } catch (e: Exception) {
            println("Error parsing alarms: $e")
        }
        return result
    }
    
    private fun convertAlarmsToJson(alarms: List<AlarmEntity>): String {
        val jsonArray = NSMutableArray()
        
        for (alarm in alarms) {
            val alarmDict = NSMutableDictionary()
            alarmDict.setObject(alarm.id, "id")
            alarmDict.setObject(NSNumber(alarm.timeInMinutes), "timeInMinutes")
            alarmDict.setObject(alarm.label, "label")
            alarmDict.setObject(if (alarm.isEnabled) 1 else 0, "isEnabled")
            
            val repeatDaysArray = NSMutableArray()
            for (day in alarm.repeatDays) {
                repeatDaysArray.addObject(NSNumber(day))
            }
            alarmDict.setObject(repeatDaysArray, "repeatDays")
            
            alarmDict.setObject(alarm.soundUri, "soundUri")
            alarmDict.setObject(if (alarm.vibrate) 1 else 0, "vibrate")
            
            jsonArray.addObject(alarmDict)
        }
        
        val data = NSJSONSerialization.dataWithJSONObject(jsonArray, NSJSONWritingPrettyPrinted, null)
        return NSString.create(data = data!!, encoding = NSUTF8StringEncoding) as String
    }
    
    companion object {
        private const val KEY_ALARMS = "wake_up_alarm_alarms"
    }
}
