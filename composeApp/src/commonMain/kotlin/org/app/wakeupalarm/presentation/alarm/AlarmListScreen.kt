package org.app.wakeupalarm.presentation.alarm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.wakeupalarm.presentation.alarm.state.AlarmUiModel
import org.app.wakeupalarm.presentation.alarm.viewmodel.AlarmListViewModel

/**
 * Tela principal de lista de alarmes
 */
@Composable
fun AlarmListScreen(
    viewModel: AlarmListViewModel,
    onAddAlarm: () -> Unit,
    onEditAlarm: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wake Up Alarm") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddAlarm) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Alarme")
            }
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
            } else if (state.alarms.isEmpty()) {
                EmptyAlarmList(modifier = Modifier.align(Alignment.Center))
            } else {
                AlarmList(
                    alarms = state.alarms,
                    onToggleAlarm = viewModel::toggleAlarmStatus,
                    onDeleteAlarm = viewModel::deleteAlarm,
                    onEditAlarm = onEditAlarm
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
 * Componente para exibir uma lista vazia de alarmes
 */
@Composable
private fun EmptyAlarmList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nenhum alarme configurado",
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Toque no botão + para adicionar um alarme",
            fontSize = 14.sp
        )
    }
}

/**
 * Componente para exibir a lista de alarmes
 */
@Composable
private fun AlarmList(
    alarms: List<AlarmUiModel>,
    onToggleAlarm: (String, Boolean) -> Unit,
    onDeleteAlarm: (String) -> Unit,
    onEditAlarm: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(alarms) { alarm ->
            AlarmItem(
                alarm = alarm,
                onToggleAlarm = onToggleAlarm,
                onDeleteAlarm = onDeleteAlarm,
                onEditAlarm = onEditAlarm
            )
            Divider()
        }
    }
}

/**
 * Componente para exibir um item de alarme
 */
@Composable
private fun AlarmItem(
    alarm: AlarmUiModel,
    onToggleAlarm: (String, Boolean) -> Unit,
    onDeleteAlarm: (String) -> Unit,
    onEditAlarm: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = alarm.timeFormatted,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = alarm.label,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = alarm.repeatDaysFormatted,
                        fontSize = 14.sp,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = { isEnabled ->
                        onToggleAlarm(alarm.id, isEnabled)
                    }
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material.TextButton(
                    onClick = { onEditAlarm(alarm.id) }
                ) {
                    Text("Editar")
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                androidx.compose.material.IconButton(
                    onClick = { onDeleteAlarm(alarm.id) }
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Excluir Alarme",
                        tint = MaterialTheme.colors.error
                    )
                }
            }
        }
    }
}
