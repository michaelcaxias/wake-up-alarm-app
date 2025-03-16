package org.app.wakeupalarm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.wakeupalarm.domain.model.Alarm
import org.app.wakeupalarm.navigation.Screen
import org.app.wakeupalarm.presentation.alarm.AlarmEditScreen
import org.app.wakeupalarm.presentation.alarm.viewmodel.SimpleAlarmEditViewModel
import org.app.wakeupalarm.presentation.components.AlarmList
import org.app.wakeupalarm.presentation.components.BottomNavigationBar
import org.app.wakeupalarm.presentation.theme.DarkColorPalette
import org.app.wakeupalarm.service.AlarmScheduler
import org.app.wakeupalarm.service.getAlarmScheduler
import org.app.wakeupalarm.presentation.alarm.viewmodel.SimpleAlarmListViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.util.UUID



/**
 * Componente principal do aplicativo
 * @param alarmScheduler Implementação do agendador de alarmes específico da plataforma
 */
@Composable
@Preview
fun App(alarmScheduler: AlarmScheduler = getAlarmScheduler()) {
    // Usando um ViewModel simples para demonstração
    val viewModel = remember { SimpleAlarmListViewModel(alarmScheduler) }
    
    // Estado para controlar a navegação entre telas
    var currentScreen by remember { mutableStateOf<Screen>(Screen.AlarmList) }
    
    // ViewModel para a tela de edição de alarme
    val editViewModel = remember { SimpleAlarmEditViewModel() }
    
    // Função para adicionar um novo alarme à lista principal
    val addAlarmToList: () -> Unit = {
        // Obter o estado atual do editViewModel
        val alarmState = editViewModel.state.value
        
        // Criar um novo alarme com os dados do editViewModel
        val newAlarm = Alarm(
            id = UUID.randomUUID().toString(),
            timeInMinutes = alarmState.timeInMinutes,
            label = alarmState.label,
            isEnabled = true,
            repeatDays = alarmState.repeatDays,
            soundUri = alarmState.soundUri,
            vibrate = alarmState.vibrate
        )
        
        // Adicionar o alarme ao viewModel da lista principal
        viewModel.addAlarm(newAlarm)
    }
    
    MaterialTheme(colors = DarkColorPalette) {
        // Navegação entre telas
        when (currentScreen) {
            is Screen.AlarmList -> {
                Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp)
                    ) {
                        // App Title
                        Text(
                            text = "Wake Up Alarm!",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        
                        Text(
                            text = "Your sleep assistant",
                            fontSize = 18.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        
                        // Alarm Cards
                        AlarmList(
                            viewModel = viewModel,
                            onEditAlarm = { alarmId ->
                                // Navegar para a tela de edição do alarme selecionado
                                currentScreen = Screen.AlarmEdit(alarmId)
                            }
                        )
                    }
                    
                    // Bottom Navigation Bar com FAB
                    BottomNavigationBar(
                        onAddClick = { 
                            // Navegar para a tela de adição de alarme
                            currentScreen = Screen.AlarmEdit(null)
                        }
                    )
                }
            }
            is Screen.AlarmEdit -> {
                // Verificar se é edição de alarme existente ou criação de novo alarme
                val alarmId = (currentScreen as Screen.AlarmEdit).alarmId
                
                // Se for edição, carregar os dados do alarme existente
                if (alarmId != null) {
                    // Encontrar o alarme na lista
                    val alarmToEdit = viewModel.state.value.alarms.find { it.id == alarmId }
                    
                    // Se encontrou o alarme, carregar seus dados no editViewModel
                    alarmToEdit?.let {
                        // Extrair horas e minutos do formato HH:MM
                        val timeParts = it.timeFormatted.split(":")
                        val hours = timeParts[0].toInt()
                        val minutes = timeParts[1].toInt()
                        
                        // Configurar o editViewModel com os dados do alarme
                        editViewModel.updateTime(hours * 60 + minutes)
                        editViewModel.updateLabel(it.label)
                        editViewModel.updateRepeatDays(viewModel.getAlarmRepeatDays(it.id))
                        // Manter as configurações de som e vibração padrão se não disponíveis
                    }
                }
                
                // Tela de edição de alarme
                AlarmEditScreen(
                    viewModel = editViewModel,
                    onNavigateBack = {
                        // Voltar para a tela de lista de alarmes sem salvar
                        currentScreen = Screen.AlarmList
                    },
                    onSave = {
                        if (alarmId != null) {
                            // Atualizar alarme existente
                            val updatedAlarm = Alarm(
                                id = alarmId,
                                timeInMinutes = editViewModel.state.value.timeInMinutes,
                                label = editViewModel.state.value.label,
                                isEnabled = true, // Manter ativo ao editar
                                repeatDays = editViewModel.state.value.repeatDays,
                                soundUri = editViewModel.state.value.soundUri,
                                vibrate = editViewModel.state.value.vibrate
                            )
                            
                            // Atualizar o alarme na lista
                            viewModel.updateAlarm(updatedAlarm)
                        } else {
                            // Adicionar novo alarme à lista principal
                            addAlarmToList()
                        }
                        
                        // Voltar para a tela de lista de alarmes
                        currentScreen = Screen.AlarmList
                    }
                )
            }
        }
    }
}
