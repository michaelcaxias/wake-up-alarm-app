package org.app.wakeupalarm

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import org.app.wakeupalarm.presentation.alarm.AlarmRingingScreen
import org.app.wakeupalarm.receiver.AlarmReceiver

/**
 * Activity que exibe a tela de alarme tocando
 * Esta activity é exibida quando um alarme é disparado
 */
class AlarmRingingActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Antes de tudo, trazer esta atividade para frente
        bringActivityToFront()
        
        // Configurar a janela para exibir sobre a tela de bloqueio
        setupWindowForLockScreen()
        
        // Obter os dados do alarme da intent
        val alarmId = intent.getStringExtra(AlarmReceiver.EXTRA_ALARM_ID) ?: ""
        val alarmLabel = intent.getStringExtra(AlarmReceiver.EXTRA_ALARM_LABEL) ?: "Alarm"
        
        setContent {
            AlarmRingingScreen(
                alarmLabel = alarmLabel,
                onDismiss = {
                    // Parar o alarme e fechar a activity
                    stopAlarmAndFinish(alarmId)
                }
            )
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Atualizar a intent atual
        setIntent(intent)
        // Trazer a atividade para frente novamente em caso de nova intent
        bringActivityToFront()
    }
    
    /**
     * Traz esta activity para o primeiro plano, sobrepondo outras apps
     */
    private fun bringActivityToFront() {
        // Garantir que a atividade esteja em primeiro plano
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        
        // Outras configurações para garantir visibilidade
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or 
            WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
        
        // Trazer a janela para frente com máxima prioridade
        window.attributes.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        window.attributes.layoutInDisplayCutoutMode = 
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }
    
    /**
     * Configura a janela para ser exibida sobre a tela de bloqueio
     */
    private fun setupWindowForLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            
            // Desativar o keyguard se possível
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            // Para versões mais antigas do Android
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        
        // Configurações adicionais para visibilidade máxima
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Para Android 11 (API 30) e superior
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Para Android 10 (API 29) e inferior
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        }
    }
    
    /**
     * Para o alarme e fecha a activity, retornando à tela anterior
     */
    private fun stopAlarmAndFinish(alarmId: String) {
        // Enviar broadcast para parar o alarme
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_STOP_ALARM
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
        }
        sendBroadcast(intent)
        
        // Verificar se há uma activity anterior para retornar
        val mainActivityIntent = Intent(this, MainActivity::class.java).apply {
            // Adicionar flags para limpar a pilha de activities se necessário
            // mas apenas se esta for a única activity aberta
            if (isTaskRoot) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
        }
        
        // Se o alarme foi disparado enquanto o app estava fechado, abra a tela principal
        if (isTaskRoot) {
            startActivity(mainActivityIntent)
        }
        
        // Fechar a activity atual
        finish()
    }
}

@Preview
@Composable
fun AlarmRingingPreview() {
    AlarmRingingScreen(
        alarmLabel = "Wake Up",
        onDismiss = {}
    )
}
