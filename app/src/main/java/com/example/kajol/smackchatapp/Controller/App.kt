package com.example.kajol.smackchatapp.Controller

import android.app.Application
import android.content.SharedPreferences
import com.example.kajol.smackchatapp.Utilities.SharedPrefs

/**
 * Created by Kajol on 2/21/2018.
 */
class App : Application() {

    companion object {
        lateinit var sharedPreferences: SharedPrefs
    }

    override fun onCreate() {
        sharedPreferences = SharedPrefs(applicationContext)
        super.onCreate()
    }
}