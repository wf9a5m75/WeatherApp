package com.example.weatherapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R

@Preview(showBackground = true)
@Composable
fun OfflineScreen() {
    Column(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)) {

        Icon(
            tint =  MaterialTheme.colors.onBackground,
            painter = painterResource(id = R.drawable.ic_baseline_wifi_off_24),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
                .width(75.dp)
                .height(75.dp),
        )
        Text(
            text = "インターネットの接続を確認し、\n時間をあけて再度お試しください",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 17.sp
        )
    }
}