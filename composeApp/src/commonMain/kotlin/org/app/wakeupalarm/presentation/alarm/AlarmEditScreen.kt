package org.app.wakeupalarm.presentation.alarm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.app.wakeupalarm.domain.model.DayOfWeek
import org.app.wakeupalarm.presentation.alarm.viewmodel.AlarmEditViewModel
import org.app.wakeupalarm.presentation.components.DayOfWeekSelector
import org.app.wakeupalarm.presentation.components.TimePickerComponent

/**
 * Tela de edição/adição de alarme
 */
@Composable
fun AlarmEditScreen(
    viewModel: AlarmEditViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Editar Alarme" else "Novo Alarme") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                AlarmEditContent(
                    timeInMinutes = state.timeInMinutes,
                    label = state.label,
                    repeatDays = state.repeatDays,
                    soundUri = state.soundUri,
                    vibrate = state.vibrate,
                    onTimeChanged = viewModel::updateTime,
                    onLabelChanged = viewModel::updateLabel,
                    onRepeatDaysChanged = viewModel::updateRepeatDays,
                    onSoundChanged = viewModel::updateSound,
                    onVibrateChanged = viewModel::updateVibrate,
                    onSaveAlarm = {
                        viewModel.saveAlarm()
                        onNavigateBack()
                    }
                )
            }
            
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
}

/**
 * Conteúdo do formulário de edição de alarme
 */
@Composable
private fun AlarmEditContent(
    timeInMinutes: Int,
    label: String,
    repeatDays: List<DayOfWeek>,
    soundUri: String,
    vibrate: Boolean,
    onTimeChanged: (Int) -> Unit,
    onLabelChanged: (String) -> Unit,
    onRepeatDaysChanged: (List<DayOfWeek>) -> Unit,
    onSoundChanged: (String) -> Unit,
    onVibrateChanged: (Boolean) -> Unit,
    onSaveAlarm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Seletor de Tempo
        TimePickerComponent(
            timeInMinutes = timeInMinutes,
            onTimeChanged = onTimeChanged
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Campo de Etiqueta
        OutlinedTextField(
            value = label,
            onValueChange = onLabelChanged,
            label = { Text("Etiqueta") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Seletor de Dias da Semana
        Text(
            text = "Repetir",
            style = MaterialTheme.typography.subtitle1
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        DayOfWeekSelector(
            selectedDays = repeatDays,
            onDaysSelected = onRepeatDaysChanged
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Opções de Som e Vibração
        // Aqui você pode adicionar componentes para selecionar som e vibração
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Botão de Salvar
        Button(
            onClick = onSaveAlarm,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Alarme")
        }
    }
}
