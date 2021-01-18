package com.example.onjobproject.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onjobproject.R
import com.example.onjobproject.model.Comment
import com.example.onjobproject.model.Post
import kotlinx.android.synthetic.main.comment_adapter.view.*

class CommentAdapter (): RecyclerView.Adapter<HolderItemCommet>() {
    lateinit var context: Context
    lateinit var list: ArrayList<Comment>

    constructor(context: Context, list: ArrayList<Comment>) : this(){
        this.context = context
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderItemCommet {
        val view = LayoutInflater.from(context).inflate(R.layout.comment_adapter,parent,false)
        return HolderItemCommet(view)
    }



    override fun onBindViewHolder(holder: HolderItemCommet, position: Int) {
        val comment = list.get(position)
        holder.onBindData(comment)
    }
    override fun getItemCount(): Int {
        return list.size
    }
    fun filterList(comments: ArrayList<Comment>) {
        list = comments
        notifyDataSetChanged()
    }

}
class HolderItemCommet(view: View): RecyclerView.ViewHolder(view) {
    fun onBindData(comment: Comment) {
        Glide.with(itemView.context).load(comment.user.avatar).into((itemView.img_avatar))
        itemView.tv_user.text = comment.user.name
        itemView.tv_comment.text = comment.content


    }
}