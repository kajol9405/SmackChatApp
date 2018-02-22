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
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.example.kajol.smackchatapp.MessageAdapter
import com.example.kajol.smackchatapp.Model.Channel
import com.example.kajol.smackchatapp.Model.Message
import com.example.kajol.smackchatapp.R
import com.example.kajol.smackchatapp.Services.AuthService
import com.example.kajol.smackchatapp.Services.MessageService
import com.example.kajol.smackchatapp.Services.UserDataService
import com.example.kajol.smackchatapp.Utilities.BROADCAST_USER_DATA_CHANGE
import com.example.kajol.smackchatapp.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>
    lateinit var messageAdapter: MessageAdapter
    var selectedChannel : Channel ? = null

    private fun setupAdapters(){

           channelAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,MessageService.channels)
           channelList.adapter = channelAdapter

           messageAdapter = MessageAdapter(this, MessageService.messages)
           messageList.adapter = messageAdapter

           val layoutManager = LinearLayoutManager(this)
           messageList.layoutManager = layoutManager

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        socket.connect()
        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated", onNewMessage)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        setupAdapters()
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE))
        channelList.setOnItemClickListener { _, _, i, _ ->
            selectedChannel = MessageService.channels[i]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        if(App.sharedPreferences.isLoggedIn){
            AuthService.findUserByEmail(this){
            }
        }
    }

    private val userDataChangeReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent?) {
           if(App.sharedPreferences.isLoggedIn){
               userNameNavHeader.text = UserDataService.name
               userEmailNavHeader.text = UserDataService.email

               val resourceId = resources.getIdentifier(UserDataService.avatarName,"drawable",packageName)
               userImageNavHeader.setImageResource(resourceId)
               userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
               userLoginbtn.text = "Logout"

               MessageService.getChannels{complete->
                   if(complete){
                         Log.e("check","size of array ${MessageService.channels.size}" )
                        if(complete){
                            if(MessageService.channels.count()>0){
                                selectedChannel = MessageService.channels[0]
                                channelAdapter.notifyDataSetChanged()
                                updateWithChannel()
                            }

                        }

                   }
               }
           }
        }
    }

    fun updateWithChannel(){
        mainChannelName.text = "#${selectedChannel?.name}"
        // download messages for channel
        if(selectedChannel!=null){
            MessageService.getMessages(selectedChannel!!.id){complete->
                if(complete){
                    messageAdapter.notifyDataSetChanged()
                    if(messageAdapter.itemCount>0){
                        messageList.smoothScrollToPosition(messageAdapter.itemCount-1)
                    }
                }
            }
        }
    }
    override fun onPause() {

        socket.connect()
        super.onPause()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        socket.disconnect()
        super.onDestroy()
    }

    fun addChannelBtnClicked(view: View){

        if(App.sharedPreferences.isLoggedIn){
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog,null)
            builder.setView(dialogView)
                    .setPositiveButton("Add"){dialogInterface, i ->
                        // perform when clicked ok
                        val nameTextField = dialogView.findViewById<EditText>(R.id.addChannelName)
                        val descTextField = dialogView.findViewById<EditText>(R.id.addChannelDescription)
                        val channelName = nameTextField.text.toString()
                        val channelDesc = descTextField.text.toString()

                        // create channel with the channel name and description
                        socket.emit("newChannel",channelName,channelDesc)

                    }.setNegativeButton("Cancel"){
                        dialogInterface, i ->
                        // cancel and close the dialog
                    }
                    .show()
        }
    }

    private val onNewChannel = Emitter.Listener {args ->

        if(App.sharedPreferences.isLoggedIn) {
            runOnUiThread {
                val channelName = args[0] as String
                val channelDescription = args[1] as String
                val channelId = args[2] as String

                val newChannel = Channel(channelName, channelDescription, channelId)
                MessageService.channels.add(newChannel)
                channelAdapter.notifyDataSetChanged()

            }
        }
    }

    private val onNewMessage = Emitter.Listener {args ->

        if(App.sharedPreferences.isLoggedIn) {
            runOnUiThread {
                val channelId = args[2] as String
                if(channelId == selectedChannel?.id){
                    val msgBody = args[0] as String

                    val userName = args[3] as String
                    val userAvatar = args[4] as String
                    val userAvatarColor = args[5] as String
                    val id = args[6] as String
                    val timeStamp = args[7] as String

                    val newMessage = Message(msgBody, userName, channelId, userAvatar, userAvatarColor, id, timeStamp)
                    MessageService.messages.add(newMessage)
                    messageAdapter.notifyDataSetChanged()
                    messageList.smoothScrollToPosition(messageAdapter.itemCount-1)
                }
            }
        }
    }

    fun userLoginClicked(view : View){

        if(App.sharedPreferences.isLoggedIn){
            UserDataService.logout()
            channelAdapter.notifyDataSetChanged()
            messageAdapter.notifyDataSetChanged()
            userNameNavHeader.text = ""
            userEmailNavHeader.text =""
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            userLoginbtn.text = "Login"
            mainChannelName.text ="Please Login"

        }else{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun sendMessageBtnClicked(view: View){

         if(App.sharedPreferences.isLoggedIn && messageTextField.text.isNotEmpty() && selectedChannel !=null){
             val userId = UserDataService.id
             val channelId = selectedChannel!!.id
             socket.emit("newMessage",messageTextField.text.toString(),userId,channelId,
                     UserDataService.name,UserDataService.avatarName,UserDataService.avatarColor)
             messageTextField.text.clear()
             hideKeyBoard()
         }
            hideKeyBoard()
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
