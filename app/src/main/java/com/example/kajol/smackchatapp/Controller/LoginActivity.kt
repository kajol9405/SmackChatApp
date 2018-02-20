package com.example.kajol.smackchatapp.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.kajol.smackchatapp.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginBtnClicked(view : View){

    }
    fun loginCreateUserBtnClicked(view : View){

        val createUserIntent = Intent(this, createUserActivity::class.java)
        startActivity(createUserIntent)

    }

}

