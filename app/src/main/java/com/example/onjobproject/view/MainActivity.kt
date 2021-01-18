package com.example.onjobproject.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.onjobproject.callback.CallBack
import com.example.onjobproject.R
import com.example.onjobproject.dagger.Component
import com.example.onjobproject.dagger.DaggerComponent


import com.example.onjobproject.dagger.modul.FireBaseModul
import com.example.onjobproject.dagger.modul.FragmentModul
import com.example.onjobproject.view.fragment.ListPostFragment
import com.example.onjobproject.view.fragment.LoginFragment
import com.example.onjobproject.view.fragment.RegisterFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.iid.FirebaseInstanceId
import javax.inject.Inject

class MainActivity() : AppCompatActivity(), CallBack {
    @Inject lateinit var listPostFragment: ListPostFragment
    @Inject lateinit var registerFragment: RegisterFragment
    @Inject lateinit var loginFragment: LoginFragment
    lateinit var fragmentManager: FragmentManager
    lateinit var transaction :FragmentTransaction


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val component = DaggerComponent.builder().fragmentModul(FragmentModul(this)).build()
        component.inject(this)
        fragmentManager = supportFragmentManager
        if(savedInstanceState == null){
            initVIew()
        }
    }

    private fun initVIew() {
        transaction  = fragmentManager.beginTransaction()
        transaction.replace(R.id.main, loginFragment, LoginFragment::TAG.toString())
//        transaction.addToBackStack(LoginFragment::class.qualifiedName)
        transaction.commitNow()
    }

    override fun showFragment() {
        transaction  = fragmentManager.beginTransaction()
        transaction.replace(R.id.main, registerFragment, RegisterFragment::TAG.toString())
        transaction.addToBackStack(RegisterFragment::class.qualifiedName)
        transaction.commit()
    }

    override fun showLiss(mail: String) {
        listPostFragment = ListPostFragment(mail)
        transaction  = fragmentManager.beginTransaction()
        transaction.replace(R.id.main, listPostFragment, ListPostFragment::TAG.toString())
        transaction.addToBackStack(ListPostFragment::class.qualifiedName)
        transaction.commit()
    }


    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        if(ListPostFragment.btSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED){
            ListPostFragment.btSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

}