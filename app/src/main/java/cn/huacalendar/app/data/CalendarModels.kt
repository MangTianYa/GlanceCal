package cn.huacalendar.app.data

import java.time.LocalDate
import java.time.YearMonth

data class HolidayInfo(
    val name: String,
    val isOffDay: Boolean,
)

data class CalendarDay(
    val date: LocalDate,
    val inCurrentMonth: Boolean,
    val lunarLabel: String,
    val festival: String?,
    val holiday: HolidayInfo?,
)

data class AlmanacDetail(
    val date: LocalDate,
    val lunarDate: String,
    val ganZhi: String,
    val zodiac: String,
    val solarTerm: String?,
    val festivals: List<String>,
    val suitable: List<String>,
    val avoid: List<String>,
    val clash: String,
    val sha: String,
    val fetalGod: String,
    val luckyGod: String,
    val wealthGod: String,
    val dayOfficer: String,
    val dayGod: String,
)

data class CalendarUiState(
    val month: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val days: List<CalendarDay> = emptyList(),
    val previousMonthDays: List<CalendarDay> = emptyList(),
    val nextMonthDays: List<CalendarDay> = emptyList(),
    val detail: AlmanacDetail? = null,
    val isRefreshing: Boolean = false,
    val holidayDataAvailable: Boolean = false,
)
