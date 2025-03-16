package org.app.wakeupalarm.presentation.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
    onNavigateBack: () -> Unit,
    onSave: () -> Unit = {}
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
    var alarmName by remember { mutableStateOf(state.label.ifEmpty { "Alarme" }) }
    var selectedRingtone by remember { mutableStateOf(state.soundUri.ifEmpty { "morning!" }) }
    var volume by remember { mutableStateOf(0.7f) }
    var isVibrateEnabled by remember { mutableStateOf(state.vibrate) }
    
    // Estado para controlar o diálogo de seleção de ringtone
    var showRingtoneDialog by remember { mutableStateOf(false) }
    
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
                        viewModel.updateLabel(alarmName.ifEmpty { "Alarme" })
                        viewModel.updateRepeatDays(selectedDays)
                        viewModel.updateSound(selectedRingtone)
                        viewModel.updateVibrate(isVibrateEnabled)
                        
                        // Salvar o alarme
                        viewModel.saveAlarm()
                        
                        // Chamar o callback de salvamento e voltar
                        onSave()
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
            
            // Alarm Name - Campo editável
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Alarm Name",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = alarmName,
                    onValueChange = { alarmName = it },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color(0xFF4A5CFF),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    ),
                    placeholder = { Text("Enter alarm name", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth()
                )
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
            
            // Espaçador no lugar da conexão IoT removida
            Spacer(modifier = Modifier.height(16.dp))
            
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
            
            // Ringtone Selector com diálogo de seleção
            SettingRow(
                title = "Ringtone",
                trailing = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showRingtoneDialog = true }
                    ) {
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
            
            // Diálogo de seleção de ringtone
            if (showRingtoneDialog) {
                AlertDialog(
                    onDismissRequest = { showRingtoneDialog = false },
                    title = { Text("Select Ringtone", color = Color.White) },
                    backgroundColor = Color(0xFF2A2B3D),
                    contentColor = Color.White,
                    text = {
                        Column {
                            val ringtones = listOf("morning!", "Gentle Alarm", "Sunrise", "Birdsong", "Ocean Waves")
                            ringtones.forEach { ringtone ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedRingtone = ringtone
                                            showRingtoneDialog = false
                                        }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedRingtone == ringtone,
                                        onClick = {
                                            selectedRingtone = ringtone
                                            showRingtoneDialog = false
                                        },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = accentColor,
                                            unselectedColor = Color.Gray
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(ringtone, color = Color.White)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { showRingtoneDialog = false },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = accentColor
                            )
                        ) {
                            Text("Close")
                        }
                    }
                )
            }
            
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
 * Seletor de dias da semana moderno com feedback visual melhorado
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
    
    // Adicionar opções rápidas para seleção de dias
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Opções rápidas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Dias úteis
            QuickDayOption(
                text = "Weekdays",
                onClick = {
                    val weekdays = listOf(
                        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
                    )
                    weekdays.forEach { day ->
                        if (!selectedDays.contains(day)) {
                            onDaySelected(day, true)
                        }
                    }
                }
            )
            
            // Fins de semana
            QuickDayOption(
                text = "Weekend",
                onClick = {
                    val weekend = listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
                    weekend.forEach { day ->
                        if (!selectedDays.contains(day)) {
                            onDaySelected(day, true)
                        }
                    }
                }
            )
            
            // Todos os dias
            QuickDayOption(
                text = "Every Day",
                onClick = {
                    DayOfWeek.values().forEach { day ->
                        if (!selectedDays.contains(day)) {
                            onDaySelected(day, true)
                        }
                    }
                }
            )
            
            // Limpar
            QuickDayOption(
                text = "Clear",
                onClick = {
                    val currentDays = selectedDays.toList()
                    currentDays.forEach { day ->
                        onDaySelected(day, false)
                    }
                }
            )
        }
        
        // Seletor de dias individuais
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
}

/**
 * Botão para opções rápidas de seleção de dias
 */
@Composable
fun QuickDayOption(
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = Color(0xFF4A5CFF)
        )
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
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
 * Seletor de hora moderno com estilo carrossel e scroll
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
        // Lista de horas formatadas com zeros à esquerda
        val hours = (1..12).map { it.toString().padStart(2, '0') }
        
        // Lista de minutos formatados com zeros à esquerda
        val minutes = (0..59).map { it.toString().padStart(2, '0') }
        
        // Calcular o índice da hora selecionada (12h format)
        val hourIndex = if (selectedHour % 12 == 0) 11 else (selectedHour % 12) - 1
        
        // Seletor de hora com scroll
        ScrollableNumberPicker(
            items = hours,
            initialIndex = hourIndex,
            onValueChange = { index ->
                // Converter de volta para o formato 24h
                val newHour = (index + 1) % 12
                onHourSelected(if (newHour == 0) 12 else newHour + (if (selectedAmPm == "PM" && newHour != 12) 12 else 0))
            }
        )
        
        Text(
            text = ":",
            color = Color.White,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        // Seletor de minutos com scroll
        ScrollableNumberPicker(
            items = minutes,
            initialIndex = selectedMinute,
            onValueChange = { index -> onMinuteSelected(index) }
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
                    .clickable { 
                        onAmPmSelected("PM")
                        // Ajustar a hora para PM
                        val hour = selectedHour % 12
                        if (hour != 0) {
                            onHourSelected(hour + 12)
                        } else {
                            onHourSelected(12)
                        }
                    }
                    .padding(vertical = 8.dp)
            )
            
            Text(
                text = "AM",
                color = if (selectedAmPm == "AM") Color.White else Color.Gray,
                fontWeight = if (selectedAmPm == "AM") FontWeight.Bold else FontWeight.Normal,
                fontSize = 16.sp,
                modifier = Modifier
                    .clickable { 
                        onAmPmSelected("AM")
                        // Ajustar a hora para AM
                        val hour = selectedHour % 12
                        if (hour != 0) {
                            onHourSelected(hour)
                        } else {
                            onHourSelected(0)
                        }
                    }
                    .padding(vertical = 8.dp)
            )
        }
    }
}

/**
 * Seletor de números com scroll para o time picker
 */
@Composable
fun ScrollableNumberPicker(
    items: List<String>,
    initialIndex: Int,
    onValueChange: (Int) -> Unit
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    var isDragging by remember { mutableStateOf(false) }
    var currentIndex by remember { mutableStateOf(initialIndex) }
    
    // Efeito para atualizar o valor quando o scroll para
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress && isDragging) {
            isDragging = false
            // Encontrar o item mais próximo do centro
            val centerIndex = listState.firstVisibleItemIndex + 1
            if (centerIndex != currentIndex) {
                currentIndex = centerIndex.coerceIn(0, items.size - 1)
                onValueChange(currentIndex)
            }
        }
    }
    
    // Detectar quando o usuário começa a arrastar
    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (listState.isScrollInProgress) {
            isDragging = true
        }
    }
    
    Box(
        modifier = Modifier
            .height(180.dp)
            .width(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // Indicador de seleção (linha central)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color(0xFF2A2B3D).copy(alpha = 0.3f))
        )
        
        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight(),
            contentPadding = PaddingValues(vertical = 60.dp)
        ) {
            // Adicionar itens antes para permitir scroll infinito
            items(items.size + 2) { index ->
                val itemIndex = when {
                    index == 0 -> items.size - 1 // Último item no topo
                    index == items.size + 1 -> 0 // Primeiro item no final
                    else -> index - 1 // Itens normais
                }
                
                val isSelected = index == listState.firstVisibleItemIndex + 1
                
                Text(
                    text = items[itemIndex],
                    fontSize = if (isSelected) 48.sp else 24.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else Color.Gray.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable {
                            currentIndex = itemIndex
                            onValueChange(itemIndex)
                        }
                )
            }
        }
    }
}
