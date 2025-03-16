package org.app.wakeupalarm.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.app.wakeupalarm.MainActivity
import org.app.wakeupalarm.R
import kotlin.random.Random

/**
 * Receptor de broadcast para alarmes
 * Responsável por tocar o som e vibrar quando um alarme é disparado
 */
class AlarmReceiver : BroadcastReceiver() {
    
    companion object {
        const val EXTRA_ALARM_ID = "alarm_id"
        const val EXTRA_ALARM_LABEL = "alarm_label"
        const val EXTRA_ALARM_SOUND_URI = "alarm_sound_uri"
        const val EXTRA_ALARM_VIBRATE = "alarm_vibrate"
        
        const val ACTION_STOP_ALARM = "org.app.wakeupalarm.STOP_ALARM"
        
        private const val NOTIFICATION_CHANNEL_ID = "alarm_channel"
        private const val NOTIFICATION_ID = 1
        private const val TAG = "AlarmReceiver"
        
        // Mapeamento de nomes de ringtones para URIs do sistema
        private val RINGTONE_MAP = mapOf(
            "morning!" to RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
            "Gentle Alarm" to RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
            "Sunrise" to RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),
            "Birdsong" to RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),
            "Ocean Waves" to RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        )
    }
    
    private var mediaPlayer: MediaPlayer? = null
    
    override fun onReceive(context: Context, intent: Intent) {
        // Verificar se é uma ação para parar o alarme
        if (intent.action == ACTION_STOP_ALARM) {
            // O ID do alarme não é usado aqui, mas poderia ser usado para cancelar alarmes específicos
            stopAlarmSound()
            return
        }
        
        val alarmId = intent.getStringExtra(EXTRA_ALARM_ID) ?: return
        val alarmLabel = intent.getStringExtra(EXTRA_ALARM_LABEL) ?: "Alarm"
        val soundUriString = intent.getStringExtra(EXTRA_ALARM_SOUND_URI) ?: "morning!"
        val shouldVibrate = intent.getBooleanExtra(EXTRA_ALARM_VIBRATE, true)
        
        Log.d("AlarmReceiver", "Alarm triggered: $alarmId, $alarmLabel")
        
        // Tocar o som do alarme
        playAlarmSound(context, soundUriString)
        
        // Vibrar se necessário
        if (shouldVibrate) {
            vibrate(context)
        }
        
        // Mostrar notificação
        showNotification(context, alarmId, alarmLabel)
    }
    
    private fun playAlarmSound(context: Context, soundName: String) {
        try {
            // Obter URI do som
            val soundUri = RINGTONE_MAP[soundName] ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            
            // Configurar e iniciar o MediaPlayer
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, soundUri)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                prepare()
                start()
            }
            
            // Configurar volume
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.setStreamVolume(
                AudioManager.STREAM_ALARM,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
                0
            )
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Error playing alarm sound", e)
        }
    }
    
    private fun vibrate(context: Context) {
        try {
            val vibrationPattern = longArrayOf(0, 500, 1000, 500, 1000, 500)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                
                vibrator.vibrate(
                    VibrationEffect.createWaveform(vibrationPattern, 0)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createWaveform(vibrationPattern, 0)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(vibrationPattern, 0)
                }
            }
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Error vibrating", e)
        }
    }
    
    private fun showNotification(context: Context, alarmId: String, alarmLabel: String) {
        // Criar intent para abrir o app quando a notificação for clicada
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_ALARM_ID, alarmId)
        }
        
        // Criar PendingIntent
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            flags
        )
        
        // Iniciar a AlarmRingingActivity
        val ringingIntent = Intent(context, org.app.wakeupalarm.AlarmRingingActivity::class.java).apply {
            // Definir flags para a intent para garantir que ela apareça sobre outras atividades
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            )
            // Passar os dados do alarme para a activity
            putExtra(EXTRA_ALARM_ID, alarmId)
            putExtra(EXTRA_ALARM_LABEL, alarmLabel)
        }
        
        try {
            // Tentar iniciar a activity
            context.startActivity(ringingIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao iniciar a tela de alarme: ${e.message}")
            // Se falhar, garantir que pelo menos a notificação seja exibida
        }
        
        // Criar notificação como fallback
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Alarm: $alarmLabel")
            .setContentText("Tap to stop the alarm")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(pendingIntent, true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        
        // Mostrar notificação
        try {
            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        } catch (e: SecurityException) {
            Log.e("AlarmReceiver", "No permission to show notification", e)
        }
    }
    
    /**
     * Para o som do alarme
     */
    fun stopAlarmSound() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }
}
