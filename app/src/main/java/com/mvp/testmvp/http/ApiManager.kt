package com.mvp.testmvp.http

import com.mvp.testmvp.base.App
import com.mvp.testmvp.bean.MainBean
import com.mvp.testmvp.bean.MyHttpResponse
import com.mvp.testmvp.bean.UpgradeBean
import com.mvp.testmvp.log.okHttpLog.HttpLoggingInterceptorM
import com.mvp.testmvp.log.okHttpLog.LogInterceptor
import com.mvp.testmvp.util.Utils
import io.reactivex.Flowable
import okhttp3.*
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.File
import java.net.Proxy
import java.util.concurrent.TimeUnit

class ApiManager private constructor(){
    companion object {

        //使用懒加载，LazyThreadSafetyMode.SYNCHRONIZED-->线程安全的
        val instance: ApiManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ApiManager() }
        //测试
        const val HOST_DEV: String = "http://xxxxxx/"
        //预发布
        const val HOST_PRE: String = ""
        //正式
        const val HOST_PRO: String = ""
    }

    val HTTP_LOG_TAG: String = "http"
    var HOST = ""

    private lateinit var mApiService: ApiService

    init {
        val retrofit = initRetrofit()
        initServices(retrofit)
    }

    private fun initRetrofit(): Retrofit {
        val builder = OkHttpClient.Builder()
        //打印日志 不区分是否是debug模式
        val interceptor = HttpLoggingInterceptorM(LogInterceptor(HTTP_LOG_TAG))
        interceptor.level = HttpLoggingInterceptorM.Level.BODY
        builder.addInterceptor(interceptor)

        val cachePath = Utils.instance.getOKHttpCachePath()
        val cacheFile = File(cachePath)
        val cache = Cache(cacheFile, (1024 * 1024 * 50).toLong())
        val cacheInterceptor = Interceptor { chain ->
            var request = chain.request()
            if (!Utils.instance.isNetworkConnected()) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build()
            }
            val response = chain.proceed(request)
            if (Utils.instance.isNetworkConnected()) {
                val maxAge = 0
                // 有网络时, 不缓存, 最大保存时长为0
                response.newBuilder()
                        .header("Cache-Control", "public, max-age=$maxAge")
                        .removeHeader("Pragma")
                        .build()
            } else {
                // 无网络时，设置超时为4周
                val maxStale = 60 * 60 * 24 * 28
                response.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                        .removeHeader("Pragma")
                        .build()
            }
        }
        builder.addNetworkInterceptor(cacheInterceptor)
        builder.addInterceptor(cacheInterceptor)
        //设置缓存
        builder.cache(cache)
        //设置超时
        builder.connectTimeout(10, TimeUnit.SECONDS)
        builder.readTimeout(20, TimeUnit.SECONDS)
        builder.writeTimeout(20, TimeUnit.SECONDS)
        //错误重连
        builder.retryOnConnectionFailure(true)
        //不设置代理
        builder.proxy(Proxy.NO_PROXY)
        val okHttpClient = builder.build()

        when (App.instance.mCurrentHost) {
            App.HOST.DEV -> HOST = HOST_DEV
            App.HOST.PRE -> HOST = HOST_PRE
            App.HOST.PRO -> HOST = HOST_PRO
        }

        return Retrofit.Builder().baseUrl(HOST)
                .client(okHttpClient)
                .addConverterFactory(createGsonConverter())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    private fun initServices(retrofit: Retrofit) {
        mApiService = retrofit.create(ApiService::class.java)
    }

    private fun createGsonConverter(): Converter.Factory {
        return CheckGsonConverterFactory.create()
    }

    fun textUrl(body: RequestBody): Flowable<MyHttpResponse<MainBean>> = mApiService.textUrl(body)
    fun text(body: RequestBody): Flowable<MyHttpResponse<UpgradeBean>> = mApiService.text(body)

}