package com.example.onjobproject.model

import androidx.annotation.Keep
import javax.inject.Inject


class Comment() {
    lateinit var user: User
    lateinit var content: String

    @Inject
    constructor( user: User, content: String) : this() {
        this.user = user
        this.content = content
    }
}
