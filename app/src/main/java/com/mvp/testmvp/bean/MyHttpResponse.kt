package com.mvp.testmvp.bean

data class MyHttpResponse<T>(val code: Int, val msg: String, val data: T)