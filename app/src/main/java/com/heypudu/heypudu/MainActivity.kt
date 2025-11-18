package com.heypudu.heypudu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.heypudu.heypudu.navigation.AppNavigation
import com.heypudu.heypudu.ui.theme.HeyPudúTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("MainActivity", "onCreate: INICIO")

        val splashScreen = installSplashScreen()
        android.util.Log.d("MainActivity", "SplashScreen instalado")

        // Liberar el SplashScreen inmediatamente
        splashScreen.setKeepOnScreenCondition { false }
        android.util.Log.d("MainActivity", "SplashScreen liberado")

        WindowCompat.setDecorFitsSystemWindows(window, false)
        android.util.Log.d("MainActivity", "DecorFitsSystemWindows configurado")

        setContent {
            android.util.Log.d("MainActivity", "setContent: iniciando")
            HeyPudúTheme {
                android.util.Log.d("MainActivity", "HeyPudúTheme aplicado, renderizando AppNavigation")
                AppNavigation()
                android.util.Log.d("MainActivity", "AppNavigation renderizado")
            }
        }
        android.util.Log.d("MainActivity", "setContent ejecutado")
    }
}
