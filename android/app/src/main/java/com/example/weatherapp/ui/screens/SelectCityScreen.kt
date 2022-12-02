package com.example.weatherapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.model.City
import com.example.weatherapp.model.Prefecture

@Preview(showBackground = true)
@Composable
fun SelectCityScreen(
    locations: List<Prefecture> = arrayListOf(),
    currentCity: City = City("", ""),
    onClose: (city: City) -> Unit = {}
) {
    var selectedCity by remember { mutableStateOf(currentCity) }
    BackHandler(true) {
        onClose(selectedCity)
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
                    .clickable { onClose(selectedCity) }
            )
            Text(
                text = "場所を選択",
                modifier = Modifier.fillMaxWidth()
            )
        }

        locations.forEach { prefecture ->
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
                                    selected = (city.id == selectedCity.id),
                                    onClick = { selectedCity = city }
                                )
                                .padding(horizontal = 16.dp)
                        ) {
                            RadioButton(
                                selected = (city.id == selectedCity.id),
                                onClick = { selectedCity = city }
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
            }
        }
    }
}
