package com.heypudu.heypudu.features.onboarding.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heypudu.heypudu.R
import com.heypudu.heypudu.ui.theme.HeyPudúTheme

/*
 --- PANTALLA DE CREACIÓN DE PERFIL ---
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProfileScreen(
    onProfileCreated: () -> Unit
) {
    // --- ESTADOS PARA GUARDAR LOS DATOS DEL USUARIO ---

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFA76A6),
                        Color(0xFF33E7B2)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*
             --- TÍTULO DE LA PANTALLA ---
             */
            Text(
                text = "Crea tu Perfil",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 32.dp, bottom = 24.dp)
            )

            /*
             --- SELECCIÓN DE FOTO DE PERFIL ---
             */
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .border(2.dp, Color(0xFFE91E63), CircleShape)
                    .clickable { /* TODO: Lógica para abrir la galería */ },
                contentAlignment = Alignment.Center
            ) {
                /*
                 Aquí podrías mostrar la imagen seleccionada. Por ahora, un icono.
                */
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_photo),
                    contentDescription = "Añadir foto de perfil",
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            /*
             --- CAMPO DE TEXTO PARA NOMBRE DE USUARIO ---
            */
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Ingresa tu contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = PasswordVisualTransformation()
            )

            /*
             --- CAMPO DE TEXTO PARA NOMBRE COMPLETO ---
            */
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Nombre completo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            /*
             --- CAMPO DE TEXTO PARA BIOGRAFÍA ---
            */
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Descríbete en pocas palabras...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp)
            )


            /*
             --- BOTÓN PARA GUARDAR EL PERFIL ---
            */
            Button(
                onClick = onProfileCreated,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE91E63)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Guardar y Continuar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


/*
 --- VISTA PREVIA PARA DISEÑAR EN AISLADO ---
*/
@Preview(showBackground = true)
@Composable
private fun CreateProfileScreenPreview() {
    HeyPudúTheme {
        CreateProfileScreen(onProfileCreated = {})
    }
}
