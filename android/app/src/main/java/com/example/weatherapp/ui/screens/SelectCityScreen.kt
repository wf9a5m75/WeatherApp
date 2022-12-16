package com.example.weatherapp.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.AppViewModel

@Preview(showBackground = true)
@Composable
fun SelectCityScreen(
    viewModel: AppViewModel = viewModel(),
    onClose: () -> Unit = {}
) {
    val rememberScrollState = rememberScrollState()

    // If no preference, move to the selectCity screen
    viewModel.syncLocations {
        if (viewModel.locations.size == 0) {
            // TODO:
            Log.e("WeatherApp", "サーバーからデータの取得に失敗しました")
            return@syncLocations
        }
    }

    BackHandler(true) {
        onClose()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {

        TopAppBar(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        onClose()
                    }
            )
            Text(
                text = "場所を選択",
                modifier = Modifier.fillMaxWidth()
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .scrollable(
                    state = rememberScrollState,
                    orientation = Orientation.Vertical
                )
        ) {
            items(viewModel.locations, itemContent = {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Text(
                        text = it.name,
                        color = MaterialTheme.colors.primary,
                        fontSize = 18.sp
                    )

                    it.cities.forEach { city ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,

                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (city == viewModel.city.value),
                                    onClick = { viewModel.city.value = city }
                                )
                                .padding(horizontal = 16.dp)
                        ) {
                            RadioButton(
                                selected = (city == viewModel.city.value),
                                onClick = { viewModel.city.value = city }
                            )
                            Text(
                                text = city.name,
                                style = MaterialTheme.typography.body1.merge(),
                                modifier = Modifier.padding(start = 16.dp),
                                color = MaterialTheme.colors.primary
                            )
                        }
                    }
                }
            })
        }

//        viewModel.locations.forEach { prefecture ->
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(20.dp)
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .wrapContentHeight()
//                ) {
//                    Text(
//                        text = prefecture.name,
//                        color = MaterialTheme.colors.primary,
//                        fontSize = 18.sp
//                    )
//
//                    prefecture.cities.forEach { city ->
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .selectable(
//                                    selected = (city == viewModel.city.value),
//                                    onClick = { viewModel.city.value = city }
//                                )
//                                .padding(horizontal = 16.dp)
//                        ) {
//                            RadioButton(
//                                selected = (city == viewModel.city.value),
//                                onClick = { viewModel.city.value = city }
//                            )
//                            Text(
//                                text = city.name,
//                                style = MaterialTheme.typography.body1.merge(),
//                                modifier = Modifier.padding(start = 16.dp),
//                                color = MaterialTheme.colors.primary
//                            )
//                        }
//                    }
//                }
//            }
//        }
    }
}
