package com.example.onjobproject.view

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.onjobproject.callback.CallBack
import com.example.onjobproject.R
import com.example.onjobproject.dagger.DaggerComponent
import com.example.onjobproject.dagger.modul.FireBaseModul


import com.example.onjobproject.dagger.modul.FragmentModul
import com.example.onjobproject.view.fragment.ListPostFragment
import com.example.onjobproject.view.fragment.LoginFragment
import com.example.onjobproject.view.fragment.RegisterFragment
import com.example.onjobproject.viewmodel.PostViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.bottom_sheet_comment.*
import javax.inject.Inject

class MainActivity() : AppCompatActivity(), CallBack {
    @Inject lateinit var listPostFragment: ListPostFragment
    @Inject lateinit var registerFragment: RegisterFragment
    @Inject lateinit var loginFragment: LoginFragment
    @Inject lateinit var postViewModel: PostViewModel
    lateinit var fragmentManager: FragmentManager
    lateinit var transaction :FragmentTransaction
    companion object {
        var token: String = ""
        lateinit var editor: SharedPreferences.Editor
        lateinit var sharePref: SharedPreferences

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val component = DaggerComponent.builder()
            .fragmentModul(FragmentModul(this))
            .fireBaseModul(FireBaseModul()).build()
        component.inject(this)

        fragmentManager = supportFragmentManager

        FirebaseMessaging.getInstance().token.addOnCompleteListener{
            if(!it.isSuccessful){
                Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
            }else token = it.result.toString()
        }
        if (savedInstanceState == null){
            initVIew()
        }
    }

    override fun showLogin(mail: String?, pass: String?) {
        transaction  = fragmentManager.beginTransaction()
        transaction.replace(R.id.main, loginFragment, LoginFragment::TAG.toString())
        transaction.commit()
    }

    override fun back() {
        super.onBackPressed()
    }

    private fun initVIew() {

        sharePref = this.getSharedPreferences("CurrentUserName", Context.MODE_PRIVATE)
        sharePref = this.getSharedPreferences("CurrentUserAvartar", Context.MODE_PRIVATE)
        sharePref = this.getSharedPreferences("Token", Context.MODE_PRIVATE)
        editor = sharePref.edit()
        if(postViewModel.mAuth.currentUser == null){
            transaction  = fragmentManager.beginTransaction()
            transaction.replace(R.id.main, loginFragment, LoginFragment::TAG.toString())
            transaction.commitNow()
        }else showLiss(postViewModel.mAuth.currentUser!!.email.toString(), token,false)

    }

    override fun showFragment() {
        transaction  = fragmentManager.beginTransaction()
        transaction.replace(R.id.main, registerFragment, RegisterFragment::TAG.toString())
        transaction.addToBackStack(RegisterFragment::class.qualifiedName)
        transaction.commit()
    }

    override fun showLiss(
        mail: String,
        token: String?,
        checkTokenChang: Boolean
    ) {
        listPostFragment = ListPostFragment(mail, token.toString(),checkTokenChang,this)
        transaction  = fragmentManager.beginTransaction()
        transaction.replace(R.id.main, listPostFragment, ListPostFragment::TAG.toString())
//        transaction.addToBackStack(ListPostFragment::class.qualifiedName)
        transaction.commitNow()
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        if(ListPostFragment.btSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED){
            ListPostFragment.btSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }
}