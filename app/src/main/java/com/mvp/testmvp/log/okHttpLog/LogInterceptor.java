package com.mvp.testmvp.log.okHttpLog;


import com.mvp.testmvp.log.LogUtil;

public class LogInterceptor implements HttpLoggingInterceptorM.Logger {

    public static String INTERCEPTOR_TAG_STR = "OkHttp";

    public LogInterceptor() {
    }

    public LogInterceptor(String tag) {
        INTERCEPTOR_TAG_STR = tag;
    }

    @Override
    public void log(String message, @LogUtil.LogType int type) {
        LogUtil.printLog(false, type, INTERCEPTOR_TAG_STR, message);
    }
}
