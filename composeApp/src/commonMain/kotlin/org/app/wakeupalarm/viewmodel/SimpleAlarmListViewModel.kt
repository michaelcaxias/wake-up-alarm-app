package org.app.wakeupalarm.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.app.wakeupalarm.domain.model.Alarm
import org.app.wakeupalarm.domain.model.DayOfWeek
import org.app.wakeupalarm.domain.usecase.ScheduleAlarmUseCase
import org.app.wakeupalarm.presentation.alarm.state.AlarmListState
import org.app.wakeupalarm.presentation.alarm.state.AlarmUiModel
import org.app.wakeupalarm.presentation.alarm.viewmodel.AlarmListViewModel
import org.app.wakeupalarm.service.AlarmScheduler

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
