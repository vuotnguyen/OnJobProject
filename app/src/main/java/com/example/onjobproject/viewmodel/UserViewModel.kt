package com.example.onjobproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.onjobproject.dagger.DaggerComponent
import com.example.onjobproject.dagger.modul.ApiModul
import com.example.onjobproject.dagger.modul.FireBaseModul
import com.example.onjobproject.model.Notify
import com.example.onjobproject.model.NotifyModel
import com.example.onjobproject.model.User
import com.example.onjobproject.view.RetrofitService
import com.example.onjobproject.view.fragment.ListPostFragment
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.collections.ArrayList

class UserViewModel (): ViewModel() {
    @Inject
    lateinit var myRef: DatabaseReference
    @Inject
    lateinit var database: FirebaseDatabase
    lateinit var users: ArrayList<User>
    lateinit var usersLiveData: MutableLiveData<ArrayList<User>>
    init {
        val component = DaggerComponent.builder().fireBaseModul(FireBaseModul()).apiModul(ApiModul()).build()
        component.injectUMD(this)
    }

    fun addDatabase(user : User){
        myRef.child("users").push().setValue(user)
    }
    fun  getAllUser() : LiveData<ArrayList<User>>{
        usersLiveData = MutableLiveData()
        myRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                users = ArrayList()
                snapshot.children.forEach{
                    users.add(it.getValue(User::class.java)!!)
                }

                usersLiveData.postValue(users)
            }
        })
        return usersLiveData
    }
    fun updateTokenDevice(token: String){

    }
}