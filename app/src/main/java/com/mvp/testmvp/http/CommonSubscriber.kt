package com.mvp.testmvp.http

import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.mvp.testmvp.base.App
import com.mvp.testmvp.base.BaseView
import io.reactivex.subscribers.ResourceSubscriber
import org.jetbrains.anko.getStackTraceString
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class CommonSubscriber<T>(view: BaseView?,  url: String = "") : ResourceSubscriber<T>() {

    private var mErrorMsg: String = ""
    private var mView: BaseView? = view
    private var mUrl = url    //请求的url

    override fun onComplete() {
    }

    override fun onError(e: Throwable?) {
        if (mView == null) {
            return
        }
        var responseData: String
        var errorTip: String

        if (!TextUtils.isEmpty(mErrorMsg)) {
            errorTip = mErrorMsg
            mView?.showErrorMsg(mErrorMsg)
        } else if (e is ApiException) {
            try {
                responseData = e.getResponseData()
                val json = JSONObject(responseData)
                errorTip = json.optString("msg")
                val code = json.optInt("code")
                when (code) {
                    ApiCode.TOKEN_ERROR -> {
                        errorTip = ""
                       //todo Token失效或者错误
                    }
                    ApiCode.UPDATE -> {
                      //todo 版本更新
                        errorTip = ""
                    }
                    ApiCode.ERROR -> {
                       //todo 系统维护
                    }
                }
            } catch (e: Exception) {
                errorTip = "服务器数据错误"
                e.printStackTrace()
            }

            mView?.showErrorMsg(e.toString())
        } else if (e is HttpException || e is ConnectException || e is UnknownHostException || e is SocketTimeoutException || e is NoRouteToHostException) {
            e.printStackTrace()
            errorTip = "网络不给力"
            mView?.showErrorMsg(errorTip)

        } else {
            errorTip = "未知错误ヽ(≧Д≦)ノ"
            mView?.showErrorMsg(errorTip)
        }

        if (!TextUtils.isEmpty(errorTip)) {
            Toast.makeText(App.instance, errorTip, Toast.LENGTH_LONG).show()
        }
        mView?.showError()
    }
}