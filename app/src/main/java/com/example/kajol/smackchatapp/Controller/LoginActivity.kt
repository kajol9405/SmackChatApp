package com.example.kajol.smackchatapp.Controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.kajol.smackchatapp.R
import com.example.kajol.smackchatapp.Services.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginSpinner.visibility = View.INVISIBLE
    }

    fun loginBtnClicked(view : View){

        enableSpinner(true)
        val email = loginEmailText.text.toString()
        val password = loginPasswordText.text.toString()
        hideKeyBoard()
        if(email.isNotEmpty() && password.isNotEmpty()){
            AuthService.loginUser(this,email,password){loginSuccess ->
                if(loginSuccess){
                    Toast.makeText(this,"Login success",Toast.LENGTH_LONG).show()
                    AuthService.findUserByEmail(this){findSuccess ->
                        if(findSuccess){
                            enableSpinner(false)
                            finish()
                        }else{
                            errorToast()
                        }
                    }
                }else{
                    errorToast()
                }

            }
        }else{
            Toast.makeText(this, "Please fill in email and password!", Toast.LENGTH_LONG).show()
        }


    }
    fun errorToast(){
        Toast.makeText(this, "Something went wrong! please try again!", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }
    fun loginCreateUserBtnClicked(view : View){

        val createUserIntent = Intent(this, createUserActivity::class.java)
        startActivity(createUserIntent)
        finish()

    }


    fun enableSpinner(enable: Boolean) {
        if (enable) {
            loginSpinner.visibility = View.VISIBLE

        } else {
            loginSpinner.visibility = View.INVISIBLE
        }
        loginButton.isEnabled = !enable
        loginCreateUserBtn.isEnabled = !enable

    }

    fun hideKeyBoard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromInputMethod(currentFocus.windowToken,0)
        }
    }

}

