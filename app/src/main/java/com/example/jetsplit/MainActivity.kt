package com.example.jetsplit

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MovableContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.jetsplit.componets.InputField
import com.example.jetsplit.ui.theme.JetSplitTheme
import com.example.jetsplit.util.calculateTotalPerPerson
import com.example.jetsplit.util.calculateTotalTip
import com.example.jetsplit.widgets.RoundIconButton


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()

        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetSplitTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize() // Make sure it occupies the full screen
                .background(MaterialTheme.colorScheme.background), // Ensure full background color
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}

@Composable
fun MainContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Ensure background color fills the screen
            .systemBarsPadding(), // Adjust for status and navigation bars
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BillForm()
    }
}

@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(150.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7) // Light purple background
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black // Explicit black color
            )
            Text(
                text = "₹$total",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black // Explicit black color
            )
        }
    }
}

@Preview
@Composable
fun BillForm() {
    val totalBillState = remember { mutableStateOf("") }
    val splitByState = remember { mutableStateOf(1) }
    val sliderPositionState = remember { mutableStateOf(0f) }
    val tipAmountState = remember { mutableStateOf(0.0) }
    val totalPerPersonState = remember { mutableStateOf(0.0) }
    val keyboardController = LocalSoftwareKeyboardController.current

    fun recalculateValues() {
        val totalBill = totalBillState.value.toDoubleOrNull() ?: 0.0
        val tipPercentage = (sliderPositionState.value * 100).toInt()
        tipAmountState.value = totalBill * tipPercentage / 100
        totalPerPersonState.value = (totalBill + tipAmountState.value) / splitByState.value
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TopHeader Composable at the Top
        TopHeader(totalPerPerson = totalPerPersonState.value)

        // Input Field for Total Bill
//        OutlinedTextField(
//            value = totalBillState.value,
//            onValueChange = { newValue ->
//                totalBillState.value = newValue
//                recalculateValues()
//            },
//            label = { Text("Enter Bill") },
//            leadingIcon = { Text("₹") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )

        OutlinedTextField(
            value = totalBillState.value,
            onValueChange = { newValue ->
                totalBillState.value = newValue
                recalculateValues()
            },
            label = { Text("Enter Bill") },
            leadingIcon = { Text("₹") }, // Indian currency symbol
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide() // Hide keyboard when Done is pressed
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )


        // Split Section
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Split", style = MaterialTheme.typography.bodyLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (splitByState.value > 1) {
                        splitByState.value -= 1
                        recalculateValues()
                    }
                }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease Split")
                }
                Text(
                    "${splitByState.value}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(onClick = {
                    splitByState.value += 1
                    recalculateValues()
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase Split")
                }
            }
        }

//
        // Tip Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start, // Align the content to the start
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display Tip Amount in front of "Tip"

            Text(
                text = "Tip",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(250.dp))
            Text(
                text = "₹${"%.2f".format(tipAmountState.value)}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(end = 8.dp) // Add space between the amount and the label
            )
        }
                // Slider and Tip Percentage
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Slider for Tip Percentage
                    Text(
                        text = "${(sliderPositionState.value * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Slider(
                        value = sliderPositionState.value,
                        onValueChange = { newValue ->
                            sliderPositionState.value = newValue
                            recalculateValues()
                        },
                        valueRange = 0f..1f,
                        steps = 5,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Tip Percentage Text

                }
            }
        }


//@Preview
//@Composable
//fun BillForm(modifier: Modifier = Modifier,
//             onValChange: (String) -> Unit = {
//
//             }){
//    val totalBillState = remember {
//        mutableStateOf("")
//    }
//    val validState  = remember (totalBillState.value){
//        totalBillState.value.trim().isNotEmpty()
//    }
//
//    val keyboardController = LocalSoftwareKeyboardController.current
//
//
//    val sliderPositionState = remember {
//        mutableStateOf(0f)
//    }
//    val tipPercentage = (sliderPositionState.value * 100).toInt()
//
//    val splitByState = remember {
//        mutableStateOf(1)
//    }
//
//    val range = IntRange(start = 1, endInclusive = 100)
//
//    val tipAmountState = remember {
//        mutableStateOf(0.0)
//    }
//
//    val totalperPersonState = remember {
//        mutableStateOf(0.0)
//    }
//
//    TopHeader(totalPerPerson = totalperPersonState.value)
//
//    Surface(
//       modifier = Modifier
//           .padding(2.dp)
//           .fillMaxWidth(),
//       shape = RoundedCornerShape(corner = CornerSize(8.dp)),
//       border = BorderStroke(width = 1.dp, color = Color.LightGray)
//   ) {
//     Column(modifier = Modifier.padding(6.dp),
//         verticalArrangement = Arrangement.Top,
//         horizontalAlignment = Alignment.Start) {
//       InputField(valueState = totalBillState ,
//           labelId = "Enter Bill",
//           enabled = true,
//           isSingleLine = true,
//           onAction = KeyboardActions
//           {
//               if(!validState) return@KeyboardActions
//               onValChange(totalBillState.value.trim())
//
//               keyboardController?.hide()
//           }       )
////         if(validState){
//            Row (modifier = Modifier.padding(3.dp),
//                horizontalArrangement = Arrangement.Start){
//                  Text(text = "Split",
//                      modifier = Modifier.align(
//                          alignment = Alignment.CenterVertically
//                      ))
//                Spacer(modifier = Modifier.width(120.dp))
//                Row(modifier = Modifier.padding(horizontal = 3.dp),
//                    horizontalArrangement = Arrangement.End) {
//                        RoundIconButton(imageVector = Icons.Default.Remove,
//                            onClick = {
//                              splitByState.value =
//                                  if (splitByState.value >1) splitByState.value -1
//                                else 1
//                                totalperPersonState.value = calculateTotalPerPerson(
//                                    totalBill = totalBillState.value.toDouble(),
//                                    splitBy = splitByState.value, // Pass splitBy as Int
//                                    tipPercentage = tipPercentage
//                                )
//
//                            })
//                         Text(text = "${splitByState.value}", modifier = Modifier
//                             .align(Alignment.CenterVertically)
//                             .padding(start = 9.dp, end = 9.dp))
//                        RoundIconButton(imageVector = Icons.Default.Add ,
//                            onClick = {
//                                if (splitByState.value < range.last){
//                                    splitByState.value = splitByState.value + 1
//                                }
//                            })
//                }
//            }
//
//             //tip row\
//         Row (modifier = Modifier.padding(horizontal = 3.dp,
//             vertical = 12.dp)){
//             Text(text ="Tip " ,
//                 modifier = Modifier.align(alignment = Alignment.CenterVertically))
//                 Spacer(modifier = Modifier.width(200.dp))
//
//             Text(text = "$ ${tipAmountState.value}",modifier = Modifier.align(alignment = Alignment.CenterVertically))
//             Spacer(modifier = Modifier.width(200.dp))
//         }
//         Column(verticalArrangement = Arrangement.Center,
//             horizontalAlignment = Alignment.CenterHorizontally){
//            Text(text = "$tipPercentage%")
//             Spacer(modifier = Modifier.height(14.dp))
//
//             //Slider
////             Slider(
////                 value = sliderPositionState.value,
////                 onValueChange = { newVal ->
////                     sliderPositionState.value = newVal
////                     val totalBill = totalBillState.value.toDoubleOrNull() ?: 0.0
////
////                     // Calculate tip amount
////                     tipAmountState.value = calculateTotalTip(
////                         totalBill = totalBill,
////                         tipPercentage = tipPercentage
////                     )
////
////                     // Calculate total per person
////                     totalperPersonState.value = calculateTotalPerPerson(
////                         totalBill = totalBillState.value.toDouble(),
////                         splitBy = splitByState.value, // Pass splitBy as Int
////                         tipPercentage = tipPercentage
////                     )
////                 },
////                 modifier = Modifier.padding(start = 16.dp, end = 16.dp),
////                 steps = 5,
////                 onValueChangeFinished = {}
////             )
//             Slider(
//                 value = sliderPositionState.value,
//                 onValueChange = { newVal ->
//                     sliderPositionState.value = newVal
//                     val totalBill = totalBillState.value.toDoubleOrNull() ?: 0.0
//
//                     // Calculate tip percentage dynamically
//                     val tipPercentage = (newVal * 100).toInt()
//
//                     // Update tipAmountState
//                     tipAmountState.value = calculateTotalTip(
//                         totalBill = totalBill,
//                         tipPercentage = tipPercentage
//                     )
//
//                     // Update totalperPersonState immediately
//                     totalperPersonState.value = calculateTotalPerPerson(
//                         totalBill = totalBill,
//                         splitBy = splitByState.value, // Pass splitBy as Int
//                         tipPercentage = tipPercentage
//                     )
//                 },
//                 modifier = Modifier.padding(start = 16.dp, end = 16.dp),
//                 steps = 5,
//                 onValueChangeFinished = {}
//             )
//
//         }
////         } else{
////             Box(){}
////         }
//     }
//   }
//}
//


