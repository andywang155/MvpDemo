package com.mvp.testmvp.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.json.JSONObject

open class RxPresenter<T : BaseView> : BasePresenter<T> {

    protected var mView: T? = null
    private var mCompositeDisposable: CompositeDisposable? = null

    override fun attachView(view: T) {
        mView = view
    }

    override fun detachView() {
        this.mView = null
        unDisposable()
    }

    protected fun addDisposable(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable?.add(disposable)
    }

    private fun unDisposable() {
        mCompositeDisposable?.clear()
    }

}