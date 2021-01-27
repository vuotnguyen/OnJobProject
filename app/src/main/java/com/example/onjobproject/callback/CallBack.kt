package com.example.onjobproject.callback

interface CallBack {
    fun showFragment()
    fun showLiss(mail: String, token: String?, checkTokenChang: Boolean)
    fun showLogin(mail: String?, pass: String?)
    fun back()
}