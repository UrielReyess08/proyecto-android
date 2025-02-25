package com.example.proyecto_final_android

object EndPoints {
    private const val URL_LOGIN = "http://172.19.96.1:8080/api/v1"
    private const val URL_VENTA = "http://172.19.96.1:8080/api/v3"
    private const val URL_ROOT = "http://172.19.96.1:8080/api/v2"

    const val GET_ALL_PRODUCTS = "$URL_ROOT/allProducts"
    const val SAVE_PRODUCT = "$URL_ROOT/save"

    const val GET_CODE_PRODUCTS = "$URL_ROOT/code"
    const val GET_USER_LOGIN = "$URL_LOGIN/login"

    const val GET_ALL_SALES = "$URL_VENTA/allSales"
    const val GET_NUMBER_SALES = "$URL_VENTA"
    const val SAVE_SALE = "$URL_VENTA/save"

}