package com.mvp.testmvp.http

class ApiException() : RuntimeException() {

    private var mResponseData: String = ""

    constructor(responseData: String) : this() {
        this.mResponseData = responseData
    }

    fun getResponseData(): String = mResponseData

}