package com.mvp.testmvp.presenter.contract

import com.mvp.testmvp.base.BasePresenter
import com.mvp.testmvp.base.BaseView
import com.mvp.testmvp.bean.UpgradeBean

interface MainContract {
    interface View : BaseView {
       fun getTextUrlResult(data : UpgradeBean)
    }

    interface MainPresenter : BasePresenter<View> {
        fun textUrl(name:String,num:Int)
    }
}