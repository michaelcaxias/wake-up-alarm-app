package org.app.wakeupalarm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.app.wakeupalarm.domain.model.Alarm
import org.app.wakeupalarm.domain.model.DayOfWeek
import org.app.wakeupalarm.domain.usecase.ScheduleAlarmUseCase
import org.app.wakeupalarm.navigation.Screen
import org.app.wakeupalarm.presentation.alarm.AlarmEditScreen
import org.app.wakeupalarm.presentation.alarm.state.AlarmListState
import org.app.wakeupalarm.presentation.alarm.state.AlarmUiModel
import org.app.wakeupalarm.presentation.alarm.viewmodel.AlarmListViewModel
import org.app.wakeupalarm.presentation.alarm.viewmodel.SimpleAlarmEditViewModel
import org.app.wakeupalarm.service.AlarmScheduler
import org.app.wakeupalarm.service.getAlarmScheduler
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.util.UUID

/**
 * Implementação simples do ViewModel para demonstração
 * @param alarmScheduler Implementação do agendador de alarmes específico da plataforma
 */
class SimpleAlarmListViewModel(private val alarmScheduler: AlarmScheduler) : AlarmListViewModel {
    private val _state = MutableStateFlow(
        AlarmListState(
            alarms = listOf(
                AlarmUiModel(
                    id = "1",
                    timeFormatted = "04:30",
                    label = "Sholat shubuh",
                    isEnabled = true,
                    repeatDaysFormatted = "Everyday"
                ),
                AlarmUiModel(
                    id = "2",
                    timeFormatted = "09:00",
                    label = "Kuliah",
                    isEnabled = false,
                    repeatDaysFormatted = "Mon Tue Wed Thu"
                ),
                AlarmUiModel(
                    id = "3",
                    timeFormatted = "22:00",
                    label = "Tidur Malam",
                    isEnabled = false,
                    repeatDaysFormatted = "Mon Tue Wed"
                )
            ),
            isLoading = false
        )
    )
    
    // Caso de uso para agendar alarmes usando a implementação específica da plataforma
    private val scheduleAlarmUseCase = ScheduleAlarmUseCase(alarmScheduler)
    
    // Função para adicionar um novo alarme à lista
    fun addAlarm(alarm: Alarm) {
        // Converter o alarme do domínio para o modelo de UI
        val alarmUiModel = AlarmUiModel(
            id = alarm.id,
            timeFormatted = formatTime(alarm.timeInMinutes),
            label = alarm.label,
            isEnabled = alarm.isEnabled,
            repeatDaysFormatted = formatRepeatDays(alarm.repeatDays)
        )
        
        // Adicionar o novo alarme à lista atual
        val currentAlarms = _state.value.alarms.toMutableList()
        currentAlarms.add(0, alarmUiModel) // Adicionar no início da lista
        
        // Atualizar o estado
        _state.value = _state.value.copy(alarms = currentAlarms)
        
        // Agendar o alarme
        scheduleAlarmUseCase.execute(alarm)
    }
    
    // Função auxiliar para formatar o tempo no formato 24h
    private fun formatTime(timeInMinutes: Int): String {
        val hours = timeInMinutes / 60
        val minutes = timeInMinutes % 60
        return String.format("%02d:%02d", hours, minutes)
    }
    
    // Função auxiliar para formatar os dias de repetição
    private fun formatRepeatDays(days: List<DayOfWeek>): String {
        if (days.isEmpty()) return "Once"
        
        // Verificar se são todos os dias
        if (days.size == 7) return "Everyday"
        
        // Verificar se são dias úteis
        val weekdays = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
        if (days.size == 5 && days.containsAll(weekdays)) return "Weekdays"
        
        // Verificar se é fim de semana
        val weekend = listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        if (days.size == 2 && days.containsAll(weekend)) return "Weekend"
        
        // Caso contrário, listar os dias abreviados
        return days.joinToString(" ") { it.name.take(3) }
    }
    
    override val state: StateFlow<AlarmListState> = _state
    
    override fun toggleAlarmStatus(id: String, isEnabled: Boolean) {
        val currentAlarms = _state.value.alarms.toMutableList()
        val index = currentAlarms.indexOfFirst { it.id == id }
        if (index >= 0) {
            val alarm = currentAlarms[index]
            currentAlarms[index] = alarm.copy(isEnabled = isEnabled)
            _state.value = _state.value.copy(alarms = currentAlarms)
        }
    }
    
    override fun deleteAlarm(id: String) {
        val currentAlarms = _state.value.alarms.toMutableList()
        currentAlarms.removeAll { it.id == id }
        _state.value = _state.value.copy(alarms = currentAlarms)
    }
    
    override fun updateAlarm(alarm: Alarm) {
        val currentAlarms = _state.value.alarms.toMutableList()
        val index = currentAlarms.indexOfFirst { it.id == alarm.id }
        
        if (index >= 0) {
            // Substituir o alarme existente pelo atualizado
            currentAlarms[index] = AlarmUiModel(
                id = alarm.id,
                timeFormatted = formatTime(alarm.timeInMinutes),
                label = alarm.label,
                isEnabled = alarm.isEnabled,
                repeatDaysFormatted = formatRepeatDays(alarm.repeatDays)
            )
            
            _state.value = _state.value.copy(alarms = currentAlarms)
            
            // Reagendar o alarme com as novas configurações
            scheduleAlarmUseCase.execute(alarm)
        }
    }
    
    override fun getAlarmRepeatDays(id: String): List<DayOfWeek> {
        // Mapeamento de alarmes para seus dias de repetição
        // Em uma implementação real, isso viria do repositório de dados
        val alarmRepeatDaysMap = mapOf(
            "1" to listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY), // Everyday
            "2" to listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY), // Mon Tue Wed Thu
            "3" to listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY) // Mon Tue Wed
        )
        
        // Retornar os dias de repetição do alarme ou uma lista vazia se não encontrado
        return alarmRepeatDaysMap[id] ?: emptyList()
    }
    
    override fun refresh() {
        // Nada a fazer na implementação simples
    }
}

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
    
    val darkColorPalette = darkColors(
        primary = Color(0xFF4A5CFF),
        primaryVariant = Color(0xFF3F51B5),
        secondary = Color(0xFFFF4081),
        background = Color(0xFF1F2033),
        surface = Color(0xFF2A2B3D),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.White,
        onSurface = Color.White
    )
    
    MaterialTheme(colors = darkColorPalette) {
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
                    
                    // Bottom Navigation & FAB
                    Box(
                        modifier = Modifier.fillMaxSize().padding(bottom = 16.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { /* TODO */ },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Outlined.List,
                                    contentDescription = "List",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            
                            FloatingActionButton(
                                onClick = { 
                                    // Navegar para a tela de adição de alarme
                                    currentScreen = Screen.AlarmEdit(null)
                                },
                                backgroundColor = Color(0xFF4A5CFF),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "Add Alarm",
                                    tint = Color.White
                                )
                            }
                            
                            IconButton(
                                onClick = { /* TODO */ },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Settings,
                                    contentDescription = "Settings",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
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

@Composable
fun AlarmList(viewModel: AlarmListViewModel, onEditAlarm: (String) -> Unit = {}) {
    val state by viewModel.state.collectAsState()
    
    Column(modifier = Modifier.fillMaxWidth()) {
        state.alarms.forEachIndexed { index, alarm ->
            AlarmCard(
                alarm = alarm,
                onToggle = { viewModel.toggleAlarmStatus(alarm.id, it) },
                index = index,
                onEdit = { 
                    // Navegar para a tela de edição do alarme selecionado usando o callback
                    onEditAlarm(alarm.id)
                },
                onDelete = { viewModel.deleteAlarm(alarm.id) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AlarmCard(
    alarm: AlarmUiModel, 
    onToggle: (Boolean) -> Unit, 
    index: Int,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    // Cores baseadas no status do alarme (ativo = rosa, inativo = cinza escuro)
    val colors = if (alarm.isEnabled) {
        listOf(Color(0xFFFF4081), Color(0xFFFF7B9C)) // Pink gradient para alarmes ativos
    } else {
        listOf(Color(0xFF2A2B3D), Color(0xFF2A2B3D)) // Dark gray para alarmes inativos
    }
    
    // Primeiro alarme sempre tem texto branco, outros alarmes têm texto com opacidade baseada no status
    val textColor = if (index == 0) Color.White else Color.White.copy(alpha = if (alarm.isEnabled) 1f else 0.5f)
    
    // Primeiro alarme tem texto maior
    val timeSize = if (index == 0) 48.sp else 40.sp
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = 0.dp,
        backgroundColor = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(colors),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = alarm.label,
                        fontSize = 16.sp,
                        color = textColor
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Botões de editar e remover
                        IconButton(
                            onClick = { onEdit() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Editar alarme",
                                tint = textColor.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        IconButton(
                            onClick = { onDelete() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Remover alarme",
                                tint = textColor.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        Switch(
                            checked = alarm.isEnabled,
                            onCheckedChange = onToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4A5CFF).copy(alpha = 0.5f),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = alarm.timeFormatted,
                            fontSize = timeSize,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        
                        // Formato 24h - sem necessidade de mostrar AM/PM
                    }
                    
                    // Mostrar tempo restante para todos os alarmes ativos e para o primeiro alarme
                    if (index == 0 || alarm.isEnabled) {
                        Text(
                            text = "8h 13m",
                            fontSize = 16.sp,
                            color = textColor
                        )
                    }
                }
                
                Text(
                    text = alarm.repeatDaysFormatted,
                    fontSize = 14.sp,
                    color = textColor.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = if (index == 0 || alarm.isEnabled) 8.dp else 0.dp)
                )
            }
        }
    }
}