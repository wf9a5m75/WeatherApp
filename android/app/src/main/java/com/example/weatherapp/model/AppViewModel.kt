package com.example.weatherapp.model

import android.content.Context
import androidx.lifecycle.ViewModel


class AppViewModel : ViewModel() {
    var city: City = City("", "")

    val cities: MutableList<Prefecture> = mutableListOf()
}