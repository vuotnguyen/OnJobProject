package com.example.onjobproject.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.onjobproject.dagger.DaggerComponent
import com.example.onjobproject.dagger.modul.FireBaseModul
import com.example.onjobproject.viewmodel.PostViewModel
import com.example.onjobproject.viewmodel.UserViewModel
import javax.inject.Inject

abstract class BaseFragment : Fragment()  {
    @Inject
    lateinit var postViewModel: PostViewModel
    @Inject
    lateinit var userViewModel: UserViewModel


    init {
        val component = DaggerComponent.builder().fireBaseModul(FireBaseModul()).build()
        component.injectBase(this)
    }
    abstract fun getLayoutID(): Int
    abstract fun initView(view: View)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val  view = inflater.inflate(getLayoutID(),container,false)
        initView(view);
        return view
    }
}