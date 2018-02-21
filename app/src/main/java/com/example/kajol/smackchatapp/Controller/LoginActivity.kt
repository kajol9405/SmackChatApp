package com.example.kajol.smackchatapp.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.kajol.smackchatapp.R
import com.example.kajol.smackchatapp.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginBtnClicked(view : View){

        val email = loginEmailText.text.toString()
        val password = loginPasswordText.text.toString()

        AuthService.loginUser(this,email,password){loginSuccess ->
            if(loginSuccess){
                Toast.makeText(this,"Login success",Toast.LENGTH_LONG).show()
                AuthService.findUserByEmail(this){findSuccess ->
                    if(findSuccess){
                        finish()
                    }
                }
            }

        }

    }
    fun loginCreateUserBtnClicked(view : View){

        val createUserIntent = Intent(this, createUserActivity::class.java)
        startActivity(createUserIntent)
        finish()

    }

}

