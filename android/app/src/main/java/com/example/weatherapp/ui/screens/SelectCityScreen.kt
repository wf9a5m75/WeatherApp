package com.example.weatherapp.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.model.AppViewModel
import com.example.weatherapp.model.City

@Preview(showBackground = true)
@Composable
fun SelectCityScreen(
    viewModel: AppViewModel? = null,
    onClose: () -> Unit = {}
) {

    val onClick: (city: City) -> Unit = { city ->
        viewModel?.city?.value = city
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

        viewModel?.locations?.forEach { prefecture ->
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
                                    selected = (city == viewModel.city.value),
                                    onClick = { onClick(city) }
                                )
                                .padding(horizontal = 16.dp)
                        ) {
                            RadioButton(
                                selected = (city == viewModel.city.value),
                                onClick = { onClick(city) }
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
