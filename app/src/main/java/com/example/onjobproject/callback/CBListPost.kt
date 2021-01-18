package com.example.onjobproject.callback

import com.example.onjobproject.model.Comment

interface CBListPost {
    fun clickLike(likes: ArrayList<String>?,post: Int)
    fun clickComment(comments: ArrayList<Comment>,post: Int)
    fun startVideo(url: String)
}