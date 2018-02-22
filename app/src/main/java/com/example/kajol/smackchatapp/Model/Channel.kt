package com.example.kajol.smackchatapp.Model

/**
 * Created by Kajol on 2/20/2018.
 */
class Channel(val name: String, val description: String, val id: String){
    override fun toString(): String {
        return "#$name"
    }
}