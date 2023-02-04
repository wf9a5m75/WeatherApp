package com.example.weatherapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.AppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun SelectCityScreen(
    viewModel: AppViewModel = viewModel(),
    onClose: () -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    BackHandler(true) {
        onClose()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
    ) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        onClose()
                    },
            )
            Text(
                text = "場所を選択",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize(),
        ) {
            items(
                viewModel.locations,
                itemContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                    ) {
                        Text(
                            text = it.name,
                            color = MaterialTheme.colors.primary,
                            fontSize = 18.sp,
                        )

                        it.cities.forEach { city ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = (city == viewModel.city.value),
                                        onClick = { viewModel.city.value = city },
                                    )
                                    .padding(horizontal = 16.dp),
                            ) {
                                RadioButton(
                                    selected = (city == viewModel.city.value),
                                    onClick = { viewModel.city.value = city },
                                )
                                Text(
                                    text = city.name,
                                    style = MaterialTheme.typography.body1.merge(),
                                    modifier = Modifier.padding(start = 16.dp),
                                    color = MaterialTheme.colors.primary,
                                )
                            }
                        }
                    }
                },
            )
        }
    }
    LaunchedEffect(true) {
        // Synchronize the location list data when this component is displayed first time.
        viewModel.syncLocations {

            // If user selected a city, scroll to the prefecture where includes the city.
            var idx: Int = -1
            viewModel.locations.forEachIndexed { index, prefecture ->
                if (idx > -1) {
                    return@forEachIndexed
                }
                for (city in prefecture.cities) {
                    if (city.id == viewModel.city.value.id) {
                        idx = index
                        break
                    }
                }
            }
            if (idx == -1) {
                return@syncLocations
            }

            coroutineScope.launch {
                // Insert 0.5 sec delay to wait the lazyColumn drawing is completed.
                delay(500)
                lazyListState.animateScrollToItem(index = idx)
            }
        }
    }
}
