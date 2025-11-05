package com.heypudu.heypudu.features.profile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    onGoToEdit: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pantalla de Perfil")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onGoToEdit) {
            Text("Editar Perfil")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(onGoToEdit = {}, onBack = {})
}
    