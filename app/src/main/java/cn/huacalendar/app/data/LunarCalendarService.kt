package cn.huacalendar.app.data

import com.nlf.calendar.Solar
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

class LunarCalendarService {
    fun monthDays(month: YearMonth, holidays: Map<LocalDate, HolidayInfo>): List<CalendarDay> {
        val first = month.atDay(1)
        val gridStart = first.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return (0L until 42L).map { offset ->
            val date = gridStart.plusDays(offset)
            val lunar = lunar(date)
            val festival = buildList {
                addAll(lunar.festivals)
                if (lunar.jie.isNotBlank()) add(lunar.jie)
                if (lunar.qi.isNotBlank()) add(lunar.qi)
                addAll(lunar.solar.festivals)
            }.firstOrNull()
            CalendarDay(
                date = date,
                inCurrentMonth = YearMonth.from(date) == month,
                lunarLabel = festival ?: lunar.dayInChinese,
                festival = festival,
                holiday = holidays[date] ?: if (date.dayOfWeek.value >= 6) {
                    HolidayInfo(name = "周末", isOffDay = true)
                } else {
                    null
                },
            )
        }
    }

    fun detail(date: LocalDate): AlmanacDetail {
        val lunar = lunar(date)
        val solarTerm = (lunar.jie.ifBlank { lunar.qi }).ifBlank { null }
        val festivals = (lunar.festivals + lunar.otherFestivals + lunar.solar.festivals).distinct()
        return AlmanacDetail(
            date = date,
            lunarDate = "农历${lunar.monthInChinese}月${lunar.dayInChinese}",
            ganZhi = "${lunar.yearInGanZhi}年 ${lunar.monthInGanZhi}月 ${lunar.dayInGanZhi}日",
            zodiac = "${lunar.yearShengXiao}年",
            solarTerm = solarTerm,
            festivals = festivals,
            suitable = lunar.dayYi,
            avoid = lunar.dayJi,
            clash = "冲${lunar.dayChongDesc}",
            sha = "煞${lunar.daySha}",
            fetalGod = lunar.dayPositionTai,
            luckyGod = "喜神${lunar.dayPositionXiDesc}",
            wealthGod = "财神${lunar.dayPositionCaiDesc}",
            dayOfficer = "${lunar.zhiXing}日",
            dayGod = "${lunar.dayTianShen} · ${lunar.dayTianShenType}",
        )
    }

    private fun lunar(date: LocalDate) = Solar.fromYmd(date.year, date.monthValue, date.dayOfMonth).lunar
}
