package cn.huacalendar.app.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate

class HolidayRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun holidaysFor(year: Int, forceRefresh: Boolean = false): Map<LocalDate, HolidayInfo> =
        withContext(Dispatchers.IO) {
            val cache = context.filesDir.resolve("holidays-$year.json")
            val payload = if (!forceRefresh && cache.exists()) {
                cache.readText()
            } else {
                runCatching { download(year) }
                    .onSuccess { cache.writeText(it) }
                    .getOrElse { if (cache.exists()) cache.readText() else return@withContext emptyMap() }
            }
            runCatching {
                json.decodeFromString<HolidayYear>(payload).days.associate { day ->
                    LocalDate.parse(day.date) to HolidayInfo(day.name, day.isOffDay)
                }
            }.getOrDefault(emptyMap())
        }

    private fun download(year: Int): String {
        val connection = URL(
            "https://cdn.jsdelivr.net/gh/NateScarlet/holiday-cn@master/$year.json",
        ).openConnection() as HttpURLConnection
        return try {
            connection.connectTimeout = 8_000
            connection.readTimeout = 8_000
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/json")
            if (connection.responseCode !in 200..299) error("HTTP ${connection.responseCode}")
            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }
}

@Serializable
private data class HolidayYear(val days: List<HolidayDay>)

@Serializable
private data class HolidayDay(
    val name: String,
    val date: String,
    val isOffDay: Boolean,
)
