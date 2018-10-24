package com.mvp.testmvp.base

import android.os.Bundle
import android.view.View

abstract class BaseFragment<in V : BaseView, T : BasePresenter<V>> : SimpleFragment(), BaseView {

    protected abstract var mPresenter: T

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter.attachView(this as V)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun showError() {
    }

    override fun showErrorMsg(msg: String) {
    }

    override fun showEmpty() {
    }

}