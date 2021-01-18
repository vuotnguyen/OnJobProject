package com.example.onjobproject.model


import javax.inject.Inject


class User() {
    lateinit var name:String
    lateinit var avatar:String
    lateinit var mail:String
    var tokenDivice: String? = null

    @Inject
    constructor(name:String, avatar:String, mail:String, tokenDivice: String?) : this() {
        this.name = name
        this.avatar = avatar
        this.mail = mail
        this.tokenDivice = tokenDivice
    }

    override fun toString(): String {
        return name + avatar + mail + tokenDivice
    }

}