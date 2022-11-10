package com.example.weatherapp.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.Settings

@Composable
fun AppGlobalNav(
    context: Context,
    settings: Settings
) {

    var mDisplayMenu by remember { mutableStateOf(false) }

    // Menu bar
    TopAppBar(
        elevation = 4.dp,
        title = {
            Text(
                text = settings.city_id.value
            )
        },

        actions = {

//                  IconButton(onClick = {
//                      Toast.makeText(mContext,
//                          "Hello", Toast.LENGTH_SHORT).show()
//                  }) {
//                      Icon(painter = painterResource(id = R.drawable.ic_baseline_home_24), null)
//                  }

            // Creates a "..." button for dropdown menu
            IconButton(
                onClick = {
                    mDisplayMenu = !mDisplayMenu
                }
            ) {
                Icon(Icons.Default.MoreVert, "")
            }

            DropdownMenu(expanded = mDisplayMenu, onDismissRequest = { mDisplayMenu = false }) {
                DropdownMenuItem(onClick = {
                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "場所の変更")
                }
            }
        }
    )
}