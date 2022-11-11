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
    onClick: () -> Unit = {}
) {

    val cities = listOf(
        Prefecture(
            id = "osaka",
            name = "大阪府",
            cities = listOf(
                City("osaka_hirakata", "枚方市"),
                City("osaka_osaka", "大阪市"),
                City("osaka_sakai", "堺市")
            )
        ),

        Prefecture(
            id = "hyogo",
            name = "兵庫県",
            cities = listOf(
                City("hyogo_himeji", "姫路市"),
                City("hyogo_kobe", "神戸市"),
                City("hyogo_tamba", "丹波市")
            )
        )
    )
    val initSelect = settings?.city_id?.value ?: "hyogo_kobe"

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
                                            selected = (city.id == selected.value),
                                            onClick = {
                                                selected.value = city.id

                                                // if not preview
                                                if (settings != null) {
                                                    settings.city_id.value = city.id
                                                }
                                            }
                                        )
                                        .padding(horizontal = 16.dp)
                                ) {
                                    RadioButton(
                                        selected = (city.id == selected.value),
                                        onClick = {
                                            selected.value = city.id

                                            // if not preview
                                            if (settings != null) {
                                                settings.city_id.value = city.id
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


fun getCities(context: Context) {
    val weatherApi = RetrofitHelper.getInstance(context).create(WeatherApi::class.java)

    GlobalScope.launch {
        val result = weatherApi.getLocations()
        if (result.code() == 200) {
            val locationRes = result.body()
            Log.d("weather", "-----> lastUpdate = ${locationRes!!.last_update}")
        }
    }
}