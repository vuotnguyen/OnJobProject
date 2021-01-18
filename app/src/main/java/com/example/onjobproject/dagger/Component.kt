package com.example.onjobproject.dagger

import com.example.onjobproject.dagger.modul.ApiModul
import com.example.onjobproject.dagger.modul.FireBaseModul
import com.example.onjobproject.dagger.modul.FragmentModul
import com.example.onjobproject.view.MainActivity
import com.example.onjobproject.view.fragment.BaseFragment
import com.example.onjobproject.viewmodel.PostViewModel
import com.example.onjobproject.viewmodel.UserViewModel

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [FireBaseModul::class,FragmentModul::class,ApiModul::class])
interface Component {
    fun inject(mainActi : MainActivity)
    fun injectVMD(postViewModel: PostViewModel)
    fun injectUMD(userViewModel: UserViewModel)
    fun injectBase(baseFragment: BaseFragment)

}