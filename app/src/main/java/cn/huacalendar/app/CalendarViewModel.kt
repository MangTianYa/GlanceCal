package cn.huacalendar.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cn.huacalendar.app.data.CalendarUiState
import cn.huacalendar.app.data.HolidayInfo
import cn.huacalendar.app.data.HolidayRepository
import cn.huacalendar.app.data.LunarCalendarService
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val lunarService = LunarCalendarService()
    private val holidayRepository = HolidayRepository(application)
    private val holidayCache = mutableMapOf<Int, Map<LocalDate, HolidayInfo>>()
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        showMonth(YearMonth.now())
    }

    fun previousMonth() = showMonth(_uiState.value.month.minusMonths(1))

    fun nextMonth() = showMonth(_uiState.value.month.plusMonths(1))

    fun goToToday() {
        val today = LocalDate.now()
        _uiState.update { it.copy(selectedDate = today) }
        showMonth(YearMonth.from(today))
    }

    fun selectDate(date: LocalDate) {
        if (YearMonth.from(date) != _uiState.value.month) {
            _uiState.update { it.copy(selectedDate = date) }
            showMonth(YearMonth.from(date))
            return
        }
        _uiState.update { it.copy(selectedDate = date, detail = lunarService.detail(date)) }
    }

    fun refresh() = showMonth(_uiState.value.month, forceRefresh = true)

    private fun showMonth(month: YearMonth, forceRefresh: Boolean = false) {
        val cachedHolidays = holidayCache.values
            .fold(emptyMap<LocalDate, HolidayInfo>()) { acc, map -> acc + map }
        _uiState.update {
            val selected = if (YearMonth.from(it.selectedDate) == month) it.selectedDate else month.atDay(1)
            it.copy(
                month = month,
                selectedDate = selected,
                days = lunarService.monthDays(month, cachedHolidays),
                previousMonthDays = lunarService.monthDays(month.minusMonths(1), cachedHolidays),
                nextMonthDays = lunarService.monthDays(month.plusMonths(1), cachedHolidays),
                detail = lunarService.detail(selected),
                isRefreshing = true,
            )
        }
        viewModelScope.launch {
            val years = setOf(month.minusMonths(1).year, month.year, month.plusMonths(1).year)
            val loaded = years.associateWith { year ->
                async { holidayRepository.holidaysFor(year, forceRefresh) }
            }.mapValues { it.value.await() }
            holidayCache.putAll(loaded)
            val holidays = loaded.values.fold(emptyMap<LocalDate, HolidayInfo>()) { acc, map -> acc + map }
            _uiState.update {
                if (it.month != month) it else it.copy(
                    days = lunarService.monthDays(month, holidays),
                    previousMonthDays = lunarService.monthDays(month.minusMonths(1), holidays),
                    nextMonthDays = lunarService.monthDays(month.plusMonths(1), holidays),
                    holidayDataAvailable = holidays.isNotEmpty(),
                    isRefreshing = false,
                )
            }
        }
    }
}
