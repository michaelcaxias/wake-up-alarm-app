package org.app.wakeupalarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.app.wakeupalarm.service.createAndroidAlarmScheduler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Criar uma instância do agendador de alarmes com o contexto do Android
        val alarmScheduler = createAndroidAlarmScheduler(applicationContext)

        setContent {
            // Passar o agendador de alarmes para o App
            App(alarmScheduler)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    // Na preview, usamos a implementação padrão do AlarmScheduler
    App()
}