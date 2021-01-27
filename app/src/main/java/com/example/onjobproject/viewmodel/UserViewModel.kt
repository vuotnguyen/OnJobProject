package com.example.onjobproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.onjobproject.dagger.DaggerComponent
import com.example.onjobproject.dagger.modul.ApiModul
import com.example.onjobproject.dagger.modul.FireBaseModul
import com.example.onjobproject.model.User
import com.google.firebase.database.*
import io.reactivex.Observable
import javax.inject.Inject
import kotlin.collections.ArrayList

class UserViewModel : ViewModel() {
    @Inject
    lateinit var myRef: DatabaseReference
    @Inject
    lateinit var database: FirebaseDatabase
    lateinit var users: ArrayList<User>
    lateinit var usersLiveData: MutableLiveData<ArrayList<User>>
    var keyUser : ArrayList<String>
    init {
        val component = DaggerComponent.builder().fireBaseModul(FireBaseModul()).apiModul(ApiModul()).build()
        component.injectUMD(this)
        keyUser = ArrayList()
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
                    var user = it.getValue(User::class.java)!!
                    val token = it.child("token").value.toString()
                    user.token = token
                    users.add(user)

                    keyUser.add(it.key.toString())
                }

                usersLiveData.postValue(users)
            }
        })
        return usersLiveData
    }
    fun  getAllUser1() : Observable<ArrayList<User>>{
        val myObservable = Observable.create<ArrayList<User>>(){
            myRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    users = ArrayList()

                    snapshot.children.forEach{
                        var user = it.getValue(User::class.java)!!
                        val token = it.child("token").value.toString()
                        user.token = token
                        users.add(user)

                        keyUser.add(it.key.toString())
                    }

                    it.onNext(users)
                }
            })
        }
        return myObservable
    }

    fun updateTokenDevice(mail: String, token: String) {
        var i = 0
        users.forEach {
            if (mail == it.mail) {
                it.token = token
                val update: DatabaseReference = database.getReference("users").child(keyUser[i])
                Log.i("TAG", "updateTokenDevice: "+keyUser[i])
                update.child("token").setValue(token)
                return@forEach
            }
            i++
        }
    }

}