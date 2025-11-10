package com.heypudu.heypudu.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MainDrawer(
    onDestinationClick: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var username = remember { mutableStateOf("") }
    var photoUrl = remember { mutableStateOf("") }

    LaunchedEffect(user?.uid) {
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    username.value = doc.getString("username") ?: ""
                    photoUrl.value = doc.getString("photoUrl") ?: ""
                }
        }
    }

    ModalDrawerSheet {
        // Header con foto y nombre
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Image(
                painter = if (photoUrl.value.isNotEmpty()) rememberAsyncImagePainter(photoUrl.value)
                          else rememberAsyncImagePainter(model = "https://ui-avatars.com/api/?name=${username.value}&background=E91E63&color=fff&size=128"),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.padding(start = 12.dp))
            Text(
                text = username.value,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        NavigationDrawerItem(
            label = { Text("Inicio") },
            selected = false,
            onClick = { onDestinationClick("main_graph") },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color(0xFF33E7B2),
                unselectedContainerColor = Color.White
            )
        )
        NavigationDrawerItem(
            label = { Text("Perfil") },
            selected = false,
            onClick = { onDestinationClick("profile_graph") },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color(0xFF33E7B2),
                unselectedContainerColor = Color.White
            )
        )
        NavigationDrawerItem(
            label = { Text("Cerrar sesión") },
            selected = false,
            onClick = { onDestinationClick("logout") },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color(0xFFFA76A6),
                unselectedContainerColor = Color.White
            )
        )
    }
}
