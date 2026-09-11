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
    fun `spring festival exposes lunar and almanac details`() {
        val detail = service.detail(LocalDate.of(2026, 2, 17))

        assertEquals("农历正月初一", detail.lunarDate)
        assertTrue(detail.festivals.contains("春节"))
        assertTrue(detail.suitable.isNotEmpty())
        assertTrue(detail.avoid.isNotEmpty())
    }
}
