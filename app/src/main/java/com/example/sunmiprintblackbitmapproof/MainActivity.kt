package com.example.sunmiprintblackbitmapproof

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sunmiprintblackbitmapproof.ui.theme.SunmiPrintBlackBitmapProofTheme
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.InnerPrinterException
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.SunmiPrinterService

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SunmiPrintBlackBitmapProofTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ElevatedButtonExample("Rectangle Test.") {
                            rectangleBlackBitmapPrint()
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        ElevatedButtonExample("Square Test.") {
                            squareBlackBitmapPrintTest()
                        }
                    }
                }
            }
        }

        connectPrinter()
    }


    private var sunmiPrinterService: SunmiPrinterService? = null
    private fun connectPrinter() {
        try {
            InnerPrinterManager.getInstance()
                .bindService(this, object : InnerPrinterCallback() {
                    override fun onConnected(service: SunmiPrinterService?) {
                        if (service != null && checkSunmiPrinterService(service)) {
                            sunmiPrinterService = service
                        }
                    }

                    override fun onDisconnected() {

                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkSunmiPrinterService(service: SunmiPrinterService): Boolean {
        return try {
            InnerPrinterManager.getInstance().hasPrinter(service)
        } catch (e: InnerPrinterException) {
            e.printStackTrace()
            false
        }
    }

    private val rectangleWidthPx = 576

    // adjust your size of bitmap you want here
    private val rectangleWidthHeightTestList = listOf(
        Pair(rectangleWidthPx, 20),
        Pair(rectangleWidthPx, 30),
        Pair(rectangleWidthPx, 36),

        // we found problem while printing this size
        Pair(rectangleWidthPx, 37)
    )

    private fun rectangleBlackBitmapPrint() {
        if (sunmiPrinterService != null) {

            rectangleWidthHeightTestList.forEachIndexed { index, pair ->

                sunmiPrinterService?.printText(generateTextSize(pair.first, pair.second), null)
                val bitmap = generateBlackBitMap(pair.first, pair.second)
                sunmiPrinterService?.printBitmap(bitmap, null)
                sunmiPrinterService?.lineWrap(1, null)
                bitmap.recycle()
                if (index == rectangleWidthHeightTestList.size - 1) {
                    sunmiPrinterService?.cutPaper(null)
                }

            }

        } else {
            Toast.makeText(this, "Printer not found", Toast.LENGTH_SHORT).show()
        }
    }

    // adjust your size of bitmap you want here
    private val squareTestList = listOf(
        100, 200, 250, 285,

        // we found problem while printing this size
        300
    )

    private fun squareBlackBitmapPrintTest() {
        if (sunmiPrinterService != null) {

            squareTestList.forEachIndexed { index, size ->

                sunmiPrinterService?.printText(generateTextSize(size, size), null)
                sunmiPrinterService?.lineWrap(1, null)
                val bitmap = generateBlackBitMap(size, size)
                sunmiPrinterService?.printBitmap(bitmap, null)
                sunmiPrinterService?.lineWrap(1, null)
                bitmap.recycle()
                if (index == squareTestList.size - 1) {
                    sunmiPrinterService?.cutPaper(null)
                }
            }

        } else {
            Toast.makeText(this, "Printer not found", Toast.LENGTH_SHORT).show()
        }
    }


    private fun generateTextSize(width: Int, height: Int): String {
        return "${width}px x ${height}px  (${width * height} square pixel)"
    }


    private fun generateBlackBitMap(width: Int, height: Int): Bitmap {
        return Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565).apply {
            eraseColor(Color.BLACK)
        }
    }
}

@Composable
fun ElevatedButtonExample(text: String, onClick: () -> Unit) {
    ElevatedButton(
        onClick = { onClick() },
        modifier = Modifier.wrapContentSize()
    ) {

        Text(text, modifier = Modifier.padding(16.dp))

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SunmiPrintBlackBitmapProofTheme {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedButtonExample("Rectangle Test.") {

            }
            Spacer(modifier = Modifier.height(16.dp))

            ElevatedButtonExample("Square Test.") {

            }
        }
    }
}