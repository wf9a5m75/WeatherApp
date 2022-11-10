package com.example.weatherapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun AppTabs() {
    var tabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("One", "Two", "Three")
    Column {
        TabRow(selectedTabIndex = tabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = {
                        tabIndex = index
                    },
                    text = {
                        Text(text = title)
                    }
                )
            }
        }
        when (tabIndex) {
            0 -> Text("Hello")
            1 -> Text("World")
            else -> Text("Welcome!")
        }
    }
}