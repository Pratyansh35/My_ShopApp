package com.example.parawaleapp.PaymentUpi

import Phonepe
import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parawaleapp.SendViewOrders.sendOrders
import com.example.parawaleapp.database.Dishfordb
import com.example.parawaleapp.sign_in.UserData
import java.util.UUID

@Composable
fun PaymentScreenLayout(
    totalMrp: Double,
    totalValue: Double,
    userData: UserData?,
    cartItems: List<Dishfordb>
) {

    val merchantId = "PGTESTPAYUAT"
    val saltkey = "099eb0cd-02cf-4e2a-8aca-3e6c6aff0399"
    val merchantTransactionId = UUID.randomUUID().toString()
    val apiEndPoint = "/pg/v1/pay"

    val context = LocalContext.current
    var specialCode by remember { mutableStateOf("") }
    var selectedPercentage by remember { mutableStateOf(10f) }
    val selectedAmount = totalValue * (selectedPercentage / 100)

    var showDialog by remember { mutableStateOf(false) }
    var transactionResult by remember { mutableStateOf(TransactionResult("", "", "")) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data: Intent? = result.data
            val response = data?.getStringExtra("response") ?: "nothing"
            handleUPIResponse(context, response, userData, merchantId, totalMrp, totalValue, selectedAmount) { result ->
                transactionResult = result
                showDialog = true
            }
        } else {
            handleUPIResponse(context, "nothing", userData, merchantId, totalMrp, totalValue, selectedAmount) { result ->
                transactionResult = result
                showDialog = true
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp), color = MaterialTheme.colors.background
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Payment Information",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Note: You have to pay between 10% to 100% of the total bill to the merchant to book your order",
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center
                        )
                    }
                    PaymentInfoRow(label = "Your total bill is", value = String.format("%.2f", totalValue))
                    PaymentInfoRow(label = "You have to pay", value = String.format("%.2f", selectedAmount))
                    PaymentInfoRow(label = "Remaining Amount", value = String.format("%.2f", totalValue - selectedAmount))
                    Slider(
                        value = selectedPercentage,
                        onValueChange = { selectedPercentage = it },
                        valueRange = 10f..100f,
                        steps = 9,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                    Text(
                        text = "Selected: ${selectedPercentage.toInt()}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            OutlinedTextField(
                value = specialCode,
                onValueChange = { specialCode = it },
                label = { Text(text = "Have Special Code") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )
            Text(
                text = "Pay ₹${String.format("%.2f", selectedAmount)} using these UPI apps:",
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                UPIIconButton(
                    specialCode = specialCode,
                    context = context,
                    userData = userData,
                    cartItems = cartItems,
                    totalMrp = totalMrp,
                    totalValue = totalValue,
                    merchantId = merchantId,
                    selectedAmount = selectedAmount,
                    launcher = launcher,
                    iconRes = com.example.parawaleapp.R.drawable.bhimupi,
                    onClick = { vpa, name, note, merchantTransactionId, transactionUrl ->
                        payUsingUPI(
                            context,
                            launcher,
                            String.format("%.2f", selectedAmount),
                            vpa,
                            name,
                            note,
                            merchantTransactionId,
                            transactionUrl
                        )
                    }
                )
                UPIIconButton(
                    specialCode = specialCode,
                    context = context,
                    userData = userData,
                    cartItems = cartItems,
                    totalMrp = totalMrp,
                    totalValue = totalValue,
                    merchantId = merchantId,
                    selectedAmount = selectedAmount,
                    launcher = launcher,
                    iconRes = com.example.parawaleapp.R.drawable.phonepeicon,
                    onClick = { vpa, name, note, merchantTransactionId, transactionUrl ->
                        Phonepe(
                            context = context,
                            amount = selectedAmount,
                            merchantId = merchantId,
                            merchantTransactionId = merchantTransactionId,
                            callbackUrl = "https://webhook.site/374d1a7f-5fd2-438a-840a-761714a04403",
                            mobileNumber = 7007254934.toString()
                        )
                    }
                )
            }
        }
    }

    if (showDialog) {
        TransactionResultDialog(result = transactionResult, onDismiss = { showDialog = false })
    }
}

@Composable
fun UPIIconButton(
    specialCode: String,
    context: Context,
    userData: UserData?,
    cartItems: List<Dishfordb>,
    totalMrp: Double,
    totalValue: Double,
    merchantId: String,
    selectedAmount: Double,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    iconRes: Int,
    onClick: (vpa: String, name: String, note: String, transactionId: String, transactionUrl: String) -> Unit
) {
    IconButton(onClick = {
        if (specialCode.replace(" ","").lowercase().trim() == "parawalespecial") {
            sendOrders(
                context,
                userData,
                cartItems,
                totalMrp,
                totalValue,
                System.currentTimeMillis().toString(),
                merchantId,
                amountReceived = "0",
                amountRemaining = totalValue.toString()
            )
        } else {
            onClick(
                "Q534044683@ybl",
                "PhonePeMerchant",
                "Booking order",
                UUID.randomUUID().toString(),
                "https://webhook.site/374d1a7f-5fd2-438a-840a-761714a04403"
            )
        }
    }) {
        Card(
            elevation = 4.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "UPI App Icon",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun TransactionResultDialog(result: TransactionResult, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Transaction Result") },
        text = {
            Column {
                Text(text = "Status: ${result.status}")
                Text(text = "Transaction ID: ${result.transactionId}")
                Text(text = "Message: ${result.message}")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

data class TransactionResult(val status: String, val transactionId: String, val message: String)

@Composable
fun PaymentInfoRow(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = label, fontWeight = FontWeight.Medium, fontSize = 16.sp)
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.width(150.dp)
        )
    }
}


