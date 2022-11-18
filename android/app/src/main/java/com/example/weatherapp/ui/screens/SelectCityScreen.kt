package com.example.weatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.model.*

@Preview(showBackground = true)
@Composable
fun SelectCityScreen(
    viewModel: AppViewModel? = null,
    onClose: () -> Unit = {}
) {

    val selected = remember { mutableStateOf(viewModel?.city) }


    val onClick: (city: City) -> Unit = { city ->
        selected.value = city

        // if not preview
        if (viewModel != null) {
            viewModel.city = city
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {

        TopAppBar(modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { onClose() }
            )
            Text(
                text = "場所を選択",
                modifier = Modifier.fillMaxWidth()
            )
        }

        viewModel?.cities?.forEach { prefecture ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Text(
                        text = prefecture.name,
                        color = MaterialTheme.colors.primary,
                        fontSize = 18.sp
                    )

                    prefecture.cities.forEach { city ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,

                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (city == selected.value),
                                    onClick = { onClick(city) }
                                )
                                .padding(horizontal = 16.dp)
                        ) {
                            RadioButton(
                                selected = (city == selected.value),
                                onClick = { onClick(city) })
                            Text(
                                text = city.name,
                                style = MaterialTheme.typography.body1.merge(),
                                modifier = Modifier.padding(start = 16.dp),
                                color = MaterialTheme.colors.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

