package org.app.wakeupalarm

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import org.app.wakeupalarm.presentation.alarm.AlarmRingingScreen
import org.app.wakeupalarm.receiver.AlarmReceiver

/**
 * Activity que exibe a tela de alarme tocando
 * Esta activity é exibida quando um alarme é disparado
 */
class AlarmRingingActivity : ComponentActivity() {
    
    private val TAG = "AlarmRingingActivity"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            
            Log.d(TAG, "onCreate: Iniciando activity de alarme")
            
            // Configurar a barra de status com a cor do tema
            configureStatusBar()
            
            // Antes de tudo, garantir que a atividade não será destruída
            // quando o dispositivo está em modo de economia de energia
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                setShowWhenLocked(true)
                setTurnScreenOn(true)
            } else {
                @Suppress("DEPRECATION")
                window.addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                )
            }
            
            // Configurar a janela para exibir sobre a tela de bloqueio
            setupWindowForLockScreen()
            
            // Obter os dados do alarme da intent
            val alarmId = intent.getStringExtra(AlarmReceiver.EXTRA_ALARM_ID) ?: ""
            val alarmLabel = intent.getStringExtra(AlarmReceiver.EXTRA_ALARM_LABEL) ?: "Alarm"
            
            Log.d(TAG, "onCreate: Alarme ID: $alarmId, Label: $alarmLabel")
            
            setContent {
                AlarmRingingScreen(
                    alarmLabel = alarmLabel,
                    onDismiss = {
                        // Parar o alarme e fechar a activity
                        stopAlarmAndFinish(alarmId)
                    }
                )
            }
            
            Log.d(TAG, "onCreate: Tela de alarme configurada com sucesso")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao criar a tela de alarme: ${e.message}")
            e.printStackTrace()
            // Garantir que a activity não falhe completamente
            finish()
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
        try {
            Log.d(TAG, "bringActivityToFront: Trazendo a activity para frente")
            
            // Trazer a janela para frente com máxima prioridade
            window.attributes.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
            
            // Definir o modo de exibição no notch apenas em versões compatíveis
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.attributes.layoutInDisplayCutoutMode = 
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
            
            Log.d(TAG, "bringActivityToFront: Activity configurada para exibição prioritária")
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao trazer activity para frente: ${e.message}")
        }
    }
    
    /**
     * Configura a janela para ser exibida sobre a tela de bloqueio
     */
    private fun setupWindowForLockScreen() {
        try {
            Log.d(TAG, "setupWindowForLockScreen: Configurando janela para tela de bloqueio")
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                // Para Android 8.1+
                try {
                    // Desativar o keyguard se possível
                    val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                    keyguardManager.requestDismissKeyguard(this, null)
                } catch (e: Exception) {
                    Log.e(TAG, "Erro ao desativar keyguard: ${e.message}")
                }
            } else {
                // Para versões mais antigas do Android
                @Suppress("DEPRECATION")
                window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
            }
            
            // Configurações adicionais para visibilidade máxima (somente em versões compatíveis)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Para Android 11 (API 30) e superior
                try {
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    window.insetsController?.let {
                        it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                        it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Erro ao configurar insetsController: ${e.message}")
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Para Android 6-10
                try {
                    @Suppress("DEPRECATION")
                    window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Erro ao configurar systemUiVisibility: ${e.message}")
                }
            }
            
            Log.d(TAG, "setupWindowForLockScreen: Configuração concluída")
        } catch (e: Exception) {
            Log.e(TAG, "Erro geral ao configurar janela: ${e.message}")
        }
    }
    
    /**
     * Configura a barra de status para usar a cor primária do tema
     */
    private fun configureStatusBar() {
        // Definir a cor da barra de status para a cor primária do tema (azul)
        val statusBarColor = Color(0xFF4A5CFF).toArgb()
        window.statusBarColor = statusBarColor
        
        // Configurar para que o conteúdo seja desenhado atrás da barra de status
        // e definir se os ícones da barra de status devem ser escuros ou claros
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false // ícones claros para tema escuro
        }
    }
    
    /**
     * Para o alarme e fecha a activity, retornando à tela anterior
     */
    private fun stopAlarmAndFinish(alarmId: String) {
        try {
            Log.d(TAG, "stopAlarmAndFinish: Parando alarme e fechando a tela")
            
            // Chamar diretamente o método estático para parar o som, sem depender do broadcast
            Log.d(TAG, "Chamando diretamente AlarmReceiver.stopAlarmSound()")
            AlarmReceiver.stopAlarmSound()

            // Também enviamos o broadcast como backup
            val intent = Intent(this, AlarmReceiver::class.java).apply {
                action = AlarmReceiver.ACTION_STOP_ALARM
                putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
            }
            sendBroadcast(intent)
            Log.d(TAG, "stopAlarmAndFinish: Broadcast enviado para parar o som")
            
            // Forçar o fechamento da activity atual com flags especiais
            try {
                // Verificar se há uma activity anterior para retornar
                val mainActivityIntent = Intent(this, MainActivity::class.java).apply {
                    // Garantir que a MainActivity seja aberta corretamente
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or 
                             Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                
                // Se o alarme foi disparado enquanto o app estava fechado, abra a tela principal
                if (isTaskRoot) {
                    Log.d(TAG, "stopAlarmAndFinish: Abrindo MainActivity")
                    startActivity(mainActivityIntent)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao navegar para MainActivity: ${e.message}")
                // Mesmo com erro, tentar finalizar esta Activity
            }
            
            // Definir um resultado para a activity (pode ajudar em alguns casos)
            setResult(RESULT_OK)
            
            // Usar um handler para garantir que o finish() seja chamado após um pequeno delay
            android.os.Handler(mainLooper).postDelayed({
                Log.d(TAG, "stopAlarmAndFinish: Chamando finish() com delay")
                // Forçar o fim desta activity
                finishAndRemoveTask()
                finish()
            }, 100) // Pequeno delay para garantir que o broadcast foi processado
            
            // Também chamar finish() imediatamente
            Log.d(TAG, "stopAlarmAndFinish: Chamando finish() imediatamente")
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Erro fatal ao tentar fechar a tela: ${e.message}")
            e.printStackTrace()
            // Última tentativa de forçar o fechamento da activity
            finishAffinity()
            finish()
        }
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
