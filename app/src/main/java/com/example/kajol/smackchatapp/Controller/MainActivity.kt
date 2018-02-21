package com.example.kajol.smackchatapp.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.example.kajol.smackchatapp.R
import com.example.kajol.smackchatapp.Services.AuthService
import com.example.kajol.smackchatapp.Services.UserDataService
import com.example.kajol.smackchatapp.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        hideKeyBoard()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE))
    }

    private val userDataChangeReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
           if(AuthService.isLoggedIn){
               userNameNavHeader.text = UserDataService.name
               userEmailNavHeader.text = UserDataService.email

               val resourceId = resources.getIdentifier(UserDataService.avatarName,"drawable",packageName)
               userImageNavHeader.setImageResource(resourceId)
               userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
               userLoginbtn.text = "Logout"
           }
        }
    }

    fun addChannelBtnClicked(view: View){

        if(AuthService.isLoggedIn){
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog,null)
            builder.setView(dialogView)
                    .setPositiveButton("Add"){dialogInterface, i ->
                        // perform when clicked ok
                        val nameTextField = dialogView.findViewById<EditText>(R.id.addChannelName)
                        val descTextField = dialogView.findViewById<EditText>(R.id.addChannelDescription)
                        val channelName = nameTextField.text.toString()
                        val channelDesc = descTextField.text.toString()

                        hideKeyBoard()

                        // create channel with the channel name and description

                    }.setNegativeButton("Cancel"){
                        dialogInterface, i ->
                        // cancel and close the dialog
                        hideKeyBoard()
                    }
                    .show()
        }
    }

    fun userLoginClicked(view : View){

        if(AuthService.isLoggedIn){
           UserDataService.logout()
            userNameNavHeader.text = ""
            userEmailNavHeader.text =""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            userLoginbtn.text = "Login"

        }else{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun sendMessageBtnClicked(view: View){

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun hideKeyBoard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromInputMethod(currentFocus.windowToken,0)
        }
    }

}
