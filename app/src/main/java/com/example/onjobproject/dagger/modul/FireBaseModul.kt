package com.example.onjobproject.dagger.modul

import com.example.onjobproject.model.Comment
import com.example.onjobproject.model.User
import com.example.onjobproject.viewmodel.PostViewModel
import com.example.onjobproject.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FireBaseModul() {

    //Khởi tạo model
    @Provides
    fun providerUser(): User {
        return User()
    }

    @Provides
    fun providerComment(): Comment {
        return Comment()
    }

    @Provides
    fun provideVMD(): PostViewModel {
        return PostViewModel()
    }

    @Provides
    fun provideUVM(): UserViewModel {
        return UserViewModel()
    }

    //khởi tạo database
    @Provides
    fun provideAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideDB(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Singleton
    @Provides
    fun provideMyRef(database: FirebaseDatabase): DatabaseReference {
        return database.reference
    }

    @Singleton
    @Provides
    fun providesStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Singleton
    @Provides
    fun providesStorageRef(storage: FirebaseStorage): StorageReference {
        return storage.reference
    }

}