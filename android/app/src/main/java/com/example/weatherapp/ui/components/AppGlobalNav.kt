package com.example.weatherapp.ui.components

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.AppViewModel

data class OptionMenuItem(val menuId: String, val text: String)

@Composable
fun AppGlobalNav(
    viewModel: AppViewModel,
    menuItems: List<OptionMenuItem>,
    onMenuItemClicked: (menuId: String) -> Unit
) {

    var mDisplayMenu by remember { mutableStateOf(false) }

    // Menu bar
    TopAppBar(
        elevation = 4.dp,
        title = {
            Text(
                text = viewModel.city.name
            )
        },

        actions = {

            // Creates a "..." button for dropdown menu
            IconButton(
                onClick = {
                    mDisplayMenu = !mDisplayMenu
                }
            ) {
                Icon(Icons.Default.MoreVert, "")
            }

            DropdownMenu(expanded = mDisplayMenu, onDismissRequest = { mDisplayMenu = false }) {

                menuItems.forEach { item ->
                    DropdownMenuItem(onClick = {
                        // close the dropdown menu
                        mDisplayMenu = false

                        // Execute the callback
                        onMenuItemClicked(item.menuId)
                    }) {
                        Text(text = item.text)
                    }
                }
            }
        }
    )
}
