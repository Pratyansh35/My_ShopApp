package com.example.parawaleapp.drawerPanel.leftPanel

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.parawaleapp.database.SlidesItems
import com.example.parawaleapp.database.img
import com.example.parawaleapp.database.name
import com.example.parawaleapp.mainScreen.MenuSlide
import com.example.parawaleapp.sign_in.UserData
import com.example.parawaleapp.sign_in.listofAuthorizedUsersEmails
import kotlinx.coroutines.CoroutineScope

@Composable
fun LeftDrawerPanel(
    scaffoldState: ScaffoldState,
    navController: NavController? = null,
    scope: CoroutineScope,
    userData: UserData?,
    signOut: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .scrollable(
                rememberScrollState(),
                orientation = Orientation.Vertical
            )
    ) {
        Card(
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            listOf(MaterialTheme.colors.primaryVariant, MaterialTheme.colors.secondary)
                        )
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = if (img.toString().isEmpty()) {
                        userData?.progilePictureUrl
                    } else img,
                    contentDescription = "userImage",
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(110.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .height(100.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    if (userData?.userName != null) {
                        Text(
                            text = if (name.isEmpty()) {
                                userData.userName
                            } else name,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                    if (userData?.userEmail != null) {
                        Text(
                            text = userData.userEmail,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colors.onBackground
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(top = 10.dp)
                .scrollable(
                    rememberScrollState(),
                    orientation = Orientation.Vertical
                )
        ) {
            items(SlidesItems) { Slidess ->
                val isAuthorized = Slidess.Type !in listOf(
                    "Customers Order",
                    "Add Items",
                    "Connect Printer"
                ) || (Slidess.Type in listOf(
                    "Customers Order",
                    "Add Items",
                    "Connect Printer"
                ) && listofAuthorizedUsersEmails.contains(userData?.userEmail))

                if (isAuthorized) {
                    MenuSlide(
                        Slidess,
                        scope = scope,
                        scaffoldState = scaffoldState,
                        navController = navController
                    )
                }
            }

            item {
                Button(
                    onClick = { signOut() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                    shape = RoundedCornerShape(40),
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "LOG OUT", color = MaterialTheme.colors.onPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
