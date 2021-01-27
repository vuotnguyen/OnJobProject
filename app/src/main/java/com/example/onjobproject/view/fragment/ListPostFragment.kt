package com.example.onjobproject.view.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.onjobproject.R
import com.example.onjobproject.callback.CBListPost
import com.example.onjobproject.callback.CallBack
import com.example.onjobproject.model.Comment
import com.example.onjobproject.model.Post
import com.example.onjobproject.model.User
import com.example.onjobproject.view.MainActivity
import com.example.onjobproject.view.adapter.CommentAdapter
import com.example.onjobproject.view.adapter.PostAdapter
import com.example.onjobproject.viewmodel.MyFireBaseMessageService
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_comment.*
import kotlinx.android.synthetic.main.bottom_sheet_comment.view.*
import kotlinx.android.synthetic.main.header_post.*
import kotlinx.android.synthetic.main.header_post.view.*
import kotlinx.android.synthetic.main.listpost_fragment.*
import kotlinx.android.synthetic.main.listpost_fragment.view.*
import kotlinx.android.synthetic.main.listpost_fragment.view.shimmer_container


class ListPostFragment() : BaseFragment(), CBListPost {

    companion object{
        val TYPE_COMMENT = 1
        val TYPE_CLICK = 0
        val TYPE_COMMENTLAST = 2
        lateinit var btSheetBehavior: BottomSheetBehavior<LinearLayout>
    }

    private lateinit var filePath: Uri
    private var typePost: Int = 0

    private val PICK_IMAGE_REQUEST: Int = 1511
    private val PICK_VIDEO_REQUEST: Int = 2109
    private lateinit var myFireBaseMessageService: MyFireBaseMessageService
    private lateinit var videoPlayer: SimpleExoPlayer
    private var pos: Int = 0
    private lateinit var comments: ArrayList<Comment>
    private lateinit var posts: ArrayList<Post>
    private lateinit var posts1: ArrayList<Post>
    private lateinit var users: ArrayList<User>

    private lateinit var adapter: PostAdapter
    private lateinit var adapterComment: CommentAdapter
    private lateinit var mail: String
    private lateinit var token: String
    private var checkTokenChang: Boolean = false
    lateinit var user: User
    private lateinit var callBack: CallBack
    val TAG: String = ListPostFragment::class.qualifiedName.toString()

    constructor(mail: String,token: String,checkTokenChang: Boolean,callBack: CallBack) : this(){
        this.mail = mail
        this.token = token
        this.checkTokenChang = checkTokenChang
        this.callBack = callBack
    }


    override fun getLayoutID(): Int {
        return R.layout.listpost_fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btSheetBehavior = BottomSheetBehavior.from(design_bottom_sheet)
    }
    override fun initView(view: View) {

//        view.swipe_layout.setOnRefreshListener {
//            showPost(view)
//        }

        comments = ArrayList()
        adapterComment = CommentAdapter(view.context,comments)
        view.rv_comment.layoutManager  = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        view.rv_comment.adapter = adapterComment

        myFireBaseMessageService = MyFireBaseMessageService()
        videoPlayer = SimpleExoPlayer.Builder(requireContext()).build()
        setHasOptionsMenu(true)

        posts  = ArrayList()
        posts1  = ArrayList()
        adapter = PostAdapter(posts, view.context,mail,this,videoPlayer)
        view.rv_listpost.adapter = adapter

        showPost(view)

        userViewModel.getAllUser().observe(this, Observer {
            users = it
        })

        addPost(view)
        addComment(view)
        logout(view)
        disPost(view)
        postPost(view)
    }

    private fun showPost(view: View) {
        postViewModel.getPostLivedata().observe(this, Observer {
            shimmer_container.stopShimmerAnimation()
            shimmer_container.visibility = View.GONE
            view.rv_listpost.layoutManager  = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

            adapter.filterList(it)
            posts = it
            posts.forEach {
                posts1.add(0,it)
            }
            if(checkTokenChang == true){
                postViewModel.updatePostUser(mail,token,posts)
            }
        })

    }


    private fun postPost(view: View) {
        var user = User()
        user.name = MainActivity.sharePref.getString("name","")!!
        user.mail = postViewModel.mAuth.currentUser!!.email.toString()
        user.token = MainActivity.sharePref.getString("token","")
        user.avatar = MainActivity.sharePref.getString("avatar","")!!
        val post = Post()
        view.tv_post.setOnClickListener{
            post.user = user
            post.comments = null
            post.likes = null
            post.share = 0
            post.title = "Cuộc sống sẻ chia"
            post.detail = edt_content_post.text.toString()
            post.date = "22/1/2021"
            post.url = filePath.toString()
            post.type = typePost

            val processDialog = ProgressDialog(requireContext())
            processDialog.setTitle("Uploading...")
            processDialog.show()
            posts.add(0,post)
            posts1.add(post)
            adapter.filterList(posts)
            postViewModel.upFileStorage(filePath,processDialog,post)
            disView(view)
        }
    }

    private fun disPost(view: View) {
        view.button2.setOnClickListener{
           disView(view)
        }
    }

    private fun disView(view: View) {
        view.edt_content_post.text = null
        view.button2.visibility = View.GONE
        view.imageView4.visibility = View.GONE
        view.imageView2.visibility = View.GONE
        view.tv_post.visibility = View.GONE
    }

    private fun addPost(view: View) {
        Glide.with(requireContext()).load(MainActivity.sharePref.getString("avatar","")!!).into(view.imageView)
        val intent = Intent()
        intent.setAction(Intent.ACTION_GET_CONTENT)
        view.tv_image.setOnClickListener{
            intent.setType("image/*")
            startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST)
        }
        view.button.setOnClickListener {
            intent.setType("video/*")
            startActivityForResult(Intent.createChooser(intent,"Select Video"),PICK_VIDEO_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && data != null && data.data != null){
            when (requestCode){
                PICK_IMAGE_REQUEST -> getImage(data)
                PICK_VIDEO_REQUEST -> getVideo(data)
            }
        }
    }

    private fun getVideo(data: Intent) {
        typePost = 1
        filePath = data.data!!

        try {
            button2.visibility = View.VISIBLE
            imageView4.visibility = View.VISIBLE
            imageView2.visibility = View.VISIBLE
            tv_post.visibility = View.VISIBLE
            Glide.with(requireContext()).load(filePath).into(imageView2)

        }catch (e: Exception){
            e.toString()
        }
    }

    private fun getImage(data: Intent) {
        typePost = 0
        filePath = data.data!!
        try {
            button2.visibility = View.VISIBLE
            imageView4.visibility = View.GONE
            imageView2.visibility = View.VISIBLE
            tv_post.visibility = View.VISIBLE
            Glide.with(requireContext()).load(filePath).into(imageView2)

        }catch (e: Exception){
            e.toString()
        }
    }

    private fun logout(view: View) {
        view.img_logout.setOnClickListener{
            postViewModel.logOut()
            callBack.showLogin(null,null)
        }
    }


    private fun addComment(view: View) {

        view.img_send.setOnClickListener {
            users.forEach {
                if(mail == it.mail) {
                    this.user = it
                    return@forEach
                }
            }
            val comment = view.edt_comment.text.toString()
            val commentUser = Comment(user,comment)
            val commentAdd = ArrayList<Comment>()
            commentAdd.add(commentUser)
            comments.add(commentUser)
            adapterComment.notifyItemInserted(0)
            postViewModel.commentUpdate(comments,pos)
                adapter.updateComment(posts1.size-1-pos,comments.size, TYPE_COMMENTLAST)
                adapter.updateLastComment(posts1.size-1-pos,commentUser,TYPE_COMMENTLAST)

            view.edt_comment.text = null
            postViewModel.sendNotifyUser(posts[posts1.size-1-pos].user.token!!, TYPE_COMMENT)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu,menu)
    }

    override fun clickLike(likes: ArrayList<String>?, post: Int) {
        postViewModel.likeUpdate(mail,posts1.size - 1- post,likes,posts1).observe(this, Observer {
            adapter.updateLike( post,it,TYPE_CLICK)
        })
//        postViewModel.getallPostChild().observe(this, Observer {
//            adapter.notifyItemChanged(posts1.size-1-post)
//        })

    }

    override fun clickComment( pos: Int) {
        comments = ArrayList()
        if(btSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED){
            btSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        else btSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        postViewModel.getAllComment(posts1.size -1 - pos).observe(this, Observer {
            comments = it
            adapterComment.filterList(comments)
        })

        this.pos = posts1.size -1 - pos
    }


    override fun startVideo(url: String) {
        val dataSourseFactory = DefaultDataSourceFactory(requireContext(),"sample")
        ProgressiveMediaSource.Factory(dataSourseFactory).createMediaSource(Uri.parse(url))?.let {
            videoPlayer.prepare(it)
            videoPlayer.playWhenReady = true
        }
    }

    override fun onResume() {
        super.onResume()
        videoPlayer.playWhenReady = true
        shimmer_container.startShimmerAnimation()
    }

    override fun onPause() {
        super.onPause()
        shimmer_container.stopShimmerAnimation()
    }
    override fun onStop() {
        super.onStop()
        videoPlayer.playWhenReady = false
        if(activity?.isDestroyed!!) videoPlayer.release()
    }
}

