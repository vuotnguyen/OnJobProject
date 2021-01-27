package com.example.onjobproject.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import javax.inject.Inject


class Post  (){
    lateinit var user: User
    lateinit var title: String
    lateinit var detail: String
    lateinit var url: String
    var type: Int = 0
    lateinit var date: String
    var likes: ArrayList<String>? = null
    var comments: ArrayList<Comment>? = null
     var share: Int = 0
    @Inject
    constructor( user: User, title: String,  detail: String, url: String,type:Int, date: String, likes: ArrayList<String>, comments: ArrayList<Comment>, share: Int) : this() {
        this.user = user
        this.title = title
        this.detail = detail
        this.url = url
        this.type = type
        this.date = date
        this.likes = likes
        this.comments = comments
        this.share = share
    }

//    override fun toString(): String {
////        return title + detail + url + date + likes + comments + share
////    }

}
