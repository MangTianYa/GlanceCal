package cn.huacalendar.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class LunarCalendarServiceTest {
    private val service = LunarCalendarService()

    @Test
    fun `month grid has six complete weeks starting on monday`() {
        val days = service.monthDays(YearMonth.of(2026, 9), emptyMap())

        assertEquals(42, days.size)
        assertEquals(LocalDate.of(2026, 8, 31), days.first().date)
        assertEquals(1, days.first().date.dayOfWeek.value)
    }

    @Test
    fun `weekends are off days unless overridden by adjusted workday`() {
        val saturday = LocalDate.of(2026, 9, 5)
        val sunday = LocalDate.of(2026, 9, 6)
        val adjustedWorkday = HolidayInfo(name = "中秋节调休", isOffDay = false)
        val days = service.monthDays(
            YearMonth.of(2026, 9),
            mapOf(saturday to adjustedWorkday),
        ).associateBy { it.date }

        assertEquals(adjustedWorkday, days.getValue(saturday).holiday)
        assertEquals(HolidayInfo(name = "周末", isOffDay = true), days.getValue(sunday).holiday)
    }

    @Test
    fun `spring festival exposes lunar and almanac details`() {
        val detail = service.detail(LocalDate.of(2026, 2, 17))

        assertEquals("农历正月初一", detail.lunarDate)
        assertTrue(detail.festivals.contains("春节"))
        assertTrue(detail.suitable.isNotEmpty())
        assertTrue(detail.avoid.isNotEmpty())
    }
}
