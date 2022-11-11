package com.example.weatherapp.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.model.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun SelectCityScreen(
    context: Context? = null,
    settings: Settings? = null,
    cities: MutableCollection<Prefecture> = mutableStateListOf<Prefecture>(),
    onClick: () -> Unit = {}
) {

    val initSelect = settings?.city?.value ?: City("", "")

    val selected = remember { mutableStateOf(initSelect) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.background)) {

        TopAppBar(modifier = Modifier.fillMaxWidth()) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { onClick() }
            )
            Text(
                text = "場所を選択",
                modifier = Modifier.fillMaxWidth()
            )
        }

        cities.forEach {
            prefecture ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                    ) {
                        Text(
                            text = prefecture.name,
                            color = MaterialTheme.colors.primary,
                            fontSize = 18.sp
                        )

                        prefecture.cities.forEach {
                            city ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,

                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = (city == selected.value),
                                            onClick = {
                                                selected.value = city

                                                // if not preview
                                                if (settings != null) {
                                                    settings.city.value = city
                                                }
                                            }
                                        )
                                        .padding(horizontal = 16.dp)
                                ) {
                                    RadioButton(
                                        selected = (city == selected.value),
                                        onClick = {
                                            selected.value = city

                                            // if not preview
                                            if (settings != null) {
                                                settings.city.value = city
                                            }
                                        })
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

