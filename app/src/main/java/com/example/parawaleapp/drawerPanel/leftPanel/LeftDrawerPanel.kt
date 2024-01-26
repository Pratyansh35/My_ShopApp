package com.example.parawaleapp.drawerPanel.leftPanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.parawaleapp.database.SlidesItems
import com.example.parawaleapp.database.clearDataFromSharedPreferences
import com.example.parawaleapp.database.name
import com.example.parawaleapp.mainScreen.MenuSlide
import com.example.parawaleapp.sign_in.UserData
import kotlinx.coroutines.CoroutineScope

@Composable
fun LeftDrawerPanel(
    scaffoldState: ScaffoldState,
    navController: NavController? = null,
    scope: CoroutineScope,
    userData: UserData?,
    signOut: () -> Unit
) {
    val context = LocalContext.current
    Column(
        Modifier.fillMaxWidth()
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
                            listOf(Color(0xFF0E0A0B), Color(0xFF707A6D))
                        )
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically


            ) {
                AsyncImage(
                    model = userData?.progilePictureUrl,
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
                            text = if (name == "") {
                                userData.userName
                            } else (name),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFC0B445)
                        )
                    }
                    if (userData?.userEmail != null) {
                        Text(
                            text = userData.userEmail,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            textAlign = TextAlign.Center,
                            color = Color(0xFFC9B9B9)

                        )
                    }

                }
            }
        }
        LazyColumn(modifier = Modifier.padding(top = 15.dp)) {
            items(SlidesItems) { Slidess ->
                MenuSlide(
                    Slidess,
                    scope = scope,
                    scaffoldState = scaffoldState,
                    navController = navController
                )
            }
        }


        Row(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Button(
                onClick = {
                    signOut()
                    clearDataFromSharedPreferences(context)
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF4CE14)),
                shape = RoundedCornerShape(40),
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = "LOG_OUT", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}