package org.app.wakeupalarm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.List
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.app.wakeupalarm.presentation.alarm.state.AlarmListState
import org.app.wakeupalarm.presentation.alarm.state.AlarmUiModel
import org.app.wakeupalarm.presentation.alarm.viewmodel.AlarmListViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Implementação simples do ViewModel para demonstração
 */
class SimpleAlarmListViewModel : AlarmListViewModel {
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
    
    override fun refresh() {
        // Nada a fazer na implementação simples
    }
}

/**
 * Componente principal do aplicativo
 */
@Composable
@Preview
fun App() {
    // Usando um ViewModel simples para demonstração
    val viewModel = remember { SimpleAlarmListViewModel() }
    
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
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp)
            ) {
                // App Title
                Text(
                    text = "Asistangi",
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
                AlarmList(viewModel)
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
                            Icons.Outlined.List,
                            contentDescription = "List",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    
                    FloatingActionButton(
                        onClick = { /* TODO */ },
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
}

@Composable
fun AlarmList(viewModel: AlarmListViewModel) {
    val state by viewModel.state.collectAsState()
    
    Column(modifier = Modifier.fillMaxWidth()) {
        state.alarms.forEachIndexed { index, alarm ->
            AlarmCard(
                alarm = alarm,
                onToggle = { viewModel.toggleAlarmStatus(alarm.id, it) },
                index = index
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AlarmCard(alarm: AlarmUiModel, onToggle: (Boolean) -> Unit, index: Int) {
    val colors = when (index) {
        0 -> listOf(Color(0xFFFF4081), Color(0xFFFF7B9C)) // Pink gradient
        1 -> listOf(Color(0xFF2A2B3D), Color(0xFF2A2B3D)) // Dark gray
        else -> listOf(Color(0xFF2A2B3D), Color(0xFF2A2B3D)) // Dark gray
    }
    
    val textColor = if (index == 0) Color.White else Color.White.copy(alpha = if (alarm.isEnabled) 1f else 0.5f)
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
                        
                        if (index == 0) {
                            Text(
                                text = " AM",
                                fontSize = 16.sp,
                                color = textColor,
                                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                            )
                        }
                    }
                    
                    if (index == 0) {
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
                    modifier = Modifier.padding(top = if (index == 0) 8.dp else 0.dp)
                )
            }
        }
    }
}