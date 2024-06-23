package com.example.parawaleapp.SendViewOrders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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

var Pending by mutableStateOf(true)
var Completed by mutableStateOf(false)
var Cancelled by mutableStateOf(false)


@Preview(showBackground = true)
@Composable
fun OrderStatusSelectBar() {

        Row(Modifier.height(48.dp)) {
            TextButton(
                onClick = {
                    Completed = false
                    Pending = true
                    Cancelled = false
                }, if (!Pending) {
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
                if (Pending) {
                    Text(
                        text = "Pending",
                        color = Color(0xFFB3172F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = "Pending",
                        color = Color(0xFF4D7467),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            TextButton(
                onClick = {
                    Completed = true
                    Pending = false
                    Cancelled = false
                }, if (!Completed) {
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
                if (Completed) {
                    Text(
                        text = "Completed",
                        color = Color(0xFFB3172F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = "Completed",
                        color = Color(0xFF4D7467),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

            }

            TextButton(
                onClick = {
                    Completed = false
                    Pending = false
                    Cancelled = true
                }, if (!Cancelled) {
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
                if (Cancelled) {
                    Text(
                        text = "Cancelled",
                        color = Color(0xFFB3172F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = "Cancelled",
                        color = Color(0xFF4D7467),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

}