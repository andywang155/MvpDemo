package com.mvp.testmvp.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import com.mvp.testmvp.base.App
import com.mvp.testmvp.http.ApiManager
import org.jetbrains.anko.doAsync
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class Utils private constructor() {

    companion object {
        //使用懒加载，LazyThreadSafetyMode.SYNCHRONIZED-->线程安全的
        val instance: Utils by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Utils() }
    }

    /**
     * 判断程序是否重复启动
     */
    fun isApplicationRepeat(applicationContext: Context): Boolean {

        val pid = android.os.Process.myPid()
        var processName: String? = null
        val am = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val l = am.runningAppProcesses
        val i = l.iterator()
        while (i.hasNext()) {
            val info = i.next()
            try {
                if (info.pid == pid) {
                    processName = info.processName
                }
            } catch (e: Exception) {
            }

        }
        return processName == null || !processName.equals(applicationContext.packageName, ignoreCase = true)
    }

    /**
     * 判断网络是否好用
     */
    fun isNetworkConnected(): Boolean {
        val connectivityManager = App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null
    }

    /**
     * 得到okhttp 缓存目录
     * /Android/data/package-name/cache/okhttp
     */
    fun getOKHttpCachePath(): String = App.instance.externalCacheDir.canonicalPath + File.separator + "okhttp"

    fun getRootCachePath(): String = App.instance.externalCacheDir.canonicalPath

    fun getImageCacheDirPath(): String {
        val path = getRootCachePath() + File.separator + "image"
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.canonicalPath
    }

    /**
     * 得到crash log目录
     * /Android/data/package-name/files/log/crashLog
     */
    fun getCrashLogPath(): String {
        val path = App.instance.getExternalFilesDir("log").canonicalPath + File.separator + "crashLog"
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.canonicalPath
    }

    /**
     * 得到http log目录
     * /Android/data/package-name/files/log/httpLog
     */
    fun getHttpLogPath(): String {
        val path = App.instance.getExternalFilesDir("log").canonicalPath + File.separator + "httpLog"
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.canonicalPath
    }

    /**
     * 下载更新apk包地址
     * /Android/data/package-name/files/apk
     */
    fun getDownLoadAppPath(): String {
        val path = App.instance.getExternalFilesDir("apk").canonicalPath
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.canonicalPath
    }


    /**
     * 写入log到文本文件中
     */
    fun writerLog(log: String) {

        doAsync {
            try {
                val path = getHttpLogPath()
                val file = File(path, "http.txt")
                if (!file.exists()) {
                    file.createNewFile()
                } else {
                    //文件超过100kb就清空重新写
                    val length = file.length()
                    val kb = length / 1024
                    if (kb > 100) {
                        val fileWriter = FileWriter(file)
                        fileWriter.write("")
                        fileWriter.flush()
                        fileWriter.close()
                    }
                }
                val out = FileWriter(file, true)
                val bw = BufferedWriter(out)
                bw.newLine()
                bw.write(log)
                bw.flush()
                bw.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    /**
     * 获取当前应用的版本号
     */
    fun getVersion(context: Context): String {
        try {
            val manager = context.packageManager
            val info = manager.getPackageInfo(context.packageName, 0)
            return info.versionName
        } catch (e: Exception) {
            e.printStackTrace()
            return "1.0.0"
        }
    }

    fun downloadAPP(context: Context, url: String) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val content_url = Uri.parse(url)
        intent.data = content_url
        intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity")
        context.startActivity(intent)
    }

    fun installApk(activity: Activity, targetFilePath: String) {
        val apkFile = File(targetFilePath)
        if (!apkFile.exists()) {
            return
        }
        val intent = Intent(Intent.ACTION_VIEW)
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            val apkUri = FileProvider.getUriForFile(activity, activity.packageName + ".fileprovider", apkFile)
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
        }
        activity.startActivity(intent)
    }

}