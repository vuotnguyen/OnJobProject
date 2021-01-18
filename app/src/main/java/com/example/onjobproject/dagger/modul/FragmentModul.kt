package com.example.onjobproject.dagger.modul

import com.example.onjobproject.callback.CallBack
import com.example.onjobproject.view.fragment.ListPostFragment
import com.example.onjobproject.view.fragment.LoginFragment
import com.example.onjobproject.view.fragment.RegisterFragment
import dagger.Module
import dagger.Provides

@Module
class FragmentModul() {
    lateinit var callBack: CallBack
    constructor(callback: CallBack):this(){
        callBack = callback
    }

    //khởi tạo fragment
    @Provides
    fun providesLogin() : LoginFragment {
        return LoginFragment(callBack)
    }
    @Provides
    fun providesRegis() : RegisterFragment {
        return RegisterFragment(callBack)
    }
    @Provides
    fun providesList(): ListPostFragment {
        return ListPostFragment()
    }
}