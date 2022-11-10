package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.Settings
import com.example.weatherapp.ui.components.AppGlobalNav
import com.example.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    //modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    WeatherApp()
                }
            }
        }
    }

    /**
     * Action: The menu button has been tapped
     * @param menu {Menu?} The options menu which you place your items
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Toast.makeText(this, "「${item.title}」が押されました",
            Toast.LENGTH_SHORT).show()
        return true
    }
}


@Composable
@Preview(showBackground = true)
fun WeatherApp(modifier: Modifier = Modifier) {
    val mContext = LocalContext.current
    var count by remember { mutableStateOf(0) }
    var settings = Settings(
        city_id = remember { mutableStateOf("hyogo_kobe") }
    )

    WeatherAppTheme {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppGlobalNav(
                context = mContext,
                settings = settings
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = settings.city_id.value)

                Button(
                    onClick = {
                        count = (count + 1) % 3
                        settings.city_id.value = when(count) {
                            0 -> "hyogo_kobe"
                            1 -> "osaka_osaka"
                            else -> "somewhere"
                        }
                        Log.d("Test", "count = ${settings.city_id.value}")
                    }
                ) {
                    Text("Tap me!")
                }
            }

        }
    }
}
