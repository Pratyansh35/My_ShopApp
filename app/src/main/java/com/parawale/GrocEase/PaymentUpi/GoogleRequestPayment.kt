//fun payUsingGooglePay(
//    context: Context,
//    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
//    amount: String,
//    vpa: String,
//    name: String,
//    note: String,
//    transactionId: String,
//    transactionUrl: String,
//    packageName: String
//) {
//    val uri = Uri.Builder().scheme("upi").authority("pay").appendQueryParameter("pa", vpa)
//        .appendQueryParameter("pn", name).appendQueryParameter("mc", 5411.toString())
//        .appendQueryParameter("tr", transactionId).appendQueryParameter("tn", note)
//        .appendQueryParameter("am", amount).appendQueryParameter("cu", "INR")
//        .appendQueryParameter("url", transactionUrl).build()
//
//    val intent = Intent(Intent.ACTION_VIEW).apply {
//        data = uri
//        setPackage(packageName)
//    }
//
//    if (intent.resolveActivity(context.packageManager) != null) {
//        launcher.launch(intent)
//    } else {
//        Toast.makeText(
//            context,
//            "Google Pay is not installed. Please install and try again.",
//            Toast.LENGTH_SHORT
//        ).show()
//    }
//}


//fun handleGooglePayResponse(
//    context: Context,
//    response: String?,
//    userData: UserData?,
//    merchantId: String,
//    totalMrp: Double,
//    totalValue: Double,
//    selectedAmount: Double,
//    onResult: (TransactionResult) -> Unit
//) {
//    var status = ""
//    var approvalRefNo = ""
//    var paymentCancel = ""
//    if (response != null && response != "discard") {
//        val responseArray = response.split("&").toTypedArray()
//        for (res in responseArray) {
//            val keyValue = res.split("=").toTypedArray()
//            if (keyValue.size >= 2) {
//                when (keyValue[0].lowercase()) {
//                    "status" -> status = keyValue[1].lowercase()
//                    "approvalrefno", "txnref" -> approvalRefNo = keyValue[1]
//                }
//            } else {
//                paymentCancel = "Payment cancelled by user."
//            }
//        }
//
//        val result = when {
//            status == "success" -> {
//                sendOrders(
//                    context,
//                    userData = userData,
//                    cartItems = cartItems,
//                    totalMrp = totalmrp,
//                    total = total,
//                    transactionId = approvalRefNo,
//                    merchantCode = merchantId,
//                    amountReceived = String.format("%.2f", selectedAmount),
//                    amountRemaining = String.format("%.2f", totalValue - selectedAmount)
//                )
//                TransactionResult("Success", approvalRefNo, "Transaction successful. Ref: $approvalRefNo")
//            }
//            paymentCancel == "Payment cancelled by user." -> {
//                TransactionResult("Cancelled", "", paymentCancel)
//            }
//            else -> {
//                TransactionResult("Failed", "", "Transaction failed. Please try again.")
//            }
//        }
//        onResult(result)
//    } else {
//        onResult(TransactionResult("Failed", "", "Internet connection is not available. Please check and try again."))
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun PaymentScreenLayoutPreview() {
//    PaymentScreenLayout(totalMrp = 1000.0, totalValue = 900.0, userData = null)
//}
