package com.darker.appsettings.app.ui.dialogs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darker.appsettings.ui.theme.AppSettings2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/*
You can use the following code for commercial purposes with some restrictions.
Read the full license here: https://semicolonspace.com/semicolonspace-license/
For more designs with source code,
visit: https://semicolonspace.com/jetpack-compose-samples/
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppSettings2Theme(darkTheme = false) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.White),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    val viewModel: MyViewModel = viewModel()

                    val openDialog by viewModel.open.observeAsState(false)

                    ButtonClick(buttonText = "Open Dialog") {
                        viewModel.open.value = true
                    }

                    if (openDialog) {
                        viewModel.startThread()
                        DialogBoxLoading()
                    }

                }
            }
        }
    }
}

@Composable
fun DialogBoxLoading(
    cornerRadius: Dp = 16.dp,
    paddingStart: Dp = 56.dp,
    paddingEnd: Dp = 56.dp,
    paddingTop: Dp = 32.dp,
    paddingBottom: Dp = 32.dp,
    progressIndicatorColor: Color = Color(0xFF35898f),
    progressIndicatorSize: Dp = 80.dp
) {

    Dialog(
        onDismissRequest = {
        }
    ) {
        Surface(
            shape = RoundedCornerShape(cornerRadius)
        ) {
            Column(
                modifier = Modifier
                    .padding(start = paddingStart, end = paddingEnd, top = paddingTop),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ProgressIndicatorLoading(
                    progressIndicatorSize = progressIndicatorSize,
                    progressIndicatorColor = progressIndicatorColor
                )

                // Gap between progress indicator and text
                Spacer(modifier = Modifier.height(32.dp))

                // Please wait text
                Text(
                    modifier = Modifier
                        .padding(bottom = paddingBottom),
                    text = "Please wait...",
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,

                    )
                )
            }
        }
    }
}

@Composable
fun ProgressIndicatorLoading(progressIndicatorSize: Dp, progressIndicatorColor: Color) {

    val infiniteTransition = rememberInfiniteTransition()
    CircularProgressIndicator(
        progress = 1f,
        modifier = Modifier
            .size(progressIndicatorSize)
            .border(
                12.dp,
                brush = Brush.sweepGradient(
                    listOf(
                        Color.White, // add background color first
                        progressIndicatorColor.copy(alpha = 0.1f),
                        progressIndicatorColor
                    )
                ),
                shape = CircleShape
            ),
        strokeWidth = 1.dp,
        color = Color.White // Set background color
    )
}

@Composable
fun ButtonClick(
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Button(
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(),
        onClick = {
            onButtonClick()
        }) {
        Text(
            text = buttonText,
            fontSize = 16.sp,
            color = Color.White
        )
    }
}

class MyViewModel : ViewModel() {
    // Dialog box
    var open = MutableLiveData<Boolean>()

    fun startThread() {
        viewModelScope.launch {

            withContext(Dispatchers.Default) {
                // Do the background work here
                // I'm adding delay
                delay(3000)
            }

            closeDialog()
        }
    }

    private fun closeDialog() {
        open.value = false
    }
}