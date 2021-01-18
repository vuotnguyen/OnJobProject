package com.example.onjobproject.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.onjobproject.callback.CallBack
import com.example.onjobproject.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.messaging.FirebaseMessaging

import kotlinx.android.synthetic.main.login_fragment.*

import kotlinx.android.synthetic.main.login_fragment.view.*
import javax.inject.Inject


class LoginFragment(): BaseFragment(), View.OnClickListener{
    companion object{
        lateinit var name: String
        lateinit var token: String
    }
    @Inject lateinit var editor: SharedPreferences.Editor
    @Inject lateinit var sharePref: SharedPreferences
    private var mail: String?=null
    private var pass: String?=null
    lateinit var callBack: CallBack
    val TAG: String = LoginFragment::class.qualifiedName.toString()


    @Inject
    constructor( callBack: CallBack): this(){
        this.callBack = callBack
    }
    override fun getLayoutID(): Int {
        return R.layout.login_fragment
    }

    override fun initView(view: View) {
        sharePref = requireContext().getSharedPreferences("User", Context.MODE_PRIVATE)
        view.bt_signin.setOnClickListener(this)
        view.tv_register.setOnClickListener(this)
        view.edt_email.setText(sharePref.getString("mail",""))
        view.edt_pass.setText(sharePref.getString("pass",""))

        FirebaseMessaging.getInstance().token.addOnCompleteListener{
            if(!it.isSuccessful){
                Toast.makeText(requireContext(),it.exception.toString(),Toast.LENGTH_SHORT).show()
            }else token = it.result.toString()
        }
    }


    override fun onClick(p0: View?) {
        mail = edt_email.text.toString()
        pass = edt_pass.text.toString()
        if(p0 == bt_signin){
            if((mail.isNullOrEmpty() && pass.isNullOrEmpty()) || mail.isNullOrEmpty() || pass.isNullOrEmpty()) {
                Toast.makeText(context, "Nhập vào đê", Toast.LENGTH_SHORT).show()
            }else   checkUser(mail!!, pass!!)
        }
        if(p0 == tv_register){
            callBack.showFragment()
        }
    }

    private fun checkUser(mail:String, pass:String){
        userViewModel.getAllUser().observe(this, Observer {
            it.forEach{
                if(mail.equals(it.mail)){
                    name = it.name
                    it.tokenDivice = token
                    userViewModel.updateTokenDevice(token)
                    Toast.makeText(context, "Thành công", Toast.LENGTH_SHORT).show()
                    callBack.showLiss(mail)
                    if(checkBox.isChecked){
                        savePref()
                    }
                    return@Observer
                }
            }
            Toast.makeText(context, "không Thành công vì" + it , Toast.LENGTH_SHORT).show()
        })
    }


    private fun savePref() {
        editor = sharePref.edit()
        editor.putString("mail",mail)
        editor.putString("pass",pass)
        editor.apply()
    }
}