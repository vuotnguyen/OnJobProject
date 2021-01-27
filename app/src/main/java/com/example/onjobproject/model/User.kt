package com.example.onjobproject.model


import com.google.gson.annotations.SerializedName
import javax.inject.Inject


class User() {
    lateinit var name:String
    lateinit var avatar:String
    lateinit var mail:String
    var token: String? = null

    @Inject
    constructor(name:String, avatar:String, mail:String, token: String?) : this() {
        this.name = name
        this.avatar = avatar
        this.mail = mail
        this.token = token
    }

//    override fun toString(): String {
//        return name + avatar + mail + tokenDevice
//    }

}