package com.example.kajol.smackchatapp.Controller

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.kajol.smackchatapp.R
import com.example.kajol.smackchatapp.Services.AuthService
import com.example.kajol.smackchatapp.Services.UserDataService
import com.example.kajol.smackchatapp.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class createUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createSpinner.visibility = View.INVISIBLE
    }

    fun generateUserAvatar(view: View) {

        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        if (color == 0) {
            userAvatar = "light$avatar"
        } else {
            userAvatar = "dark$avatar"
        }

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        createUserAvatarImage.setImageResource(resourceId)

    }

    fun generateColorClicked(view: View) {

        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        createUserAvatarImage.setBackgroundColor(Color.rgb(r, g, b))
        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255

        avatarColor = "[$savedR, $savedG, $savedB, 1]"

    }

    fun createUserBtnClicked(view: View) {

        enableSpinner(true)

        val userEmail = createUserEmail.text.toString()
        val password = createUserPassword.text.toString()
        val userName = createUserName.text.toString()

        if(userName.isNotEmpty() && userEmail.isNotEmpty() && password.isNotEmpty()){

            AuthService.registerUser(this, userEmail, password) { complete ->
                if (complete) {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_LONG).show()
                    AuthService.loginUser(this, userEmail, password) { loginSuccess ->
                        if (loginSuccess) {
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show()
                            AuthService.createUser(this, userName, userEmail, userAvatar, avatarColor) { createSuccess ->
                                if (createSuccess) {

                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)

                                    enableSpinner(false)
                                    finish()
                                } else {
                                    errorToast()
                                }

                            }
                        } else {
                            errorToast()
                        }
                    }

                } else {
                    errorToast()
                }

            }
        }else{
            Toast.makeText(this, "Make sure user name, email and password are filled in", Toast.LENGTH_LONG).show()
            enableSpinner(false)
        }
    }

    fun errorToast(){
        Toast.makeText(this, "Something went wrong! please try again!", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable: Boolean) {
        if (enable) {
            createSpinner.visibility = View.VISIBLE

        } else {
            createSpinner.visibility = View.INVISIBLE
        }
        createUserBtn.isEnabled = !enable
        createUserAvatarImage.isEnabled = !enable
        generateColorBtn.isEnabled = !enable

    }


}
