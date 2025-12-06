import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.heypudu.heypudu.ui.components.ProfileImage

@Composable
fun MainDrawer(
    onDestinationClick: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var username by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }

    LaunchedEffect(user?.uid) {
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users").document(user.uid)
                .get()
                .addOnSuccessListener { doc ->
                    username = doc.getString("username") ?: ""
                    photoUrl = doc.getString("photoUrl") ?: ""
                }
        }
    }

    ModalDrawerSheet(
        modifier = Modifier.width(280.dp),
        drawerContainerColor = Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF6A1B9A),
                            Color(0xFF4A148C)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                val context = LocalContext.current
                ProfileImage(
                    context = context,
                    userId = user?.uid,
                    photoUrl = photoUrl,
                    size = 80.dp,
                    modifier = Modifier
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(4.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = username.ifBlank { "Usuario" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user?.email ?: "",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            color = Color.Gray.copy(alpha = 0.2f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Opción: Inicio
        NavigationDrawerItem(
            label = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Inicio",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Inicio",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            },
            selected = false,
            onClick = { onDestinationClick("main_graph") },
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp)),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color(0xFFE91E63).copy(alpha = 0.3f),
                unselectedContainerColor = Color.Transparent,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.White
            )
        )

        // Opción: Perfil
        NavigationDrawerItem(
            label = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Perfil",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            },
            selected = false,
            onClick = {
                val userId = auth.currentUser?.uid
                if (!userId.isNullOrEmpty()) {
                    onDestinationClick("profile_graph/profile_view?userId=$userId")
                }
            },
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp)),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color(0xFFE91E63).copy(alpha = 0.3f),
                unselectedContainerColor = Color.Transparent,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.White
            )
        )

        // Opción: Eventos Musicales
        NavigationDrawerItem(
            label = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Newspaper,
                        contentDescription = "Eventos Musicales",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Eventos Musicales",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            },
            selected = false,
            onClick = { onDestinationClick("news_screen") },
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp)),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color(0xFFE91E63).copy(alpha = 0.3f),
                unselectedContainerColor = Color.Transparent,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            color = Color.Gray.copy(alpha = 0.2f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Opción: Cerrar sesión
        NavigationDrawerItem(
            label = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Cerrar sesión",
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "Cerrar sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFF6B6B)
                    )
                }
            },
            selected = false,
            onClick = { onDestinationClick("logout") },
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp)),
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color(0xFFFF6B6B).copy(alpha = 0.2f),
                unselectedContainerColor = Color.Transparent,
                selectedTextColor = Color(0xFFFF6B6B),
                unselectedTextColor = Color(0xFFFF6B6B)
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(
                    color = Color(0xFF6A1B9A).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = "heyPudú v1.0",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

