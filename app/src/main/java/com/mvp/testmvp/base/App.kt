package com.mvp.testmvp.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.multidex.MultiDex
import com.mvp.testmvp.log.LogUtil
import com.mvp.testmvp.util.Utils
import kotlin.properties.Delegates


class App : Application(), Application.ActivityLifecycleCallbacks {

    private var resumeCount: Int = 0
    private val mAllActivities: LinkedHashSet<Activity> = LinkedHashSet()

    //当前环境
    var mCurrentHost = HOST.DEV

    // DEV开发,DEV_HTTPS测试https,PRE预发布,PRO上线
    enum class HOST {
        DEV, DEV_HTTPS, PRE, PRO
    }

    companion object {
        var instance: App by Delegates.notNull()
        var isOnResume: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()

        //判断程序是否重复启动
        val isApplicationRepeat = Utils.instance.isApplicationRepeat(this)
        if (isApplicationRepeat) {
            return
        }
        instance = this
        registerActivityLifecycleCallbacks(this)
//        var push_id = JPushInterface.getRegistrationID(instance)
        //初始化Logger的TAG
        LogUtil.init(true)
        // dex突破65535的限制
        MultiDex.install(this)
    }

    /**
     * 保存Activity的引用
     */
    fun addActivity(act: Activity) {
        mAllActivities.add(act)
    }

    /**
     * 清除Activity的引用
     */
    fun removeActivity(act: Activity) {
        mAllActivities.remove(act)
    }

    /**
     * 得到当前栈顶的Activity
     */
    fun getCurrentActivity(): Activity {
        return mAllActivities.last()
    }

    /**
     * 退出App
     */
    fun exitApp() {
        synchronized(mAllActivities) {
            for (act in mAllActivities) {
                act.finish()
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(0)
    }


    override fun onActivityResumed(activity: Activity) {
        isOnResume = true
        resumeCount++
    }

    override fun onActivityPaused(activity: Activity) {
        resumeCount--
        if (resumeCount == 0) {
            isOnResume = false
        }
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }
}