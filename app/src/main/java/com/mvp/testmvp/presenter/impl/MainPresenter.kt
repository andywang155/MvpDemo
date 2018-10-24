package com.mvp.testmvp.presenter.impl

import com.mvp.testmvp.base.RxPresenter
import com.mvp.testmvp.bean.MainBean
import com.mvp.testmvp.bean.MyHttpResponse
import com.mvp.testmvp.bean.UpgradeBean
import com.mvp.testmvp.http.ApiManager
import com.mvp.testmvp.http.ApiSettings
import com.mvp.testmvp.http.CommonSubscriber
import com.mvp.testmvp.http.RxUtil
import com.mvp.testmvp.presenter.contract.MainContract
import com.mvp.testmvp.util.SignUtils
import okhttp3.RequestBody
import org.json.JSONObject

class MainPresenter : RxPresenter<MainContract.View>(), MainContract.MainPresenter {
    override fun textUrl(name: String, num: Int) {
        val jsonObject = JSONObject()
        jsonObject.put("name", num)
        jsonObject.put("num", num)

        val body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), jsonObject.toString())

        addDisposable(
            ApiManager.instance.text(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<UpgradeBean>>())
                .compose(RxUtil.handleResult())
                .subscribeWith(object : CommonSubscriber<UpgradeBean>(mView, ApiSettings.text) {
                    override fun onNext(data: UpgradeBean?) {
                        mView?.getTextUrlResult(data!!)
                    }
                })
        )
    }
}