package com.example.onjobproject.viewmodel


import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.onjobproject.dagger.DaggerComponent

import com.example.onjobproject.dagger.modul.FireBaseModul
import com.example.onjobproject.model.*
import com.example.onjobproject.view.MainActivity
import com.example.onjobproject.view.RetrofitService
import com.example.onjobproject.view.fragment.ListPostFragment

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PostViewModel() : ViewModel() {

    @Inject
    lateinit var myStorage: FirebaseStorage

    @Inject
    lateinit var storageRef: StorageReference

    @Inject
    lateinit var myRef: DatabaseReference

    @Inject
    lateinit var database: FirebaseDatabase

    @Inject
    lateinit var mAuth: FirebaseAuth
    @Inject
    lateinit var retrofitService: RetrofitService

    private lateinit var rs: MutableLiveData<String>
    private lateinit var posts: ArrayList<Post>
    private lateinit var comments: ArrayList<Comment>
    private lateinit var postLiveData: MutableLiveData<ArrayList<Post>>
    private lateinit var commentLiveData: MutableLiveData<ArrayList<Comment>>
    private lateinit var changeLiveData: MutableLiveData<Post>
    private val likesLiveData = MutableLiveData<ArrayList<String>>()

    init {
        val component = DaggerComponent.builder().fireBaseModul(FireBaseModul()).build()
        component.injectVMD(this)

    }

//    fun getRS(email: String, password: String, name: String?, i: Int, context: Context): LiveData<String> {
//        rs = MutableLiveData()
//        if (i == 0) {
//            singIn(email, password, context)
//        } else {
//            signUp(email, password, name!!, context)
//        }
//        return rs
//    }
    fun getRX(email: String, password: String,name:String?,i: Int,context: Context) : Observable<String>{
        if(i == 0)
        return singIn1(email,password,context)
        else return singUp1(email,password,name,context)
    }

    private fun singUp1(email: String, password: String, name: String?, context: Context): Observable<String> {
        return Observable.create {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(context as Activity) { task ->
                    if (task.isSuccessful) {
                        it.onNext("true")

                    } else {
                        it.onNext(task.exception.toString())
                    }
                }
        }
    }

    private fun singIn1(email: String, password: String, context: Context): Observable<String> {
        return Observable.create {
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(context as Activity) { task ->
                        if (task.isSuccessful) {
                            mAuth.currentUser
                            it.onNext("true")
                        } else {
                            Log.w("TAG", "signInWithEmail:failure", task.exception)
                           it.onNext(task.exception.toString())
                        }
                    }
        }
    }

    fun getPostLivedata(): LiveData<ArrayList<Post>> {
        postLiveData = MutableLiveData()
        getallPost()
        return postLiveData
    }

    private fun getallPost(): MutableLiveData<ArrayList<Post>> {
        postLiveData = MutableLiveData()
        myRef.child("posts").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                posts = ArrayList()
                snapshot.children.forEach {
                    posts.add(0,it.getValue(Post::class.java)!!)
                }
                Log.i("TAG", "onDataChange: " + posts.size)
                postLiveData.postValue(posts)
            }
        })
        return postLiveData
    }
    fun getallPostChild(): MutableLiveData<Post> {
        changeLiveData = MutableLiveData()
        myRef.child("posts").addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i("TAG", "onChildChanged: "+snapshot.value)
                val post: Post? = snapshot.getValue(Post::class.java)
                changeLiveData.postValue(post)
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

        })
        return changeLiveData
    }

//    private fun singIn(email: String, password: String, context: Context) {
//        mAuth.let {
//            mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(context as Activity) { task ->
//                    if (task.isSuccessful) {
//                        rs.postValue("true")
//                        val user = mAuth.currentUser
//                        Log.w("TAG", "signInWithEmail" + task.isSuccessful, task.exception)
//
//                    } else {
//                        Log.w("TAG", "signInWithEmail:failure", task.exception)
//                        rs.postValue(task.exception.toString())
//
//                    }
//                }
//        }
//    }

//    private fun signUp(
//        emai: String,
//        password: String,
//        name: String,
//        context: Context
//    ) {
//        mAuth.let {
//            mAuth.createUserWithEmailAndPassword(emai, password)
//                .addOnCompleteListener(context as Activity) { task ->
//                    if (task.isSuccessful) {
//                        rs.postValue("true")
//
//                    } else {
//                        Log.w("TAG", "createUserWithEmail:failure", task.exception)
//                        rs.postValue(task.exception.toString())
//                    }
//                }
//        }
//    }

    fun likeUpdate(mail: String, key: Int, likes: ArrayList<String>?, posts: List<Post>): LiveData<ArrayList<String>> {

        var boolean = false
        val liket = ArrayList<String>()
        if (likes == null) {
            liket.add(mail)
            listLike(liket, key)
            likesLiveData.postValue(liket)
            return likesLiveData
        } else {
            likes.forEach {
                liket.add(it)
                if (mail == it) {
                    boolean = true
                }
            }
            if (boolean == false) {
                liket.add(mail)
                sendNotifyUser(posts[key].user.token!!, ListPostFragment.TYPE_CLICK)
            } else {
                liket.remove(mail)
            }

            listLike(liket, key)
            likesLiveData.postValue(liket)
            return likesLiveData
        }

    }

    fun sendNotifyUser(token: String, type: Int) {
        if (token != MainActivity.token) {
            var notifyModel: NotifyModel
            if (type == 0) notifyModel = NotifyModel(
                token,
                Notify(
                    "Thông báo ",
                    "${MainActivity.sharePref.getString("name", "")} đã like ảnh bạn "
                )
            )
            else notifyModel = NotifyModel(
                token,
                Notify(
                    "Thông báo ",
                    "${MainActivity.sharePref.getString("name", "")} đã comment bài viết của bạn "
                )
            )

            val userSend: Call<NotifyModel> = retrofitService.sendNotifycation(notifyModel)
            userSend.enqueue(object : Callback<NotifyModel> {
                override fun onFailure(call: Call<NotifyModel>, t: Throwable) {
                }

                override fun onResponse(call: Call<NotifyModel>, response: Response<NotifyModel>) {
                    Log.i("TAG", "onResponse: successfully")
                }
            })
        }

    }


    private fun listLike(list: ArrayList<String>, key: Int) {
        val update: DatabaseReference = database.getReference("posts").child(key.toString())
//        val newLike: HashMap<String, ArrayList<String>> = HashMap()
//        newLike["likes"] = list
//        update.updateChildren(newLike as Map<String, Any>)
        update.child("likes").setValue(list)
    }

    fun commentUpdate(comments: ArrayList<Comment>, pos: Int) {
        val newComments: HashMap<String, ArrayList<Comment>> = HashMap()
        newComments["comments"] = comments
        val update: DatabaseReference = database.getReference("posts").child(pos.toString())
        update.updateChildren(newComments as Map<String, Any>)
    }

    fun getAllComment(posit: Int): LiveData<ArrayList<Comment>> {
        comments = ArrayList()
        commentLiveData = MutableLiveData()
        val ref = database.getReference("posts").child(posit.toString())
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                comments.clear()
                snapshot.child("comments").children.forEach {
                    Log.i("TAG", "onDataChange: " + it)
                    comments.add(it.getValue(Comment::class.java)!!)
                }
                commentLiveData.postValue(comments)
            }
        })
        return commentLiveData
    }

    fun updatePostUser(mail: String, token: String, posts: ArrayList<Post>) {

        var i = 0

        posts.forEach {
            if (mail.equals(it.user.mail)) {
                val ref = database.getReference("posts").child(i.toString())
                it.user.token = token
                val userChange: HashMap<String, User> = HashMap()
                userChange.put("user", it.user)
                ref.updateChildren(userChange as Map<String, Any>)
            }
            i++
        }
    }

    fun logOut() {
        mAuth.signOut()
    }

    fun postDatabase(post: Post) {
        this.posts.size - 1
        myRef.child("posts").child((this.posts.size-1).toString()).setValue(post)

    }

    fun upFileStorage(
        uri: Uri,
        processDialog: ProgressDialog,
        post: Post
    ) {
        val ref: StorageReference = storageRef.child("images/" + UUID.randomUUID().toString())
        ref.putFile(uri).addOnSuccessListener {
            processDialog.dismiss()

        }.addOnFailureListener {
            Log.i("TAG", "upFileStorage: $it")
        }.addOnCompleteListener {
            ref.downloadUrl.addOnSuccessListener {
                post.url = it.toString()
                postDatabase(post)
            }.addOnFailureListener {
                Log.i("TAG", "upFileStorage: "+it)
            }
        }
    }


//    private fun addRealtimeDB() {
//            val user = User("hiền","https://cdn.tgdd.vn/Files/2019/10/09/1206509/google-photos-colorize_800x450.jpg","hien1@gmail.com",null)
//            val user1 = User("vuot","https://img.nhandan.com.vn/Files/Images/2020/07/26/nhat_cay-1595747664059.jpg","vuot@gmail.com")
//            val user2 = User("son  ","https://aphoto.vn/wp-content/uploads/2018/02/anh-dep-chup-dien-thoai.jpg","son@gmail.com")
//
//            val listLike: ArrayList<String> = ArrayList()
//            listLike.add("vuot@gmail.com")
//
//            val comment = Comment(user,"chúng ta của hiện tại")
//            val comment1 = Comment(user1,"chắc ai đó sẽ về")
//            val listComment:ArrayList<Comment> = ArrayList()
//            listComment.add(comment)
//            listComment.add(comment1)
//
//            val post = Post(user, "Thực tập sinh Ominext", "Ominext tuyệt vời","https://baoquocte.vn/stores/news_dataimages/dieulinh/012020/29/15/nhung-buc-anh-dep-tuyet-voi-ve-tinh-ban.jpg",0, "22/3/2020", listLike, listComment, 2)
//            val post1 = Post(user1, "AlarmManager", "Set nextNonWakeup as mNextNonWakeupDeliveryTime=213416437 , orig nextNonWakeup=0","https://baoquocte.vn/stores/news_dataimages/dieulinh/012020/29/15/nhung-buc-anh-dep-tuyet-voi-ve-tinh-ban.jpg",1, "22/3/2020", listLike, listComment, 2)
//            val post2 = Post(user2, "Watchdog", "!@Sync 2681 [2021-01-06 09:10:21.986]","https://baoquocte.vn/stores/news_dataimages/dieulinh/012020/29/15/nhung-buc-anh-dep-tuyet-voi-ve-tinh-ban.jpg",0, "22/3/2020", listLike, listComment, 2)
//            val post3 = Post(user, " E/SPPClientService", "[g] __PingReply__","https://baoquocte.vn/stores/news_dataimages/dieulinh/012020/29/15/nhung-buc-anh-dep-tuyet-voi-ve-tinh-ban.jpg",1, "22/3/2020", listLike, listComment, 2)
//            val post4 = Post(user1, "BatteryExternalStatsWorker", "no controller energy info supplied for bluetooth","https://baoquocte.vn/stores/news_dataimages/dieulinh/012020/29/15/nhung-buc-anh-dep-tuyet-voi-ve-tinh-ban.jpg",0, "22/3/2020", listLike, listComment, 2)
//            val post5 = Post(user2, "E/libpersona", " scanKnoxPersonas","https://baoquocte.vn/stores/news_dataimages/dieulinh/012020/29/15/nhung-buc-anh-dep-tuyet-voi-ve-tinh-ban.jpg",0, "22/3/2020", listLike, listComment, 2)
//            val post6 = Post(user, "E/libpersona", "Couldn't open the File - /data/system/users/0/personalist.xml - No such file or directory","https://baoquocte.vn/stores/news_dataimages/dieulinh/012020/29/15/nhung-buc-anh-dep-tuyet-voi-ve-tinh-ban.jpg",0, "22/3/2020", listLike, listComment, 2)
//            val post7 = Post(user1, "TLC_TZ_ICCC_initialize", " sys.mobicoredaemon.enable is true ","https://baoquocte.vn/stores/news_dataimages/dieulinh/012020/29/15/nhung-buc-anh-dep-tuyet-voi-ve-tinh-ban.jpg",1, "22/3/2020", listLike, listComment, 2)
//            val post8 = Post(user2, "E/TZ_ICCC_device_status_tlc", "called","https://baoquocte.vn/stores/news_dataimages/dieulinh/012020/29/15/nhung-buc-anh-dep-tuyet-voi-ve-tinh-ban.jpg",0, "22/3/2020", listLike, listComment, 2)
//            val post9 = Post(user, "creating buffers", "Copying response buffer","https://baoquocte.vn/stores/news_dataimages/dieulinh/012020/29/15/nhung-buc-anh-dep-tuyet-voi-ve-tinh-ban.jpg",0, "22/3/2020", listLike, listComment, 2)
//            val post10 = Post(user1, "before comm_request", "returning in the end of the method","https://baoquocte.vn/stores/news_dataimages/dieulinh/012020/29/15/nhung-buc-anh-dep-tuyet-voi-ve-tinh-ban.jpg",1, "22/3/2020", listLike, listComment, 2)
//
//
//            val listPost: ArrayList<Post> = ArrayList()
//            listPost.add(post)
//            listPost.add(post1)
//            listPost.add(post2)
//            listPost.add(post3)
//            listPost.add(post4)
//            listPost.add(post5)
//            listPost.add(post6)
//            listPost.add(post7)
//            listPost.add(post8)
//            listPost.add(post9)
//            listPost.add(post10)
//
//
////        userRef = myRef.child("users")
////        newUser = userRef.push()
////        newUser.setValue(user)
//
//            myRef.child("posts").setValue(listPost)
//
//    }
}


