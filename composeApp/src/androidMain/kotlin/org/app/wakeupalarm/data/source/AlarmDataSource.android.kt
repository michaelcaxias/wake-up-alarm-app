package org.app.wakeupalarm.data.source

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.app.wakeupalarm.data.mapper.AlarmEntity
import org.json.JSONArray
import org.json.JSONObject

/**
 * Interface para fonte de dados de alarmes para Android
 */
actual interface AlarmLocalDataSource {
    actual fun getAlarms(): Flow<List<AlarmEntity>>
    actual suspend fun getAlarmById(id: String): AlarmEntity?
    actual suspend fun saveAlarm(alarm: AlarmEntity)
    actual suspend fun deleteAlarm(id: String)
    actual suspend fun updateAlarmStatus(id: String, isEnabled: Boolean)
}

/**
 * Implementação da fonte de dados de alarmes usando SharedPreferences
 * Esta é uma implementação simples para demonstração. Em um app real,
 * você provavelmente usaria Room Database ou outro mecanismo de persistência.
 */
class SharedPrefsAlarmDataSource(
    private val context: Context
) : AlarmLocalDataSource {
    
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    private val alarmsFlow = MutableStateFlow<List<AlarmEntity>>(emptyList())
    
    init {
        loadAlarmsFromPrefs()
    }
    
    private fun loadAlarmsFromPrefs() {
        val alarmsJson = sharedPreferences.getString(KEY_ALARMS, "[]") ?: "[]"
        val alarms = parseAlarmsFromJson(alarmsJson)
        alarmsFlow.value = alarms
    }
    
    private fun saveAlarmsToPrefs(alarms: List<AlarmEntity>) {
        val alarmsJson = convertAlarmsToJson(alarms)
        sharedPreferences.edit().putString(KEY_ALARMS, alarmsJson).apply()
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
        
        saveAlarmsToPrefs(currentAlarms)
    }
    
    override suspend fun deleteAlarm(id: String) {
        val currentAlarms = alarmsFlow.value.toMutableList()
        currentAlarms.removeAll { it.id == id }
        saveAlarmsToPrefs(currentAlarms)
    }
    
    override suspend fun updateAlarmStatus(id: String, isEnabled: Boolean) {
        val currentAlarms = alarmsFlow.value.toMutableList()
        val existingIndex = currentAlarms.indexOfFirst { it.id == id }
        
        if (existingIndex >= 0) {
            val alarm = currentAlarms[existingIndex]
            currentAlarms[existingIndex] = alarm.copy(isEnabled = isEnabled)
            saveAlarmsToPrefs(currentAlarms)
        }
    }
    
    private fun parseAlarmsFromJson(json: String): List<AlarmEntity> {
        val result = mutableListOf<AlarmEntity>()
        try {
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val alarmJson = jsonArray.getJSONObject(i)
                val repeatDaysJson = alarmJson.getJSONArray("repeatDays")
                val repeatDays = mutableListOf<Int>()
                
                for (j in 0 until repeatDaysJson.length()) {
                    repeatDays.add(repeatDaysJson.getInt(j))
                }
                
                val alarm = AlarmEntity(
                    id = alarmJson.getString("id"),
                    timeInMinutes = alarmJson.getInt("timeInMinutes"),
                    label = alarmJson.getString("label"),
                    isEnabled = alarmJson.getBoolean("isEnabled"),
                    repeatDays = repeatDays,
                    soundUri = alarmJson.getString("soundUri"),
                    vibrate = alarmJson.getBoolean("vibrate")
                )
                result.add(alarm)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
    
    private fun convertAlarmsToJson(alarms: List<AlarmEntity>): String {
        val jsonArray = JSONArray()
        
        for (alarm in alarms) {
            val alarmJson = JSONObject().apply {
                put("id", alarm.id)
                put("timeInMinutes", alarm.timeInMinutes)
                put("label", alarm.label)
                put("isEnabled", alarm.isEnabled)
                
                val repeatDaysJson = JSONArray()
                for (day in alarm.repeatDays) {
                    repeatDaysJson.put(day)
                }
                put("repeatDays", repeatDaysJson)
                
                put("soundUri", alarm.soundUri)
                put("vibrate", alarm.vibrate)
            }
            jsonArray.put(alarmJson)
        }
        
        return jsonArray.toString()
    }
    
    companion object {
        private const val PREFS_NAME = "wake_up_alarm_prefs"
        private const val KEY_ALARMS = "alarms"
    }
}
