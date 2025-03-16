package org.app.wakeupalarm

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import org.app.wakeupalarm.service.createAndroidAlarmScheduler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar a barra de status para utilizar a cor primária do tema
        configureStatusBar()

        // Criar uma instância do agendador de alarmes com o contexto do Android
        val alarmScheduler = createAndroidAlarmScheduler(applicationContext)

        setContent {
            // Passar o agendador de alarmes para o App
            App(alarmScheduler)
        }
    }
    
    /**
     * Configura a barra de status para usar a cor primária do tema
     */
    private fun configureStatusBar() {
        // Definir a cor da barra de status para a cor primária do tema (azul)
        val statusBarColor = Color(0xFF1F2033).toArgb()
        window.statusBarColor = statusBarColor
        
        // Configurar para que o conteúdo seja desenhado atrás da barra de status
        // e definir se os ícones da barra de status devem ser escuros ou claros
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false // ícones claros para tema escuro
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    // Na preview, usamos a implementação padrão do AlarmScheduler
    App()
}