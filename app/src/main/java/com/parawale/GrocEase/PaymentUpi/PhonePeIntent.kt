import android.content.Context
import androidx.core.app.ActivityCompat.startActivityForResult
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe


fun Phonepe(
    context: Context,
    amount: Double,
    merchantId: String,
    merchantTransactionId: String,
    callbackUrl: String,
    mobileNumber: String
) {
//    val b2BPGRequest = B2BPGRequestBuilder()
//        .setData(base64Body)
//        .setChecksum(checksum)
//        .setUrl(apiEndPoint)
//        .build()
//
//    val B2B_PG_REQUEST_CODE = 777
//
//    //For SDK call below function
//    try {
//        startActivityForResult(
//            PhonePe.getImplicitIntent(
//            context, b2BPGRequest, ,B2B_PG_REQUEST_CODE);
//    } catch(PhonePeInitException e){
//    }
}
