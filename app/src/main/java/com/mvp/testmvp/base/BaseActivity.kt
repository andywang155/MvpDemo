package com.mvp.testmvp.base

abstract class BaseActivity<in V : BaseView, T : BasePresenter<V>> : SimpleActivity(), BaseView {

    protected abstract var mPresenter: T

    override fun initPresenter() {
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