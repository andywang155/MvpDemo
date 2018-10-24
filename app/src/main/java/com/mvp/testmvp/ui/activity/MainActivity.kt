package com.mvp.testmvp.ui.activity

import android.util.Log
import com.mvp.testmvp.R
import com.mvp.testmvp.base.BaseActivity
import com.mvp.testmvp.bean.MainBean
import com.mvp.testmvp.bean.UpgradeBean
import com.mvp.testmvp.presenter.contract.MainContract
import com.mvp.testmvp.presenter.impl.MainPresenter

class MainActivity : BaseActivity<MainContract.View, MainContract.MainPresenter>(), MainContract.View {
    override var mPresenter: MainContract.MainPresenter = MainPresenter()

    override fun getLayout(): Int = R.layout.activity_main

    override fun initView() {
        mPresenter.textUrl("baidu", 2)
    }

    override fun initData() {

    }

    override fun getTextUrlResult(data: UpgradeBean) {
        //获得请求数据
    }

}
