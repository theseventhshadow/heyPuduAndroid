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
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Simplemente instala la splash screen, sin condiciones de espera.
        // Esto mostrará la nativa (rosa) muy brevemente y luego le pasará el control a Compose.
        installSplashScreen()

        setContent {
            HeyPudúTheme {
                AppNavigation()
            }
        }
    }
}
