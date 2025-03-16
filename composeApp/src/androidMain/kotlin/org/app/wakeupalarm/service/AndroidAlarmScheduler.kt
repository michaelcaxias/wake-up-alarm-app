package org.app.wakeupalarm.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import org.app.wakeupalarm.domain.model.Alarm
import org.app.wakeupalarm.receiver.AlarmReceiver
import java.util.Calendar

/**
 * Implementação do agendador de alarmes para Android
 * Utiliza o AlarmManager do Android para agendar alarmes
 */
class AndroidAlarmScheduler(private val context: Context) : AlarmScheduler {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    override fun scheduleAlarm(alarm: Alarm) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.timeInMinutes / 60)
            set(Calendar.MINUTE, alarm.timeInMinutes % 60)
            set(Calendar.SECOND, 0)
            
            // Se o horário já passou hoje, agendar para amanhã
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        
        // Criar intent para o AlarmReceiver
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarm.id)
            putExtra(AlarmReceiver.EXTRA_ALARM_LABEL, alarm.label)
            putExtra(AlarmReceiver.EXTRA_ALARM_SOUND_URI, alarm.soundUri)
            putExtra(AlarmReceiver.EXTRA_ALARM_VIBRATE, alarm.vibrate)
        }
        
        // Criar PendingIntent
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Agendar o alarme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
    
    override fun cancelAlarm(alarmId: String) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }
    
    override fun cancelAllAlarms() {
        // Implementação simplificada - em um app real, você manteria um registro de todos os alarmes
        // e os cancelaria individualmente
    }
    
    override fun isAlarmScheduled(alarmId: String): Boolean {
        val intent = Intent(context, AlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            alarmId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) != null
    }
}

/**
 * Função para obter a implementação do agendador de alarmes para Android
 * 
 * Nota: Esta implementação requer um contexto Android válido.
 * O contexto deve ser fornecido pela camada de UI ou Activity/Application.
 */
actual fun getAlarmScheduler(): AlarmScheduler {
    // Retornar uma implementação vazia que será substituída em tempo de execução
    // pelo código que tem acesso ao contexto Android
    return object : AlarmScheduler {
        override fun scheduleAlarm(alarm: Alarm) {
            println("Android: Alarm scheduler not initialized with context")
        }
        
        override fun cancelAlarm(alarmId: String) {
            println("Android: Alarm scheduler not initialized with context")
        }
        
        override fun cancelAllAlarms() {
            println("Android: Alarm scheduler not initialized with context")
        }
        
        override fun isAlarmScheduled(alarmId: String): Boolean {
            println("Android: Alarm scheduler not initialized with context")
            return false
        }
    }
}

/**
 * Cria uma instância do agendador de alarmes para Android com um contexto válido
 * Esta função deve ser chamada pela camada de UI que tem acesso ao contexto Android
 */
fun createAndroidAlarmScheduler(context: Context): AlarmScheduler {
    return AndroidAlarmScheduler(context)
}
