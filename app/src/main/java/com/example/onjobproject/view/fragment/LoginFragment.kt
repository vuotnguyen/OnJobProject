package com.example.onjobproject.view.fragment

import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
//import androidx.lifecycle.Observer

import com.example.onjobproject.R
import com.example.onjobproject.callback.CallBack
import com.example.onjobproject.model.User
import com.example.onjobproject.view.MainActivity
import com.example.onjobproject.view.MainActivity.Companion.token
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.android.synthetic.main.login_fragment.view.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.text.StringBuilder


class LoginFragment() : BaseFragment(), View.OnClickListener {
    companion object {
        lateinit var name: String
    }
    private var mail: String? = null
    private var pass: String? = null
    private lateinit var callBack: CallBack
    val TAG: String = LoginFragment::class.qualifiedName.toString()
    var checkTokenChang = false
    lateinit var disposable: Disposable
    @Inject
    constructor(callBack: CallBack) : this() {
        this.callBack = callBack
    }

    override fun getLayoutID(): Int {
        return R.layout.login_fragment
    }

    override fun initView(view: View) {
        view.bt_signin.setOnClickListener(this)
        view.tv_register.setOnClickListener(this)
        checkEmailVSPass(view)
    }

    private fun checkEmailVSPass(view: View) {

        RxTextView.afterTextChangeEvents(view.edt_email)
            .skipInitialValue()
            .map {
                    view.tip_mail.error = null
                    it.view().text.toString()
            }
            .debounce(0, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .compose(lengthGreaterThanSix)
            .compose(verifyEmailPattern)
            .compose(retryWhenError {
                view.tip_mail.error = it.message
            })
            .subscribe()

        RxTextView.afterTextChangeEvents(view.edt_pass)
            .skipInitialValue()
            .map {
                view.tip_pass.error = null
                it.view().text.toString()
            }
            .debounce(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
            .compose(lengthGreaterThanSix)
            .compose(retryWhenError {
                view.tip_pass.error = it.message
            })
            .subscribe()
    }
    private inline fun retryWhenError(crossinline onError: (ex: Throwable) -> Unit): ObservableTransformer<String, String> =
        ObservableTransformer { observable ->
            observable.retryWhen { errors ->
                errors.flatMap {
                    onError(it)
                    Observable.just("")
                }
            }
        }
    private val lengthGreaterThanSix = ObservableTransformer<String, String> { observable ->
        observable.flatMap {
            Observable.just(it).map { it.trim() } // - abcdefg - |
                .filter { it.length >= 6 }
                .singleOrError()
                .onErrorResumeNext {
                    if (it is NoSuchElementException) {
                        Single.error(Exception("Length should be greater than 6"))
                    } else {
                        Single.error(it)
                    }
                }
                .toObservable()
        }
    }
    private val verifyEmailPattern = ObservableTransformer<String, String> { observable ->
        observable.map { it.trim() }
            .filter {
                Patterns.EMAIL_ADDRESS.matcher(it).matches()
            }
            .singleOrError()
            .onErrorResumeNext {
                if (it is NoSuchElementException) {
                    Single.error(Exception("Email not valid"))
                } else {
                    Single.error(it)
                }
            }
            .toObservable()
    }

    override fun onClick(p0: View?) {
        mail = edt_email.text.toString()
        pass = edt_pass.text.toString()
        if (p0 == bt_signin) {
            if ((mail.isNullOrEmpty() && pass.isNullOrEmpty()) || mail.isNullOrEmpty() || pass.isNullOrEmpty()) {
                Toast.makeText(context, "Nhập vào đê", Toast.LENGTH_SHORT).show()
            } else {
                checkRX(mail!!, pass!!)
            }
        }
        if (p0 == tv_register) {
            callBack.showFragment()
        }
    }

    private fun checkRX(mail: String,pass: String){
        val myObserver : Observer<String> = getObserver()
        postViewModel.getRX(mail,pass,null,0,requireContext()).subscribe(myObserver)
    }

    private fun getObserver(): Observer<String> {
        return object : Observer<String>{
            override fun onComplete() {
                Log.i("TAG", "onComplete: ")
            }

            override fun onSubscribe(d: Disposable) {
                Log.i("TAG", "onSubscribe: ")
            }

            override fun onNext(t: String) {
                checkUser(t)
            }

            override fun onError(e: Throwable) {

            }

        }
    }

    private fun checkUser(t: String) {
            if (t == "true") {
                val myObserAllUser : Observer<ArrayList<User>> = getObserverAllUser()
                userViewModel.getAllUser1().subscribe(myObserAllUser)
            } else Toast.makeText(context, "không Thành công vì $t", Toast.LENGTH_SHORT).show()
    }

    private fun getObserverAllUser(): Observer<ArrayList<User>> {
        return object : Observer<ArrayList<User>>{
            override fun onComplete() {
            }
            override fun onSubscribe(d: Disposable) {
                disposable = d
            }
            override fun onNext(t: ArrayList<User>) {
                t.forEach {
                        if (mail == it.mail) {
                            name = it.name
                            MainActivity.editor.putString("avatar", it.avatar)
                            MainActivity.editor.putString("token", token)
                            MainActivity.editor.putString("name", name)
                            MainActivity.editor.apply()
                            if (!token.equals(it.token)) {
                                userViewModel.updateTokenDevice(mail!!, token)
                                checkTokenChang = true
                            }

                            Toast.makeText(context, "Thành công", Toast.LENGTH_SHORT).show()

                            callBack.showLiss(mail!!, token, checkTokenChang)

                        }
                }
            }
            override fun onError(e: Throwable) {
            }
        }
    }

    override fun onPause() {
        super.onPause()
        edt_email.setText("")
        edt_pass.setText("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.dispose()
    }

}

