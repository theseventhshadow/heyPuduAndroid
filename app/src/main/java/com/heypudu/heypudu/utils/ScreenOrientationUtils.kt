package com.heypudu.heypudu.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/*
 -- Busca la Activity actual desde el contexto de un Composable.
 */
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/*
 -- Un Composable que bloquea la orientación de la pantalla mientras está en la composición
    y la restaura cuando sale.

 -- @param orientation La orientación deseada, por ejemplo: ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
 */
@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity()

        activity?.let {
            val originalOrientation = it.requestedOrientation
            it.requestedOrientation = orientation

            onDispose {
                it.requestedOrientation = originalOrientation
            }
        } ?: onDispose {
        }
    }
}
    