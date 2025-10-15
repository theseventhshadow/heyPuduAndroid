package com.heypudu.heypudu.features.onboarding.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heypudu.heypudu.R
import com.heypudu.heypudu.ui.theme.HeyPudúTheme


/*
    -- Funcion de la pantalla de bienvenida --
 */

@Composable
fun GreetingScreen(
    onContinueClick: () -> Unit,
    onProfileCreated: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient_transition")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1500f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, delayMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_offset"
    )
    /*
       -- Box para el fondo del botón --
     */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFA76A6),
                        Color(0xFF33E7B2)
                    ),
                    startY = 0f + offset,
                    endY = 1500f + offset
                )
            ),
    ) {
        /*
         -- Un 'Column' para organizar los elementos verticalmente --
         */
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            /*
                -- El Logo
             */
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Image(
                    painter = painterResource(id = R.drawable.ic_pudu_logo),
                    contentDescription = "Logo de HeyPudú",
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(32.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(32.dp))

                /*
                    -- El Título
                 */
                Text(
                    text = "¡Bienvenido a heyPudú!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                /*
                 -- El Subtítulo o texto descriptivo
                 */
                Text(
                    text = "Escucha. Hazte Escuchar...",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(48.dp))

                /*
                    -- El Botón con estilo
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
                ) {
                    Text(
                        text = "Registrarse",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onContinueClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =  Color( 0xFFE91E63)
                    ),
                        modifier = Modifier
                            .height(50.dp)
                            .fillMaxWidth()
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            }


            Spacer(modifier = Modifier.weight(1f))


            Text(
                text= "Created by a bunch of pudús",
                fontSize = 12.sp,
                fontWeight = FontWeight.W900,
                color = Color.Gray
            )
        }
    }

}

/*
    -- La vista previa actualizada para reflejar el nuevo diseño.
 */
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun GreetingScreenPreview() {
    HeyPudúTheme {
        GreetingScreen(
            onProfileCreated = {},
            onContinueClick = {}
        )
    }
}
