package com.example.onjobproject.dagger.modul

import android.content.Context
import android.content.SharedPreferences
import com.example.onjobproject.model.Comment
import com.example.onjobproject.model.User
import com.example.onjobproject.viewmodel.PostViewModel
import com.example.onjobproject.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FireBaseModul() {
//    lateinit var context: Context
//    constructor(context: Context?):this(){
//        this.context = context!!
//    }

    //Khởi tạo model
    @Provides
    fun providerUser() : User{
        return User()
    }
    @Provides
    fun providerComment() : Comment{
        return Comment()
    }
    @Provides
    fun provideVMD(): PostViewModel{
        return PostViewModel()
    }
    @Provides
    fun provideUVM(): UserViewModel{
        return UserViewModel()
    }

    //khởi tạo database
    @Provides
    fun provideAuth() : FirebaseAuth{
        return FirebaseAuth.getInstance()
    }
    @Singleton
    @Provides
    fun provideDB() : FirebaseDatabase{
        return FirebaseDatabase.getInstance()
    }
    @Singleton
    @Provides
    fun provideMyRef(database: FirebaseDatabase) : DatabaseReference{
        return database.reference
    }


//khởi tạo bộ nhớ
//    @Provides
//    fun provideShare (): SharedPreferences{
//        return context.getSharedPreferences("User",Context.MODE_PRIVATE)
//    }
//    @Provides
//    fun providesEditor(sharedPreferences: SharedPreferences) :SharedPreferences.Editor{
//        return sharedPreferences.edit()
//    }
//    @Provides
//    fun provideFrgManager() : FragmentManager{
//        return context.get
//    }
//
//
//    @Provides
//    fun provideFrgTran(fragmentManager: FragmentManager): FragmentTransaction{
//        return fragmentManager.beginTransaction()
//    }
}