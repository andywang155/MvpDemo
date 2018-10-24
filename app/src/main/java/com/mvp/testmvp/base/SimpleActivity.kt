package com.mvp.testmvp.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.CheckResult
import com.mvp.testmvp.R
import com.mvp.testmvp.event.DummyEvent
import com.mvp.testmvp.util.StatusBarUtil
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.LifecycleTransformer
import com.trello.rxlifecycle2.RxLifecycle
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.android.RxLifecycleAndroid
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import me.yokeyword.fragmentation.SupportActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


abstract class SimpleActivity : SupportActivity(), LifecycleProvider<ActivityEvent> {

    private val lifecycleSubject = BehaviorSubject.create<ActivityEvent>()
    protected val mActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        setStatusBar()
        App.instance.addActivity(mActivity)
        EventBus.getDefault().register(mActivity)
        initPresenter()
        lifecycleSubject.onNext(ActivityEvent.CREATE)
        initView()
        initData()
    }

    open fun initPresenter() {}

    @CallSuper
    override fun onStart() {
        super.onStart()
        lifecycleSubject.onNext(ActivityEvent.START)
    }

    override fun onResume() {
        super.onResume()
        lifecycleSubject.onNext(ActivityEvent.RESUME)
    }

    override fun onPause() {
        lifecycleSubject.onNext(ActivityEvent.PAUSE)
        super.onPause()
    }

    @CallSuper
    override fun onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP)
        super.onStop()
    }

    override fun onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY)
        super.onDestroy()
        EventBus.getDefault().unregister(mActivity)
        App.instance.removeActivity(mActivity)
    }

    @CheckResult
    override fun lifecycle(): Observable<ActivityEvent> {
        return lifecycleSubject.hide()
    }

    @CheckResult
    override fun <T> bindUntilEvent(event: ActivityEvent): LifecycleTransformer<T> {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event)
    }

    @CheckResult
    override fun <T> bindToLifecycle(): LifecycleTransformer<T> {
        return RxLifecycleAndroid.bindActivity(lifecycleSubject)
    }

    /**
     * 设置状态栏
     */
    protected open fun setStatusBar() {
        StatusBarUtil.setStatusBar(mActivity)
    }

    /**
     * 跳转到某个Activity
     */
    protected fun gotoActivity(mContext: Context, toActivityClass: Class<*>, bundle: Bundle?) {
        val intent = Intent(mContext, toActivityClass)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        mContext.startActivity(intent)
        (mContext as Activity).overridePendingTransition(R.anim.push_right_in, R.anim.not_exit_push_left_out)
    }

    /**
     * 退出Activity
     */
    protected open fun backActivity() {
        finish()
        overridePendingTransition(R.anim.not_exit_push_left_in, R.anim.push_right_out)
    }


    /**
     * 初始化子类布局
     */
    protected abstract fun getLayout(): Int

    /**
     * 初始化子类View
     */
    protected abstract fun initView()

    /**
     * 初始化子类一些数据
     */
    protected abstract fun initData()

    /**
     * 该方法不执行，只是让Event编译通过
     */
    @Subscribe
    fun dummy(event: DummyEvent) {
    }

}