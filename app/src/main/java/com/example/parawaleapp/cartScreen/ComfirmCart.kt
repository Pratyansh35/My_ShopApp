package com.example.parawaleapp.cartScreen

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.parawaleapp.database.Dishfordb
import com.example.parawaleapp.database.cartItems
import com.example.parawaleapp.database.total
import com.example.parawaleapp.database.totalmrp
import java.io.IOException
import java.io.OutputStream
import java.util.IllegalFormatConversionException
import java.util.Locale
import java.util.UUID

@Composable
fun CartLayout() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Text(
            text = "Item Name",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Quantity",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "MRP",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Total",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun ConfirmItems(dish: Dishfordb) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Text(
            text = dish.name.take(21),
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            textAlign = TextAlign.Center
        )
        Text(
            text = dish.count.toString(),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(text = dish.price, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(
            text = (dish.count * dish.price.removePrefix("₹").toDouble()).toString(),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}


var selectedPrinter by mutableStateOf<String>("")
@Composable
fun ConfirmCart(navController: NavController? = null) {
    val context =  LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(modifier = Modifier.weight(0.8f)) {
            item {
                Text(
                    text = "Parawale",
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 45.sp,
                    color = Color.Red,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive
                )
                Text(
                    text = "Cart Summary",
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                CartLayout()
            }
            items(cartItems) { dish ->
                ConfirmItems(dish)
            }
        }
        Row(modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Total MRP: ₹${totalmrp}", modifier = Modifier.padding(10.dp))
            Row(modifier = Modifier.padding(10.dp)) {
                Text(text = "Discount on MRP: ")
                Text(
                    text = "-₹${totalmrp - total}", fontWeight = FontWeight.Bold, color = Color(0xFF449C44)
                )
            }
        }
        Text(
            text = "Total Amount: ₹$total",
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Button(
            onClick = {
                if(
                    selectedPrinter.isEmpty()
                ) {
                    Toast.makeText(context, "Please select a printer", Toast.LENGTH_SHORT).show()
                    navController?.navigate("BluetoothScreenRoute")
                    return@Button
                }

                val printData = formatForPrinting(cartItems, totalmrp.toDouble(), total.toDouble())
                printData( context ,selectedPrinter, printData)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF4CE14)),
            shape = RoundedCornerShape(40),
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.End)
        ) {
            Text(text = "Print Bill", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}






fun formatForPrinting(
    cartItems: List<Dishfordb>,
    totalMrp: Double,
    total: Double
): String {
    val ESC = "\u001B"
    val GS = "\u001D"
    val InitializePrinter = ESC + "@"
    val AlignCenter = ESC + "a" + "\u0001"
    val AlignLeft = ESC + "a" + "\u0000"
    val AlignRight = ESC + "a" + "\u0002"
    val BoldOn = ESC + "E" + "\u0001"
    val BoldOff = ESC + "E" + "\u0000"
    val DoubleOn = GS + "!" + "\u0011"  // 2x sized text (double-high + double-wide)
    val DoubleOff = GS + "!" + "\u0000"

    val sb = StringBuilder()

    sb.append(InitializePrinter)
    sb.append(AlignCenter)
    sb.append(DoubleOn + "Parawale\n\n" + DoubleOff)  // Set "Parawale" to bold and larger font
    sb.append("Cart Summary\n")

    sb.append("------------------------------\n")
    sb.append("Item Name    Qty   MRP    Total\n")
    sb.append("------------------------------\n")

    try {
        cartItems.forEach { dish ->
            val itemNameChunks = dish.name.chunked(10)
            val price = dish.price.removePrefix("₹").toDoubleOrNull() ?: 0.0
            val totalDishPrice = dish.count * price
            val formattedPrice = String.format(Locale.US, "%.2f", price) // Format price to 2 decimal places
            val formattedTotalDishPrice = String.format(Locale.US, "%.2f", totalDishPrice) // Format total dish price to 2 decimal places

            itemNameChunks.forEachIndexed { index, chunk ->
                if (index == 0) {
                    // First line includes the quantity, price, and total
                    sb.append(
                        String.format(
                            Locale.US,
                            "%-9s %3d  %5s  %7s\n",
                            chunk,
                            dish.count,
                            formattedPrice,
                            formattedTotalDishPrice
                        )
                    )
                } else {
                    // Subsequent lines only include the item name
                    sb.append(AlignLeft)
                    sb.append(
                        String.format(
                            Locale.US,
                            " %-9s\n",
                            chunk
                        )
                    )
                    sb.append(AlignCenter)
                }

            }
            sb.append("\n")
        }

        sb.append("------------------------------\n")
        sb.append(AlignRight)  // Center align for the totals

        sb.append(String.format(Locale.US, "Total MRP: %.2f\n", totalMrp))
        sb.append(String.format(Locale.US, "Discount: -%.2f\n\n", totalMrp - total))
        sb.append(AlignCenter)
        sb.append(DoubleOn + "--Total Amount--\n" + DoubleOff)
        sb.append(DoubleOn)
        sb.append(String.format(Locale.US, "%.2f", total))
        sb.append(DoubleOff)
        sb.append("\n")

        sb.append("------------------------------\n\n\n\n")

        Log.e("PrintingError", "Cart Items: $cartItems")
        Log.e("PrintingError", "Total MRP: $totalMrp, Total: $total")
    } catch (e: IllegalFormatConversionException) {
        Log.e("PrintingError", "IllegalFormatConversionException: ${e.message}")
        // Log the data causing the issue
        Log.e("PrintingError", "Cart Items: $cartItems")
        Log.e("PrintingError", "--Total MRP--\n$totalMrp, Total: $total")
    }

    return sb.toString()
}







fun printData(context: Context, printerAddress: String, data: String) {
    val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    if (!bluetoothAdapter.isEnabled) {
        Toast.makeText(context, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show()
        return
    }

    val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(printerAddress)
    val uuid: UUID = device.uuids[0].uuid // Standard SerialPortService ID

    var socket: BluetoothSocket? = null
    var outputStream: OutputStream? = null

    try {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "Bluetooth permission not granted", Toast.LENGTH_SHORT).show()
            return
        }
        socket = device.createRfcommSocketToServiceRecord(uuid)
        socket.connect()
        outputStream = socket.outputStream

        // Send the data
        outputStream.write(data.toByteArray())

        // Add ESC/POS command to cut the paper
        outputStream.write(byteArrayOf(0x1D, 0x56, 0x41, 0x10))

        outputStream.flush()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            outputStream?.close()
            socket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}