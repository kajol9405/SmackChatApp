package com.example.kajol.smackchatapp.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.example.kajol.smackchatapp.Controller.App
import com.example.kajol.smackchatapp.Model.Channel
import com.example.kajol.smackchatapp.Model.Message
import com.example.kajol.smackchatapp.Utilities.URL_GET_CHANNELS
import com.example.kajol.smackchatapp.Utilities.URL_GET_MESSAGES
import org.json.JSONException
import java.lang.reflect.Method

/**
 * Created by Kajol on 2/20/2018.
 */
object MessageService {
    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getChannels(complete : (Boolean) -> Unit){

        val channelsRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null , Response.Listener {response ->
            try{

                for(x in 0 until response.length()){
                    val channel = response.getJSONObject(x)
                    val name = channel.getString("name")
                    val channelDesc = channel.getString("description")
                    val channelId = channel.getString("_id")

                    val newChannel = Channel(name, channelDesc, channelId)
                    this.channels.add(newChannel)
                }

                complete(true)

            }catch (e: JSONException){
                Log.d("JSON", "EXC:" + e.localizedMessage)
                complete(false)
            }


        },Response.ErrorListener { error ->
            Log.d("ERROR", "Couldn't retrieve channels")
            complete(false)

        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers.put("Authorization","Bearer ${App.sharedPreferences.authToken}")
                return headers
            }
        }
        App.sharedPreferences.requestQueue.add(channelsRequest)
    }

    fun getMessages(channelId : String,complete: (Boolean) -> Unit){

        val url = "$URL_GET_MESSAGES$channelId"

        val messagesRequest = object : JsonArrayRequest(Method.GET,url,null,Response.Listener { response ->
            clearMessages()
            try{

                for(x in 0 until response.length()){
                    val message = response.getJSONObject(x)
                    val messageBody = message.getString("messageBody")
                    val channelId = message.getString("channelId")
                    val id = message.getString("_id")
                    val userName = message.getString("userName")
                    val userAvatar = message.getString("userAvatar")
                    val userAvatarColor = message.getString("userAvatarColor")
                    val timeStamp = message.getString("timeStamp")

                    val newMessage = Message(messageBody, userName, channelId, userAvatar, userAvatarColor, id, timeStamp)
                    this.messages.add(newMessage)
                }

                complete(true)

            }catch (e: JSONException){
                Log.d("JSON", "EXC:" + e.localizedMessage)
                complete(false)
            }



        }, Response.ErrorListener { error ->

            Log.d("ERROR", "Couldn't retrieve messages")
            complete(false)

        }){

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers.put("Authorization","Bearer ${App.sharedPreferences.authToken}")
                return headers
            }

        }

        App.sharedPreferences.requestQueue.add(messagesRequest)
    }

    fun clearMessages(){
        messages.clear()
    }

    fun clearChannels(){
        channels.clear()
    }
}