package com.mioshek.theclock.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mioshek.theclock.R
import com.mioshek.theclock.ui.theme.bodyFontFamily
import com.mioshek.theclock.ui.theme.displayFontFamily

enum class StopwatchState{
    RUNNING,
    NOTSTARTED,
    PAUSED,
}
@Composable
fun StopwatchView(modifier: Modifier = Modifier) {
    var buttonState by remember { mutableStateOf(StopwatchState.NOTSTARTED) }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {

        Box(
            modifier = modifier
                .weight(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ){
            Text(
                text = "00:00:00",
                fontSize = 50.sp,
                fontFamily = displayFontFamily,
            )
        }

        Row(
            modifier = modifier
                .weight(1f)
                .padding(25.dp),
            verticalAlignment = Alignment.Bottom
            ) {
            when(buttonState){
                StopwatchState.NOTSTARTED -> {
                    Button(
                        onClick = {
                            buttonState = StopwatchState.RUNNING
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        ) {
                        Icon(painter = painterResource(id = R.drawable.play), contentDescription = "Play")
                    }
                }

                StopwatchState.RUNNING -> {
                    Button(
                        onClick = { /*TODO START */ },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    ) {
                        Icon(painter = painterResource(id = R.drawable.flag), contentDescription = "NewLoop")
                    }

                    Button(
                        onClick = { buttonState = StopwatchState.PAUSED },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        ) {
                        Icon(painter = painterResource(id = R.drawable.pause), contentDescription = "Pause")
                    }
                }

                StopwatchState.PAUSED -> {
                    Button(
                        onClick = { buttonState = StopwatchState.NOTSTARTED },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        ) {
                        Icon(painter = painterResource(id = R.drawable.delete), contentDescription = "Clear")
                    }

                    Button(
                        onClick = { buttonState = StopwatchState.RUNNING },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        ) {
                        Icon(painter = painterResource(id = R.drawable.play), contentDescription = "Play")
                    }
                }
            }
        }
    }

}