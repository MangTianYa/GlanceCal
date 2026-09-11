package cn.huacalendar.app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import cn.huacalendar.app.ui.CalendarApp
import cn.huacalendar.app.ui.theme.HuaCalendarTheme

class MainActivity : ComponentActivity() {
    private val viewModel: CalendarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val preferences = getSharedPreferences("appearance", MODE_PRIVATE)
            val systemDark = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                Configuration.UI_MODE_NIGHT_YES
            var darkTheme by rememberSaveable {
                mutableStateOf(preferences.getBoolean("dark_theme", systemDark))
            }
            var dynamicColor by rememberSaveable {
                mutableStateOf(preferences.getBoolean("dynamic_color", true))
            }

            HuaCalendarTheme(darkTheme = darkTheme, dynamicColor = dynamicColor) {
                CalendarApp(
                    viewModel = viewModel,
                    darkTheme = darkTheme,
                    dynamicColor = dynamicColor,
                    onDarkThemeChanged = { enabled ->
                        darkTheme = enabled
                        preferences.edit().putBoolean("dark_theme", enabled).apply()
                    },
                    onDynamicColorChanged = { enabled ->
                        dynamicColor = enabled
                        preferences.edit().putBoolean("dynamic_color", enabled).apply()
                    },
                )
            }
        }
    }
}
