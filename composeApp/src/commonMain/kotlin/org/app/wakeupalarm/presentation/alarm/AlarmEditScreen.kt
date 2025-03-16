package org.app.wakeupalarm.presentation.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.wakeupalarm.domain.model.DayOfWeek
import org.app.wakeupalarm.presentation.alarm.viewmodel.SimpleAlarmEditViewModel

/**
 * Tela de edição/adição de alarme com design moderno
 */
@Composable
fun AlarmEditScreen(
    viewModel: SimpleAlarmEditViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val darkBackground = Color(0xFF1F2033)
    val accentColor = Color(0xFFFF4081)
    
    // Valores para o time picker
    var selectedHour by remember { mutableStateOf(state.timeInMinutes / 60) }
    var selectedMinute by remember { mutableStateOf(state.timeInMinutes % 60) }
    var selectedAmPm by remember { mutableStateOf(if (selectedHour < 12) "AM" else "PM") }
    
    // Valores para os dias da semana
    var selectedDays by remember { mutableStateOf(state.repeatDays) }
    
    // Valores para configurações adicionais
    var alarmName by remember { mutableStateOf(state.label) }
    var isIoTConnected by remember { mutableStateOf(false) }
    var selectedRingtone by remember { mutableStateOf("morning!") }
    var volume by remember { mutableStateOf(0.7f) }
    var isVibrateEnabled by remember { mutableStateOf(state.vibrate) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Top Bar with Close and Save buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF4A5CFF),
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Button(
                    onClick = {
                        // Atualizar o estado do viewModel antes de salvar
                        viewModel.updateTime(selectedHour * 60 + selectedMinute)
                        viewModel.updateLabel(alarmName)
                        viewModel.updateRepeatDays(selectedDays)
                        viewModel.updateVibrate(isVibrateEnabled)
                        
                        // Salvar e voltar
                        viewModel.saveAlarm()
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF4A5CFF),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.width(100.dp)
                ) {
                    Text("Save")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Alarm Name with Edit icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Alarm Name",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = { /* Abrir diálogo para editar nome */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Name",
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Time Picker
            ModernTimePicker(
                selectedHour = selectedHour,
                selectedMinute = selectedMinute,
                selectedAmPm = selectedAmPm,
                onHourSelected = { selectedHour = it },
                onMinuteSelected = { selectedMinute = it },
                onAmPmSelected = { selectedAmPm = it }
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // IoT Connection Switch
            SettingRow(
                title = "Connect IoT",
                trailing = {
                    Switch(
                        checked = isIoTConnected,
                        onCheckedChange = { isIoTConnected = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = accentColor,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                        )
                    )
                }
            )
            
            // Repeat Days Selector
            SettingRow(
                title = "Repeat",
                trailing = {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = Color.White
                    )
                },
                expanded = true,
                expandedContent = {
                    ModernDaySelector(
                        selectedDays = selectedDays,
                        onDaySelected = { day, selected ->
                            selectedDays = if (selected) {
                                selectedDays + day
                            } else {
                                selectedDays - day
                            }
                        }
                    )
                }
            )
            
            // Ringtone Selector
            SettingRow(
                title = "Ringtone",
                trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = selectedRingtone,
                            color = Color.White,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Select",
                            tint = accentColor
                        )
                    }
                }
            )
            
            // Volume Slider
            SettingRow(
                title = "Volume",
                trailing = {},
                expanded = true,
                expandedContent = {
                    Slider(
                        value = volume,
                        onValueChange = { volume = it },
                        colors = SliderDefaults.colors(
                            thumbColor = accentColor,
                            activeTrackColor = accentColor
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            )
            
            // Vibrate Switch
            SettingRow(
                title = "Vibrate",
                trailing = {
                    Switch(
                        checked = isVibrateEnabled,
                        onCheckedChange = { isVibrateEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = accentColor,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                        )
                    )
                }
            )
        }
        
        // Error message if needed
        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colors.error,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

/**
 * Componente de configuração com opção de expansão
 */
@Composable
fun SettingRow(
    title: String,
    trailing: @Composable () -> Unit,
    expanded: Boolean = false,
    expandedContent: @Composable (() -> Unit)? = null
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            
            trailing()
        }
        
        if (expanded && expandedContent != null) {
            expandedContent()
        }
        
        Divider(color = Color(0xFF2A2B3D), thickness = 1.dp)
    }
}

/**
 * Seletor de dias da semana moderno
 */
@Composable
fun ModernDaySelector(
    selectedDays: List<DayOfWeek>,
    onDaySelected: (DayOfWeek, Boolean) -> Unit
) {
    val days = listOf(
        "Mon" to DayOfWeek.MONDAY,
        "Tue" to DayOfWeek.TUESDAY,
        "Wed" to DayOfWeek.WEDNESDAY,
        "Thu" to DayOfWeek.THURSDAY,
        "Fri" to DayOfWeek.FRIDAY,
        "Sat" to DayOfWeek.SATURDAY,
        "Sun" to DayOfWeek.SUNDAY
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEach { (label, day) ->
            val isSelected = selectedDays.contains(day)
            DayButton(
                day = label,
                isSelected = isSelected,
                onClick = { onDaySelected(day, !isSelected) }
            )
        }
    }
}

/**
 * Botão de dia da semana
 */
@Composable
fun DayButton(day: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFFF4081) else Color(0xFF2A2B3D))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * Seletor de hora moderno com estilo carrossel
 */
@Composable
fun ModernTimePicker(
    selectedHour: Int,
    selectedMinute: Int,
    selectedAmPm: String,
    onHourSelected: (Int) -> Unit,
    onMinuteSelected: (Int) -> Unit,
    onAmPmSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hour selector
        TimePickerColumn(
            items = listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"),
            selectedIndex = (selectedHour - 1) % 12,
            onItemSelected = { onHourSelected((it + 1) % 12 + if (selectedAmPm == "PM") 12 else 0) }
        )
        
        Text(
            text = ":",
            color = Color.White,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        // Minute selector
        TimePickerColumn(
            items = listOf("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
                    "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                    "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
                    "31", "32", "33", "34", "35", "36", "37", "38", "39", "40",
                    "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
                    "51", "52", "53", "54", "55", "56", "57", "58", "59"),
            selectedIndex = selectedMinute,
            onItemSelected = { onMinuteSelected(it) }
        )
        
        // AM/PM selector
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = "PM",
                color = if (selectedAmPm == "PM") Color.White else Color.Gray,
                fontWeight = if (selectedAmPm == "PM") FontWeight.Bold else FontWeight.Normal,
                fontSize = 16.sp,
                modifier = Modifier
                    .clickable { onAmPmSelected("PM") }
                    .padding(vertical = 8.dp)
            )
            
            Text(
                text = "AM",
                color = if (selectedAmPm == "AM") Color.White else Color.Gray,
                fontWeight = if (selectedAmPm == "AM") FontWeight.Bold else FontWeight.Normal,
                fontSize = 16.sp,
                modifier = Modifier
                    .clickable { onAmPmSelected("AM") }
                    .padding(vertical = 8.dp)
            )
        }
    }
}

/**
 * Coluna de seleção para o time picker
 */
@Composable
fun TimePickerColumn(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.height(180.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(80.dp)
        ) {
            // Mostrar item anterior com opacidade reduzida
            if (selectedIndex > 0) {
                Text(
                    text = items[(selectedIndex - 1) % items.size],
                    fontSize = 24.sp,
                    color = Color.Gray.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable { onItemSelected((selectedIndex - 1) % items.size) }
                )
            } else {
                Text(
                    text = items[items.size - 1],
                    fontSize = 24.sp,
                    color = Color.Gray.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable { onItemSelected(items.size - 1) }
                )
            }
            
            // Item selecionado
            Text(
                text = items[selectedIndex],
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // Próximo item com opacidade reduzida
            Text(
                text = items[(selectedIndex + 1) % items.size],
                fontSize = 24.sp,
                color = Color.Gray.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { onItemSelected((selectedIndex + 1) % items.size) }
            )
        }
    }
}
