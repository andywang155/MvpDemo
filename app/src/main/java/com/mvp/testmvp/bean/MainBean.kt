package com.mvp.testmvp.bean

import com.google.gson.annotations.SerializedName

data class MainBean(
    @SerializedName("nameAPI")
    val name: String = "",
    @SerializedName("productList")
    var list: List<ProductList>?
) {
    data class ProductList(
        @SerializedName("productName")
        val productName: String = "",
        @SerializedName("productNum")
        val productNum: Int
    )
}