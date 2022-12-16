package com.example.weatherapp.network.cache

import okhttp3.Interceptor
import java.net.HttpURLConnection
import java.util.Calendar
import java.util.TimeZone

class ETagInspector(
    private val cacheDao: CacheDao
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        var request = chain.request()
        val url = request.url().toString()

        // Add "If-None-Match" header if cached ETag is available
        val cache = cacheDao.get(url)
            ?: CacheValue(
                url = url,
                eTag = "",
                lastModified = ""
            )
        if (!request.url().queryParameter("_cache").equals("false") &&
            cache.eTag != ""
        ) {
            request = request.newBuilder()
                .addHeader("If-None-Match", cache.eTag)
                .addHeader("If-Modified-Since", cache.lastModified)
                .build()
        }

        // Process the HTTP request
        val response = chain.proceed(request)
        if ((response.code() == HttpURLConnection.HTTP_NOT_MODIFIED)) {
            return response
        }

        // Save ETag and Last-Modified values to the Cache DB
        cache.eTag = response.header("ETag") ?: ""
        cache.lastModified = response.header("Last-Modified") ?: getCurrentTime()

        cacheDao.put(cache)

        return response
    }

    private fun getCurrentTime(): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"))

        val weekday = arrayOf(
            "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
        )[cal.get(Calendar.DAY_OF_WEEK) - 1]

        val month = arrayOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
            "Aug", "Sep", "Oct", "Nov", "Dec"
        )[cal.get(Calendar.MONTH)]

        val day = cal.get(Calendar.DAY_OF_MONTH)

        val year = cal.get(Calendar.YEAR)

        val hour = "%02d".format(cal.get(Calendar.HOUR))
        val minute = "%02d".format(cal.get(Calendar.MINUTE))
        val second = "%02d".format(cal.get(Calendar.SECOND))

        // i.e. "Wed, 21 Oct 2015 07:28:00 GMT"
        return "$weekday, $day $month $year $hour:$minute:$second GMT"
    }
}
