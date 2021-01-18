package com.example.onjobproject.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onjobproject.R
import com.example.onjobproject.callback.CBListPost
import com.example.onjobproject.model.Comment
import com.example.onjobproject.model.Post
import com.example.onjobproject.view.fragment.ListPostFragment
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.viewtype1_adapter.view.*
import kotlinx.android.synthetic.main.viewtype1_adapter.view.img_avatar
import kotlinx.android.synthetic.main.viewtype1_adapter.view.img_comment
import kotlinx.android.synthetic.main.viewtype1_adapter.view.img_like
import kotlinx.android.synthetic.main.viewtype1_adapter.view.tv_comment
import kotlinx.android.synthetic.main.viewtype1_adapter.view.tv_detail
import kotlinx.android.synthetic.main.viewtype1_adapter.view.tv_like
import kotlinx.android.synthetic.main.viewtype1_adapter.view.tv_name
import kotlinx.android.synthetic.main.viewtype1_adapter.view.tv_share
import kotlinx.android.synthetic.main.viewtype1_adapter.view.tv_title
import kotlinx.android.synthetic.main.viewtype2_adapter.view.*
import kotlin.collections.ArrayList

class PostAdapter(
    private var list: ArrayList<Post>,
    private var context: Context,
    private val mail: String,
    private val cbListPost: CBListPost,
    private val videoPlayer: SimpleExoPlayer

): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var likes: ArrayList<String>? = null
    private var comments: ArrayList<Comment>? = null
    private lateinit var adapterModel: Post
    private var typeUpdate: Int? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType){
            ViewType.TYPE_ONE.type -> {
                val view  = inflater.inflate(R.layout.viewtype1_adapter,parent,false)
                HolderItem1(view)
            }
            else ->{
                val view = inflater.inflate(R.layout.viewtype2_adapter,parent,false)
                HolderItem2(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position].type){
            0 -> ViewType.TYPE_ONE.type
            else -> ViewType.TYPE_TWO.type
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        adapterModel = list[position]
        holder.apply {
            when (holder) {
                is HolderItem1 -> holder.bindData(adapterModel,mail,position,cbListPost)
                is HolderItem2 -> holder.bindData(adapterModel,mail,position,cbListPost,videoPlayer)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {

        if (payloads.isEmpty()) super.onBindViewHolder(holder, position, payloads)
        else {
            if(typeUpdate == ListPostFragment.TYPE_CLICK){
                if(likes == null){
                    holder.itemView.tv_like.text = "0 lượt thích"
                } else holder.itemView.tv_like.text = "${list.get(position).likes!!.size} lượt thích"
            }
            if(typeUpdate == ListPostFragment.TYPE_COMMENT){
                holder.itemView.tv_comment.text = "${comments?.size} comment"
            }
        }
    }

    fun updateLike(
        pos: Int,
        likes: ArrayList<String>?,
        typeClick: Int
    ){
        this.likes = ArrayList()
        adapterModel = list[pos]
        adapterModel.likes = likes
        typeUpdate = typeClick
       notifyItemChanged(pos,likes)
        if (likes != null) {
            this.likes = likes
        } else this.likes = null
    }
    fun updateComment(
        pos: Int,
        comments: ArrayList<Comment>,
        typeComment: Int
    ){
        this.comments = ArrayList()
        adapterModel = list[pos]
        adapterModel.comments = comments
        typeUpdate = typeComment
        notifyItemChanged(pos,comments.toMutableList())
        this.comments = comments

    }
    fun filterList(listSearch: ArrayList<Post>) {
        list = listSearch
        notifyDataSetChanged()
    }

}

class HolderItem1(view: View) : RecyclerView.ViewHolder(view) {
    fun bindData(
        post: Post,
        mail: String,
        position: Int,
        cbListPost: CBListPost
    ) {
        Glide.with(itemView.context).load(post.user.avatar).into(itemView.img_avatar)
        itemView.tv_name.text = post.user.name
        itemView.tv_title.text = post.title
        itemView.tv_detail.text = post.detail
        if(post.likes == null)itemView.tv_like.text = "0 lượt thích"
        else itemView.tv_like.text = "${post.likes!!.size} lượt thích"
        itemView.tv_comment.text = "${post.comments.size} bình luận"
        itemView.tv_share.text = "${post.share} chia sẻ"
        Glide.with(itemView.context).load(post.url).into(itemView.img_image)


        itemView.img_like.setImageLevel(checkLike(mail, post.likes))
        itemView.img_like.setOnClickListener {
            if(itemView.img_like.drawable.level == 0){
                itemView.img_like.setImageLevel(1)
            }else{
                itemView.img_like.setImageLevel(0)
            }
            cbListPost.clickLike(post.likes,adapterPosition)
        }

        itemView.img_comment.setOnClickListener {
            cbListPost.clickComment(post.comments,position)
        }
    }

    private fun checkLike(mail: String, likes: ArrayList<String>?): Int {
        if(likes == null){
            return 0
        }
        likes.forEach{
            if(mail == it){
                return 1
            }
        }
        return 0
    }
}
class HolderItem2(view: View) : RecyclerView.ViewHolder(view) {
    fun bindData(
        post: Post,
        mail: String,
        position: Int,
        cbListPost: CBListPost,
        videoPlayer: SimpleExoPlayer
    ) {
        Glide.with(itemView.context).load(post.user.avatar).into(itemView.img_avatar)
        itemView.tv_name.text = post.user.name
        itemView.tv_title.text = post.title
        itemView.tv_detail.text = post.detail
        if(post.likes == null)itemView.tv_like.text = "0 lượt thích"
        else itemView.tv_like.text = "${post.likes!!.size} lượt thích"
        itemView.tv_comment.text = "${post.comments.size} bình luận"
        itemView.tv_share.text = "${post.share} chia sẻ"

        itemView.img_like.setImageLevel(checkLike(mail, post.likes))
        itemView.img_like.setOnClickListener {
            if(itemView.img_like.drawable.level == 0){
                itemView.img_like.setImageLevel(1)
            }else{
                itemView.img_like.setImageLevel(0)
            }
            cbListPost.clickLike(post.likes,adapterPosition)
        }

        itemView.img_comment.setOnClickListener {
            cbListPost.clickComment(post.comments,position)
        }

        loadVideo(post,cbListPost,videoPlayer)
    }

    private fun checkLike(mail: String, likes: ArrayList<String>?): Int {
        if(likes == null){
            return 0
        }
        likes.forEach{
            if(mail == it){
                return 1
            }
        }
        return 0
    }

    private fun loadVideo(post: Post, cbListPost: CBListPost, videoPlayer: SimpleExoPlayer) {
        itemView.exo_player.controllerAutoShow = false
        itemView.exo_player.controllerShowTimeoutMs = 2000
        itemView.exo_player.player = videoPlayer
        cbListPost.startVideo(post.url)
        itemView.exo_player.requestFocus()
    }
}
enum class ViewType(val type: Int) {
    TYPE_ONE(0),
    TYPE_TWO(1)
}
