package com.mioshek.theclock.controllers

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mioshek.theclock.db.models.Alarms
import com.mioshek.theclock.db.models.AlarmsRepository
import kotlinx.coroutines.launch

data class AlarmsUiState(
    val id: Int = 0,
    val name: String? = null,
    val time: Int = 0,
    val daysOfWeek: Array<Boolean> = Array(7) { false },
    val sound: String? = null,
    val enabled: Boolean
)


class AlarmsListViewModel(private val alarmsRepository: AlarmsRepository): ViewModel() {
    private val _alarms = mutableStateListOf<AlarmsUiState>()
    val alarms: List<AlarmsUiState> = _alarms


    fun toggleAlarm(index: Int){
        val previousState = _alarms[index]
        _alarms[index] = previousState.copy(enabled = !previousState.enabled)
    }

    fun upsert(alarm: AlarmsUiState){
        _alarms.add(alarm)
        viewModelScope.launch {
            alarmsRepository.upsert(
                Alarms(
                    name = alarm.name,
                    time = alarm.time,
                    daysOfWeek = 1,
                    sound = alarm.sound,
                    enabled = alarm.enabled
                )
            )
        }
    }

    fun encodeDaysOfWeek(daysOfWeek: Array<Boolean>): Int {
        var encodedDays = 0
        for ((index, shouldRing) in daysOfWeek.withIndex()) {
            if (shouldRing) {
                encodedDays += 1 shl (daysOfWeek.size - 1 - index)
            }
        }
        return encodedDays
    }


}