package com.example.moneytrackerapp

import android.app.Application
import com.example.moneytrackerapp.data.container.AppContainer
import com.example.moneytrackerapp.data.container.AppDataContainer

class MoneyTrackerApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(context = this)
    }

}