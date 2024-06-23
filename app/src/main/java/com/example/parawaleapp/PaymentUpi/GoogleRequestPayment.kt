import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.parawaleapp.SendViewOrders.sendOrders
import com.example.parawaleapp.database.cartItems
import com.example.parawaleapp.database.total
import com.example.parawaleapp.database.totalmrp
import com.example.parawaleapp.sign_in.UserData

@Composable
fun PaymentScreenLayout(totalMrp: Double, totalValue: Double, userData: UserData?) {
    val context = LocalContext.current
    val googlePayPackageName = "com.google.android.apps.nbu.paisa.user"
    val merchantId = "BCR2DN4TWX57P5DJ"

    var selectedPercentage by remember { mutableFloatStateOf(10f) }
    val selectedAmount = totalValue * ( selectedPercentage / 100)

    var showDialog by remember { mutableStateOf(false) }
    var transactionResult by remember { mutableStateOf(TransactionResult("", "", "")) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                val response = data.getStringExtra("response")
                handleGooglePayResponse(context, response, userData, merchantId, totalMrp, totalValue, selectedAmount) { result ->
                    transactionResult = result
                    showDialog = true
                }
            } else {
                handleGooglePayResponse(context, "nothing", userData, merchantId, totalMrp, totalValue, selectedAmount) { result ->
                    transactionResult = result
                    showDialog = true
                }
            }
        } else {
            handleGooglePayResponse(context, "nothing", userData, merchantId, totalMrp, totalValue, selectedAmount) { result ->
                transactionResult = result
                showDialog = true
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        color = MaterialTheme.colors.background
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
                        steps = 2,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    Text(
                        text = "Selected: ${selectedPercentage.toInt()}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Button(
                onClick = {
                    payUsingGooglePay(
                        context,
                        launcher,
                        String.format("%.2f", selectedAmount),
                        "pratyansh35@okhdfcbank",
                        "Parawale Kirana Store",
                        "Booking order",
                        "transaction-id",
                        merchantId,
                        "https://transaction.url",
                        googlePayPackageName
                    )
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                shape = RoundedCornerShape(50),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Pay ₹${String.format("%.2f", selectedAmount)} with Google Pay", color = Color.White)
            }
        }
    }

    if (showDialog) {
        TransactionResultDialog(
            result = transactionResult,
            onDismiss = { showDialog = false }
        )
    }
}

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

private fun payUsingGooglePay(
    context: Context,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    amount: String,
    vpa: String,
    name: String,
    note: String,
    transactionId: String,
    merchantCode: String,
    transactionUrl: String,
    packageName: String
) {
    val uri = Uri.Builder()
        .scheme("upi")
        .authority("pay")
        .appendQueryParameter("pa", vpa)
        .appendQueryParameter("pn", name)
        .appendQueryParameter("mc", merchantCode)
        .appendQueryParameter("tr", transactionId)
        .appendQueryParameter("tn", note)
        .appendQueryParameter("am", amount)
        .appendQueryParameter("cu", "INR")
        .appendQueryParameter("url", transactionUrl)
        .build()

    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = uri
        setPackage(packageName)
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        launcher.launch(intent)
    } else {
        Toast.makeText(context, "Google Pay is not installed. Please install and try again.", Toast.LENGTH_SHORT).show()
    }
}

private fun handleGooglePayResponse(
    context: Context,
    response: String?,
    userData: UserData?,
    merchantId: String,
    totalMrp: Double,
    totalValue: Double,
    selectedAmount: Double,
    onResult: (TransactionResult) -> Unit
) {
    var status = ""
    var approvalRefNo = ""
    var paymentCancel = ""
    if (response != null && response != "discard") {
        val responseArray = response.split("&").toTypedArray()
        for (res in responseArray) {
            val keyValue = res.split("=").toTypedArray()
            if (keyValue.size >= 2) {
                when (keyValue[0].lowercase()) {
                    "status" -> status = keyValue[1].lowercase()
                    "approvalrefno", "txnref" -> approvalRefNo = keyValue[1]
                }
            } else {
                paymentCancel = "Payment cancelled by user."
            }
        }

        val result = when {
            status == "success" -> {
                sendOrders(
                    context,
                    userData = userData,
                    cartItems = cartItems,
                    totalMrp = totalmrp,
                    total = total,
                    transactionId = approvalRefNo,
                    merchantCode = merchantId,
                    amountReceived = String.format("%.2f", selectedAmount),
                    amountRemaining = String.format("%.2f", totalValue - selectedAmount)
                )
                TransactionResult("Success", approvalRefNo, "Transaction successful. Ref: $approvalRefNo")
            }
            paymentCancel == "Payment cancelled by user." -> {
                TransactionResult("Cancelled", "", paymentCancel)
            }
            else -> {
                TransactionResult("Failed", "", "Transaction failed. Please try again.")
            }
        }
        onResult(result)
    } else {
        onResult(TransactionResult("Failed", "", "Internet connection is not available. Please check and try again."))
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentScreenLayoutPreview() {
    PaymentScreenLayout(totalMrp = 1000.0, totalValue = 900.0, userData = null)
}
