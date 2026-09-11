package cn.huacalendar.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.huacalendar.app.CalendarViewModel
import cn.huacalendar.app.data.AlmanacDetail
import cn.huacalendar.app.data.CalendarDay
import cn.huacalendar.app.data.CalendarUiState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ChineseLocale = Locale.SIMPLIFIED_CHINESE
private val MonthFormatter = DateTimeFormatter.ofPattern("yyyy年 M月", ChineseLocale)
private val DetailDateFormatter = DateTimeFormatter.ofPattern("M月d日 EEEE", ChineseLocale)

@Composable
fun CalendarApp(
    viewModel: CalendarViewModel,
    darkTheme: Boolean,
    dynamicColor: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CalendarScreen(
        state = state,
        onPreviousMonth = viewModel::previousMonth,
        onNextMonth = viewModel::nextMonth,
        onToday = viewModel::goToToday,
        onRefresh = viewModel::refresh,
        onDateSelected = viewModel::selectDate,
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        onDarkThemeChanged = onDarkThemeChanged,
        onDynamicColorChanged = onDynamicColorChanged,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarScreen(
    state: CalendarUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToday: () -> Unit,
    onRefresh: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    darkTheme: Boolean,
    dynamicColor: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("日历", fontWeight = FontWeight.SemiBold)
                        Text(
                            "农历 · 黄历 · 节假日",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onDarkThemeChanged(!darkTheme) }) {
                        Icon(
                            imageVector = if (darkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                            contentDescription = if (darkTheme) "切换浅色模式" else "切换深色模式",
                        )
                    }
                    IconToggleButton(
                        checked = dynamicColor,
                        onCheckedChange = onDynamicColorChanged,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Palette,
                            contentDescription = if (dynamicColor) "关闭动态取色" else "开启动态取色",
                            tint = if (dynamicColor) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                    if (state.isRefreshing) {
                        CircularProgressIndicator(Modifier.padding(14.dp).size(20.dp), strokeWidth = 2.dp)
                    } else {
                        IconButton(onClick = onRefresh) {
                            Icon(Icons.Rounded.Refresh, contentDescription = "更新节假日")
                        }
                    }
                    FilledTonalButton(
                        onClick = onToday,
                        modifier = Modifier.padding(end = 12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp),
                    ) {
                        Icon(Icons.Rounded.CalendarToday, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("今天")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
            )
        },
    ) { contentPadding ->
        BoxWithResponsiveLayout(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            calendar = {
                CalendarPanel(
                    state = state,
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth,
                    onDateSelected = onDateSelected,
                )
            },
            detail = { state.detail?.let { AlmanacPanel(it, state.days.firstOrNull { day -> day.date == it.date }) } },
        )
    }
}

@Composable
private fun BoxWithResponsiveLayout(
    modifier: Modifier,
    calendar: @Composable () -> Unit,
    detail: @Composable () -> Unit,
) {
    androidx.compose.foundation.layout.BoxWithConstraints(modifier) {
        if (maxWidth >= 840.dp) {
            Row(
                Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Box(Modifier.weight(1.25f).fillMaxHeight()) { calendar() }
                Box(Modifier.weight(0.75f).fillMaxHeight()) { detail() }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { calendar() }
                item { detail() }
            }
        }
    }
}

@Composable
private fun CalendarPanel(
    state: CalendarUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val previousMonth by rememberUpdatedState(onPreviousMonth)
    val nextMonth by rememberUpdatedState(onNextMonth)

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                when (page) {
                    0 -> previousMonth()
                    2 -> nextMonth()
                }
            }
    }
    LaunchedEffect(state.month) {
        if (pagerState.currentPage != 1 || pagerState.currentPageOffsetFraction != 0f) {
            pagerState.scrollToPage(1)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(Modifier.padding(vertical = 16.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { scope.launch { pagerState.animateScrollToPage(0) } }) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "上个月")
                }
                AnimatedContent(
                    targetState = state.month,
                    modifier = Modifier.weight(1f).clipToBounds(),
                    transitionSpec = {
                        val forward = targetState > initialState
                        (slideInHorizontally(tween(260)) { if (forward) it / 3 else -it / 3 } + fadeIn(tween(180)))
                            .togetherWith(
                                slideOutHorizontally(tween(220)) { if (forward) -it / 3 else it / 3 } +
                                    fadeOut(tween(140)),
                            )
                    },
                    label = "月份标题",
                ) { month ->
                    Text(
                        month.format(MonthFormatter),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                IconButton(onClick = { scope.launch { pagerState.animateScrollToPage(2) } }) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = "下个月")
                }
            }
            Spacer(Modifier.height(8.dp))
            WeekHeader()
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().clipToBounds(),
                beyondViewportPageCount = 1,
                pageSpacing = 8.dp,
            ) { page ->
                val days = when (page) {
                    0 -> state.previousMonthDays
                    2 -> state.nextMonthDays
                    else -> state.days
                }
                CalendarMonthGrid(
                    days = days,
                    selectedDate = state.selectedDate,
                    onDateSelected = onDateSelected,
                )
            }
            if (!state.holidayDataAvailable && !state.isRefreshing) {
                Text(
                    "节假日数据暂不可用，农历与黄历仍可离线使用",
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun CalendarMonthGrid(
    days: List<CalendarDay>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
        days.chunked(7).forEach { week ->
            Row(Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    DayCell(
                        day = day,
                        selected = day.date == selectedDate,
                        today = day.date == LocalDate.now(),
                        onClick = { onDateSelected(day.date) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekHeader() {
    Row(Modifier.fillMaxWidth()) {
        listOf("一", "二", "三", "四", "五", "六", "日").forEachIndexed { index, label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = if (index >= 5) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DayCell(
    day: CalendarDay,
    selected: Boolean,
    today: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val weekend = day.date.dayOfWeek.value >= 6
    val holidayColor = if (day.holiday?.isOffDay == false) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.primary
    }
    Box(
        modifier = modifier.height(70.dp).padding(2.dp).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Box(
                Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(18.dp)),
            )
        }
        Column(
            modifier = Modifier.alpha(if (day.inCurrentMonth) 1f else 0.38f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (today && !selected) {
                    Box(Modifier.size(31.dp).background(MaterialTheme.colorScheme.secondaryContainer, CircleShape))
                }
                Text(
                    day.date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (today || selected) FontWeight.Bold else FontWeight.Medium,
                    color = when {
                        selected -> MaterialTheme.colorScheme.onPrimaryContainer
                        weekend -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                )
            }
            Text(
                day.lunarLabel,
                modifier = Modifier.widthIn(max = 52.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
                color = if (day.festival != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        day.holiday?.let { holiday ->
            Text(
                text = if (holiday.isOffDay) "休" else "班",
                modifier = Modifier.align(Alignment.TopEnd)
                    .background(holidayColor, CircleShape)
                    .padding(horizontal = 4.dp, vertical = 1.dp),
                color = MaterialTheme.colorScheme.surface,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlmanacPanel(detail: AlmanacDetail, calendarDay: CalendarDay?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            detail.date.dayOfMonth.toString(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        detail.date.format(DetailDateFormatter),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        "${detail.lunarDate} · ${detail.zodiac}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                calendarDay?.holiday?.let {
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                        Text(
                            if (it.isOffDay) "休" else "班",
                            Modifier.padding(10.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                detail.solarTerm?.let { AssistChip(onClick = {}, label = { Text(it) }) }
                detail.festivals.forEach { AssistChip(onClick = {}, label = { Text(it) }) }
                calendarDay?.holiday?.let { AssistChip(onClick = {}, label = { Text(it.name) }) }
            }
            Text(
                detail.ganZhi,
                modifier = Modifier.padding(vertical = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))
            AlmanacList("宜", detail.suitable, MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(14.dp))
            AlmanacList("忌", detail.avoid, MaterialTheme.colorScheme.tertiary)
            Spacer(Modifier.height(18.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))
            InfoRow("值日", "${detail.dayOfficer} · ${detail.dayGod}")
            InfoRow("冲煞", "${detail.clash} · ${detail.sha}")
            InfoRow("胎神", detail.fetalGod)
            InfoRow("方位", "${detail.luckyGod} · ${detail.wealthGod}")
        }
    }
}

@Composable
private fun AlmanacList(title: String, values: List<String>, color: Color) {
    Row(verticalAlignment = Alignment.Top) {
        Surface(shape = CircleShape, color = color) {
            Text(
                title,
                Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                color = MaterialTheme.colorScheme.surface,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(values.joinToString("  "), Modifier.weight(1f), lineHeight = 24.sp)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Text(label, Modifier.width(52.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, Modifier.weight(1f))
    }
}
