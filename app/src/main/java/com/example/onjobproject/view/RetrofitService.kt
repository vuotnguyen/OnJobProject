package com.example.onjobproject.view

import com.example.onjobproject.model.NotifyModel
import com.example.onjobproject.model.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetrofitService {

    @Headers("Authorization: key=AAAAI8z5mhA:APA91bHKWGS4NhnvH2hWkDuzSudd0u6BTCvQeVMO8T2Oop7c9OAo8rPVLlSDvvRk0sS0fMTbu5xG36Ms5a9tAbvNbvE_C7WQ_Y5k_GTnT_se0_CWSiAuAZvA-G6HG0uSXKQNl5A3lHXl","Content-Type:application/json")
    @POST("fcm/send")
    fun sendNotifycation (@Body notifyModel: NotifyModel): Call<NotifyModel>
}