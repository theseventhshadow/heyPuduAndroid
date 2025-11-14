package com.heypudu.heypudu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.heypudu.heypudu.navigation.AppNavigation
import com.heypudu.heypudu.ui.theme.HeyPudúTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    companion object {
        var isReady: Boolean by mutableStateOf(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("MainActivity", "onCreate: INICIO")
        val splashScreen = installSplashScreen()
        android.util.Log.d("MainActivity", "SplashScreen instalado")
        splashScreen.setKeepOnScreenCondition { !isReady }
        android.util.Log.d("MainActivity", "SplashScreen condición configurada")
        WindowCompat.setDecorFitsSystemWindows(window, false)
        android.util.Log.d("MainActivity", "DecorFitsSystemWindows configurado")
        setContent {
            HeyPudúTheme {
                AppNavigation()
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    android.util.Log.d("MainActivity", "Compose listo, liberando SplashScreen")
                    isReady = true
                }
            }
        }
        android.util.Log.d("MainActivity", "setContent ejecutado")
    }
}
