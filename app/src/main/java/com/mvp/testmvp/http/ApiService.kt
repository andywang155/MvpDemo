package com.mvp.testmvp.http

import com.mvp.testmvp.bean.MainBean
import com.mvp.testmvp.bean.MyHttpResponse
import com.mvp.testmvp.bean.UpgradeBean
import io.reactivex.Flowable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST(ApiSettings.TEXT_ULR)
    fun textUrl(@Body body: RequestBody): Flowable<MyHttpResponse<MainBean>>
    @POST(ApiSettings.text)
    fun text(@Body body: RequestBody): Flowable<MyHttpResponse<UpgradeBean>>

}