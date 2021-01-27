package com.example.onjobproject.view.fragment


import android.view.View
import android.widget.Toast
import com.example.onjobproject.R
import com.example.onjobproject.callback.CallBack
import com.example.onjobproject.model.User
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.register_fragment.*
import kotlinx.android.synthetic.main.register_fragment.view.*
import javax.inject.Inject

class RegisterFragment() :
    BaseFragment(), View.OnClickListener {
    private lateinit var name: String
    private lateinit var mail: String
    private lateinit var pass: String
    private lateinit var rePass: String
    private lateinit var callBack: CallBack
    val TAG: String = RegisterFragment::class.qualifiedName.toString()


    @Inject
    constructor(callBack: CallBack) : this() {
        this.callBack = callBack
    }

    override fun getLayoutID(): Int {
        return R.layout.register_fragment
    }

    override fun initView(view: View) {
        view.bt_signup.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        name = edt_name.text.toString()
        mail = edt_email.text.toString()
        pass = edt_pass.text.toString()
        rePass = edt_repass.text.toString()
        // vadidate
        when (true) {
            name.isEmpty(), mail.isEmpty(), pass.isEmpty(), rePass.isEmpty() -> Toast.makeText(
                context,
                "điền đủ đê",
                Toast.LENGTH_SHORT
            ).show()
            pass != rePass -> Toast.makeText(context, "Pass k trùng", Toast.LENGTH_SHORT).show()
            else -> {
                checkRS(mail, pass, name)
            }
        }
    }

    private fun checkRS(mail: String, pass: String, name: String) {
        val myObserver: Observer<String> = getObserver()
        postViewModel.getRX(mail, pass, name, 1, requireContext()).subscribe(myObserver)
    }


    private fun getObserver(): Observer<String> {
        return object : Observer<String> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                if (t == "true") {
                    Toast.makeText(context, "Thành công", Toast.LENGTH_SHORT).show()
                    userViewModel.addDatabase(
                        User(name, "https://scr.vn/wp-content/uploads/2020/07/%E1%BA%A2nh-%C4%91%E1%BA%A1i-di%E1%BB%87n-FB-m%E1%BA%B7c-%C4%91%E1%BB%8Bnh-n%E1%BB%AF.jpg", mail, "sadasdasd")
                    )
                    callBack.showLogin(mail, pass)
                } else Toast.makeText(context, "không Thành công vì $t", Toast.LENGTH_SHORT).show()
            }

            override fun onError(e: Throwable) {
            }

        }
    }

}