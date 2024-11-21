package com.parawale.GrocEase.PaymentUpi

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import com.parawale.GrocEase.DataClasses.UserData

fun payUsingUPI(
    context: Context,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    amount: String,
    vpa: String,
    name: String,
    note: String,
    transactionId: String,
    transactionUrl: String
) {
    val uri = Uri.Builder()
        .scheme("upi")
        .authority("pay")
        .appendQueryParameter("pa", vpa)
        .appendQueryParameter("pn", name)
        .appendQueryParameter("mc", "0000")
        .appendQueryParameter("tr", transactionId)
        .appendQueryParameter("tn", note)
        .appendQueryParameter("am", amount)
        .appendQueryParameter("cu", "INR")
        .appendQueryParameter("url", transactionUrl)
        .appendQueryParameter("mode", "02")
        .appendQueryParameter("purpose", "00")
        .appendQueryParameter("orgid", "180001")
        .appendQueryParameter("sign", "MEUCID5bRD/BADwOIhZq+nV65bJjTsDbVlzq5vuXmZaWcYqVAiEA+QjwgmXrknSDDnImrRNLIeCIXS6+9Ird16Rb1WEeVH0=")
        .build()

    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = uri
    }

    val packageManager = context.packageManager
    val activities = packageManager.queryIntentActivities(intent, 0)

    if (activities.isNotEmpty()) {
        launcher.launch(intent)
    } else {
        Toast.makeText(
            context,
            "No UPI app is installed. Please install and try again.",
            Toast.LENGTH_SHORT
        ).show()
    }
}

fun handleUPIResponse(
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

    response?.let {
        val responseArray = it.split("&").toTypedArray()
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
    } ?: run {
        onResult(TransactionResult("Failed", "", "Internet connection is not available. Please check and try again."))
    }
}
