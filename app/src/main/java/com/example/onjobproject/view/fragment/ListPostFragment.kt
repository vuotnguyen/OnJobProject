package com.example.onjobproject.view.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onjobproject.R
import com.example.onjobproject.callback.CBListPost
import com.example.onjobproject.model.*
import com.example.onjobproject.view.adapter.CommentAdapter
import com.example.onjobproject.view.adapter.PostAdapter
import com.example.onjobproject.viewmodel.MyFireBaseMessageService
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.bottom_sheet_comment.*
import kotlinx.android.synthetic.main.bottom_sheet_comment.view.*
import kotlinx.android.synthetic.main.listpost_fragment.view.*

class ListPostFragment() : BaseFragment(), CBListPost {

    companion object{

        val TYPE_COMMENT = 1
        val TYPE_CLICK = 0
        lateinit var btSheetBehavior: BottomSheetBehavior<LinearLayout>
    }


    private lateinit var myFireBaseMessageService: MyFireBaseMessageService
    private lateinit var videoPlayer: SimpleExoPlayer
    private var pos: Int = 0
    private lateinit var comments: ArrayList<Comment>
    private lateinit var posts: ArrayList<Post>
    private lateinit var users: ArrayList<User>

    private lateinit var adapter: PostAdapter
    private lateinit var adapterComment: CommentAdapter
    private lateinit var mail: String
    lateinit var user: User
    val TAG: String = ListPostFragment::class.qualifiedName.toString()

    constructor(mail: String) : this(){
        this.mail = mail
    }

    override fun getLayoutID(): Int {
        return R.layout.listpost_fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btSheetBehavior = BottomSheetBehavior.from(design_bottom_sheet)

        adapterComment = CommentAdapter(view.context,comments)
        view.rv_comment.layoutManager  = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        view.rv_comment.adapter = adapterComment

    }
    override fun initView(view: View) {
        myFireBaseMessageService = MyFireBaseMessageService()
        videoPlayer = SimpleExoPlayer.Builder(requireContext()).build()
        setHasOptionsMenu(true)

        posts  = ArrayList()
        adapter = PostAdapter(posts, view.context,mail,this,videoPlayer)
        view.rv_listpost.adapter = adapter

        postViewModel.getPostLivedata().observe(this, Observer {
            view.rv_listpost.layoutManager  = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            adapter.filterList(it)
            posts = it

        })
        userViewModel.getAllUser().observe(this, Observer {
            users = it
        })

        addComment(view)



    }

    private fun addComment(view: View) {
        comments = ArrayList()

        view.img_send.setOnClickListener {

            users.forEach {
                if(mail == it.mail) {
                    this.user = it
                    return@forEach
                }
            }
            val comment = view.edt_comment.text.toString()
            val commentUser = Comment(user,comment)
            comments.add(commentUser)
            postViewModel.commentUpdate(comments,pos).observe(this, Observer {
                adapterComment.filterList(it)
                adapter.updateComment(pos,it,TYPE_COMMENT)
            })
            view.edt_comment.text = null

        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu,menu)
    }

    override fun clickLike(likes: ArrayList<String>?, post: Int) {
            postViewModel.likeUpdate(mail,post,likes).observe(this, Observer {
                adapter.updateLike(post,it,TYPE_CLICK)
            })

//        Log.i("TAG", "clickLike: "+token)
//        userViewModel.sendNotifyUser(posts[post].user.name)
//        fhzRFF_ySlSdLWPkaYwhSE:APA91bE2VWgX7brbc3khSBT3eO9QiQNtHr7H66V1STq7MnLIXgcl6ECYhKhSkOgKNLr8Qvzf9-53gQvLncAFGgD38sye7Y1cXaa7hduzfmxUh-rPUFP-Fwl7bFG_sFEtkvSpG-LsFDha

    }

    override fun clickComment(comments: ArrayList<Comment>, pos: Int) {
        if(btSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED){
            btSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        }
        else btSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        adapterComment.filterList(comments)
        this.comments = comments
        this.pos = pos
    }


    override fun startVideo(url: String) {
        val dataSourseFactory = DefaultDataSourceFactory(requireContext(),"sample")
        ProgressiveMediaSource.Factory(dataSourseFactory).createMediaSource(Uri.parse(url))?.let {
            videoPlayer?.prepare(it)
            videoPlayer?.playWhenReady = true
        }
    }

    override fun onResume() {
        super.onResume()
        videoPlayer?.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        videoPlayer?.playWhenReady = false
        if(activity?.isDestroyed!!) videoPlayer.release()
    }

}

