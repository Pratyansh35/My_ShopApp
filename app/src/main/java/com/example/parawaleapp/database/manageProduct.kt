package com.example.parawaleapp.database

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parawaleapp.sign_in.UserData

@Preview(showBackground = true)
@Composable
fun topBar() {
    Row(Modifier.height(54.dp)) {
        TextButton(
            onClick = {
                modify = false
                add = true
                delete = false
            }, if (!add) {
                Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            } else {
                Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFA8A16F), Color(0xFFD37B6E)
                            )
                        )
                    )
            }
        ) {
            if (add) {
                Text(
                    text = "add",
                    color = Color(0xFFB3172F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "add",
                    color = Color(0xFF4D7467),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
        TextButton(
            onClick = {
                modify = true
                add = false
                delete = false
            }, if (!modify) {
                Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            } else {
                Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFA8A16F), Color(0xFFD37B6E)
                            )
                        )
                    )
            }
        ) {
            if (modify) {
                Text(
                    text = "Modify",
                    color = Color(0xFFB3172F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Modify",
                    color = Color(0xFF4D7467),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

        }

        TextButton(
            onClick = {
                modify = false
                add = false
                delete = true
            }, if (!delete) {
                Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            } else {
                Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFA8A16F), Color(0xFFD37B6E)
                            )
                        )
                    )
            }
        ) {
            if (delete) {
                Text(
                    text = "Delete",
                    color = Color(0xFFB3172F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Delete",
                    color = Color(0xFF4D7467),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

var add by mutableStateOf(true)
var modify by mutableStateOf(false)
var delete by mutableStateOf(false)

@Composable
fun ManageItem(
    userData: UserData?, dishData: List<Dishfordb>
) {
    var selectedDish by remember { mutableStateOf<Dishfordb?>(null) }
    var showModifyScreen by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        topBar()
        if (!add) {
            if (showModifyScreen) {
                selectedDish?.let { dish ->
                    // Make sure that the ModifyScreen composable is defined correctly
                    ModifyScreen(dish = dish, showModifyScreen = {
                        showModifyScreen = false
                    })

                }
            } else if (!delete) {
                Column {
                    Divider(
                        modifier = Modifier.padding(8.dp), color = Color.Gray, thickness = 1.dp
                    )
                    LazyColumn {
                        items(dishData) { dish ->
                            // Ensure that the ModifyItemScreen composable is defined correctly
                            ModifyItemScreen(dish = dish, onEditClicked = {
                                selectedDish = dish
                                showModifyScreen = true
                            })
                        }
                    }
                }
            }
        } else {
            AddItemScreen(userData = userData)
        }
    }
}
