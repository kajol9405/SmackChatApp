package com.example.kajol.smackchatapp.Services

import android.content.Context
import android.graphics.Color
import com.example.kajol.smackchatapp.Controller.App
import java.util.*

/**
 * Created by Kajol on 2/20/2018.
 */
object UserDataService {

    var id = ""
    var avatarColor =""
    var avatarName =""
    var email=""
    var name=""

    fun logout(){

         id = ""
         avatarColor =""
         avatarName =""
         email=""
         name=""
         App.sharedPreferences.authToken =""
         App.sharedPreferences.userEmail =""
         App.sharedPreferences.isLoggedIn =false
         MessageService.clearMessages()
         MessageService.clearChannels()

    }
    fun returnAvatarColor(components: String ): Int{
        //"[0.00784313725490196, 0.9215686274509803, 0.36470588235294116, 1]"
        //"0.00784313725490196  0.9215686274509803  0.36470588235294116, 1

        val stripedColor = components
                .replace("[","")
                .replace("]","")
                .replace(",","")
        var r = 0
        var g = 0
        var b = 0

        var scanner = Scanner(stripedColor)
        if(scanner.hasNext()){
            r = ((scanner.nextDouble())* 255).toInt()
            g = ((scanner.nextDouble())* 255).toInt()
            b = ((scanner.nextDouble())* 255).toInt()
        }

        return Color.rgb(r,g,b)

    }


}