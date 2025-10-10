package com.heypudu.heypudu.profile

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


@Composable
fun Profile(onProfileClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("¡Bienvenido a la pantalla del Perfil!")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onProfileClick) { // <- Usamos la función que recibimos
            Text("Ir al inicio")
        }
    }
}
