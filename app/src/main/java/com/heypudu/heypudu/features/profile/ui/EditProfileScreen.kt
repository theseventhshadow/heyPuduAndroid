package com.heypudu.heypudu.features.profile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heypudu.heypudu.ui.components.ProfileImagePicker
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import android.net.Uri

@Composable
fun EditProfileScreen(
    onSave: () -> Unit
) {
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pantalla de Edición de Perfil")
        Spacer(modifier = Modifier.height(16.dp))
        ProfileImagePicker(imageUri = imageUri)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSave) {
            Text("Guardar y Volver")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditProfileScreenPreview() {
    EditProfileScreen(onSave = {})
}
