import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.weatherapp.R
import java.util.Calendar

@Composable
fun weatherIconResource(weather: String, hour24: Int): Painter {
    val isDayTime = (hour24 >= 7) && (hour24 <= 17)

    return painterResource(
        id = when (weather) {
            "sunny" -> when (isDayTime) {
                true -> R.drawable.wt_clear_day
                else -> R.drawable.wt_clear_night
            }
            "cloudy" -> when (isDayTime) {
                true -> R.drawable.wt_cloud_day
                else -> R.drawable.wt_cloud_night
            }
            "rain" -> when (isDayTime) {
                true -> R.drawable.wt_rain_day
                else -> R.drawable.wt_rain_night
            }
            "snow" -> when (isDayTime) {
                true -> R.drawable.wt_snow_day
                else -> R.drawable.wt_snow_night
            }

            else -> R.drawable.wt_unknown
        }
    )
}
