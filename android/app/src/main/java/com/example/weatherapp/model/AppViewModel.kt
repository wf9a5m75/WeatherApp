package com.example.weatherapp.model

import androidx.lifecycle.ViewModel


class AppViewModel : ViewModel() {
    var city: City = City("", "")

    val cities: MutableList<Prefecture> = mutableListOf()
}