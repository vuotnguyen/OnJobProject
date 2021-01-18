package com.example.onjobproject.model

class Notify(var title: String,var body: String){
    override fun toString(): String {
        return title + body
    }
}