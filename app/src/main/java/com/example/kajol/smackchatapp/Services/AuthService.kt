package com.example.kajol.smackchatapp.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.kajol.smackchatapp.Utilities.URL_CREATE_USER
import com.example.kajol.smackchatapp.Utilities.URL_LOGIN
import com.example.kajol.smackchatapp.Utilities.URL_REGISTER
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Method

/**
 * Created by Kajol on 2/19/2018.
 */
object AuthService {

    var isLoggedIn = false
    var userEmail =" "
    var authToken =" "

    fun registerUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {
        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)

        val requestBody = jsonBody.toString()

        val registerRequest = object : StringRequest(Method.POST, URL_REGISTER, Response.Listener { response ->
            println(response)
            complete(true)
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Couldn't register user: $error")
            complete(false)
        }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

        }

        Volley.newRequestQueue(context).add(registerRequest)
    }

    fun loginUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener { response ->

            try {
                userEmail = response.getString("user")
                authToken = response.getString("token")
                isLoggedIn = true

            }catch (e: JSONException){
                Log.d("JSON", "EXCEPTION: $e")
            }
            complete(true)
        }, Response.ErrorListener { error ->
            //this is where we handle our errors
            Log.d("ERROR", "Couldn't register user: $error")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
        Volley.newRequestQueue(context).add(loginRequest)
    }

    fun createUser(context: Context,name:String, email: String,avatarName: String, avatarColor : String, complete : (Boolean)-> Unit){

        val jsonBody = JSONObject()
        jsonBody.put("name", name)
        jsonBody.put("email", email)
        jsonBody.put("avatarName", avatarName)
        jsonBody.put("avatarColor", avatarColor)
        val requestBody = jsonBody.toString()

        val createRequest = object : JsonObjectRequest(Method.POST, URL_CREATE_USER, null, Response.Listener { response ->

            try {
                UserDataService.avatarName = response.getString("avatarName")
                UserDataService.avatarColor = response.getString("avatarColor")
                UserDataService.email =  response.getString("email")
                UserDataService.id =  response.getString("_id")
                UserDataService.name =  response.getString("name")

            }catch (e: JSONException){
                Log.d("JSON", "EXCEPTION: $e")
            }
            complete(true)

        },Response.ErrorListener { error ->
            Log.d("ERROR", "Couldn't add user: $error")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

             override fun getHeaders(): MutableMap<String, String> {
                 val headers = HashMap<String,String>()
                 headers.put("Authorization","Bearer $authToken")
                 return headers
            }
        }
        Volley.newRequestQueue(context).add(createRequest)
    }
}