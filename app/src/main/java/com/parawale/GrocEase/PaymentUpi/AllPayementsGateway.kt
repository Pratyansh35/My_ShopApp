package com.parawale.GrocEase.PaymentUpi

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.util.Log
import com.parawale.GrocEase.DataClasses.Dishfordb
import com.parawale.GrocEase.DataClasses.UserAddressDetails
import com.parawale.GrocEase.DataClasses.UserData
import com.parawale.GrocEase.Notifications.sendNotificationToUser
import com.parawale.GrocEase.R
import com.parawale.GrocEase.SendViewOrders.sendOrders
import com.parawale.GrocEase.sign_in.PhoneNumberLinkingDialog
import com.parawale.GrocEase.sign_in.SignInState
import com.parawale.GrocEase.sign_in.SignInViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun PaymentScreenLayout(
    totalMrp: Double,
    totalValue: Double,
    userData: UserData?,
    cartItems: SnapshotStateList<Dishfordb>,
    isDarkTheme: Boolean,
    state: SignInState,
    linkWithOtpClick: (String) -> Unit = {},
    onSendVerificationCodeClick: (String) -> Unit,
    location: UserAddressDetails?,
    merchantCode: String
) {
    val merchantId = "PGTESTPAYUAT"

    val context = LocalContext.current
    var specialCode by remember { mutableStateOf("") }
    var selectedPercentage by remember { mutableFloatStateOf(10f) }
    val selectedAmount = totalValue * (selectedPercentage / 100)

    var showDialog by remember { mutableStateOf(false) }
    var transactionResult by remember { mutableStateOf(TransactionResult("", "", "")) }
    var showPhoneLinkDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val showAddressDialog by remember { mutableStateOf(false) }
    if (showPhoneLinkDialog) {
        PhoneNumberLinkingDialog(
            state = state,
            onDismissRequest = { showPhoneLinkDialog = false },
            onSendVerificationCodeClick = { phoneNumber ->
                onSendVerificationCodeClick(phoneNumber)
            },
            onVerifyCodeClick = { otp ->
                linkWithOtpClick(otp)
            }
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data: Intent? = result.data
            val response = data?.getStringExtra("response") ?: "nothing"
            handleUPIResponse(
                context,
                response,
                userData,
                merchantId,
                totalMrp,
                totalValue,
                selectedAmount
            ) { result ->
                transactionResult = result
                showDialog = true
            }
        } else {
            handleUPIResponse(
                context,
                "nothing",
                userData,
                merchantId,
                totalMrp,
                totalValue,
                selectedAmount
            ) { result ->
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
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Payment Information",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 18.dp, top = 8.dp)
            )

            Card(
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
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
                    PaymentInfoRow(
                        label = "Your total bill is",
                        value = String.format("%.2f", totalValue)
                    )
                    PaymentInfoRow(
                        label = "You have to pay",
                        value = String.format("%.2f", selectedAmount)
                    )
                    PaymentInfoRow(
                        label = "Remaining Amount",
                        value = String.format("%.2f", totalValue - selectedAmount)
                    )
                    Slider(
                        value = selectedPercentage,
                        onValueChange = { selectedPercentage = it },
                        valueRange = 10f..100f,
                        steps = 9,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = "Selected: ${selectedPercentage.toInt()}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = specialCode,
                    onValueChange = { specialCode = it },
                    label = { Text(text = "Have Special Code") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text
                    )
                )
                TextButton(
                    onClick = {
                        if (specialCode.replace(" ", "").lowercase().trim() == "parawalespecial") {
                            selectedPercentage = 0f
                        } else {
                            selectedPercentage = 10f
                        }
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(text = "Apply")
                }
            }

            Text(
                text = if (selectedPercentage == 0f) "You applied Cash on Delivery" else "Pay ₹${String.format("%.2f", selectedAmount)} using these UPI apps:",
                color = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                    .background(color = MaterialTheme.colors.background)
            ) {
                    UPIIconButton(
                        specialCode = specialCode,
                        context = context,
                        userData = userData,
                        cartItems = cartItems,
                        totalMrp = totalMrp,
                        totalValue = totalValue,
                        merchantId = merchantId,
                        merchantCode = merchantCode,
                        iconRes = R.drawable.bhimupi,
                        onClick = { vpa, name, note, merchantTransactionId, transactionUrl ->
                            SignInViewModel().startLoading()
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
                        },
                        onPhoneLinkRequired = { showPhoneLinkDialog = true },
                        location = location
                    )


                if (selectedPercentage == 0f) {
                    IconButton(onClick = {
                        SignInViewModel().startLoading()
                        sendOrders(
                            context = context,
                            userData = userData,
                            cartItems = cartItems,
                            totalMrp = totalMrp,
                            totalValue = totalValue,
                            transactionId = System.currentTimeMillis().toString(),
                            merchantId = merchantId,
                            merchantCode = merchantCode,
                            amountReceived = "0",
                            amountRemaining = totalValue.toString(),
                            onPhoneLinkRequired = { showPhoneLinkDialog = true },
                            onSuccessSendNotification = {
                                lifecycleOwner.lifecycleScope.launch {
                                    val contactInfo = if (userData?.userEmail.isNullOrEmpty()) {
                                        userData?.userPhoneNumber.toString()
                                    } else {
                                        userData?.userEmail
                                    }
                                    lifecycleOwner.lifecycleScope.launch {
                                        sendNotificationTOMerchant(
                                            "pratyansh35@gmail.com",contactInfo.toString())
                                    }
                                }
                                cartItems.clear()
                            },
                            onAddressRequired = {

                            },
                            location = location
                        )
                    }) {
                        Card(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = if (isDarkTheme) { R.drawable.codlight } else { R.drawable.coddark }),
                                contentDescription = "Cash on Delivery",
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        TransactionResultDialog(result = transactionResult, onDismiss = { showDialog = false })
    }
}

suspend fun sendNotificationTOMerchant(merchantEmail: String, client:String ){
    Log.d("Notifications", "sendNotificationTOMerchant: $merchantEmail")
        sendNotificationToUser(merchantEmail, "New Order from $client", "You have a new order from $client. Please check Customers Orders for more details.")
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
    merchantCode: String,
    iconRes: Int,
    onClick: (vpa: String, name: String, note: String, transactionId: String, transactionUrl: String) -> Unit,
    onPhoneLinkRequired: () -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    location: UserAddressDetails?
) {

    IconButton(onClick = {
        if (specialCode.replace(" ", "").lowercase().trim() == "parawalespecial") {
            SignInViewModel().startLoading()
            sendOrders(
                context,
                userData,
                cartItems,
                totalMrp,
                totalValue,
                System.currentTimeMillis().toString(),
                merchantId = merchantId,
                merchantCode = merchantCode,
                amountReceived = "0",
                amountRemaining = totalValue.toString(),
                onPhoneLinkRequired = onPhoneLinkRequired,
                onSuccessSendNotification = {
                    val contactInfo = if (userData?.userEmail.isNullOrEmpty()) {
                        userData?.userPhoneNumber.toString()
                    } else {
                        userData?.userEmail
                    }
                    lifecycleOwner.lifecycleScope.launch {
                        sendNotificationTOMerchant(
                            "pratyansh35@gmail.com",contactInfo.toString())
                    }
                },
                onAddressRequired = {

                },
                location = location
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
    AlertDialog(onDismissRequest = onDismiss,
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
        })
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
            value = value, onValueChange = {}, readOnly = true, modifier = Modifier.width(150.dp)
        )
    }
}


