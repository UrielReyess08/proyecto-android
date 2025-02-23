package com.example.proyecto_final_android

object EndPoint {
    private const val URL_ROOT = "http://localhost:8080/api/v1/"

    const val URL_ADD_PRODUCT = URL_ROOT + "product/save"
    const val URL_GET_PRODUCT = URL_ROOT + "product/find"
    const val URL_GET_ALL_PRODUCTS = URL_ROOT + "product/findAll"
    const val URL_UPDATE_PRODUCT = URL_ROOT + "product/update"
    const val URL_DELETE_PRODUCT = URL_ROOT + "product/delete"
}