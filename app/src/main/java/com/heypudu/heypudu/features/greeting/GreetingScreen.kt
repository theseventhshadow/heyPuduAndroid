package com.heypudu.heypudu.features.greeting // <-- Fíjate que el paquete es correcto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heypudu.heypudu.ui.theme.HeyPudúTheme
import androidx.compose.material3.MaterialTheme


/**
 * La pantalla de saludo. Ahora acepta una función lambda `onProfileClick`.
 * Esta es la acción que se ejecutará cuando el usuario presione el botón.
 */
@Composable
fun GreetingScreen(onProfileClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("¡Bienvenido a HeyPudú!",
            style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onProfileClick) { // <- Usamos la función que recibimos
            Text("Ir a mi Perfil")
        }
    }
}

/**
 * La vista previa también necesita ser actualizada.
 * Como no navega, simplemente le pasamos una lambda vacía {}.
 */
@Preview(showBackground = true)
@Composable
private fun GreetingScreenPreview() {
    HeyPudúTheme {
        GreetingScreen(onProfileClick = {})
    }
}
