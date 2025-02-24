package com.example.proyecto_final_android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class ListarProductos : AppCompatActivity() {

    lateinit var btnregresar2: Button
    lateinit var btnSearch: Button
    lateinit var editTextProductCode: EditText
    lateinit var listViewProducts: ListView
    lateinit var productList: ArrayList<Producto>
    lateinit var adapter: ProductoLista

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_productos)

        btnregresar2 = findViewById(R.id.btnregresar2)
        listViewProducts = findViewById(R.id.listViewProducts)
        productList = ArrayList()
        btnSearch = findViewById(R.id.btnSearch)
        editTextProductCode = findViewById(R.id.editTextProductCode)

        adapter = ProductoLista(this, productList)
        listViewProducts.adapter = adapter


        btnSearch.setOnClickListener{
            val code = editTextProductCode.text.toString()
            if (code.isNotEmpty()) {
                searchProductByCode(code)
            } else {
                Toast.makeText(this, "Por favor ingrese un codigo", Toast.LENGTH_SHORT).show()
            }
        }

        btnregresar2.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        loadProducts()
    }

    private fun loadProducts() {
        val request = JsonArrayRequest(
            Request.Method.GET, EndPoints.GET_ALL_PRODUCTS, null,
            { response: JSONArray ->
                productList.clear()
                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)
                    val product = Producto(
                        jsonObject.getString("code"),
                        jsonObject.getString("name"),
                        jsonObject.getDouble("price"),
                        jsonObject.getInt("stock")
                    )
                    productList.add(product)
                }
                adapter.notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(this, "Error: ${error.networkResponse?.statusCode} - ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun searchProductByCode(code: String) {
        val url = "${EndPoints.GET_CODE_PRODUCTS}/$code"  // URL corregida

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response: JSONArray ->
                productList.clear() // Limpiar lista antes de agregar nuevos productos
                for (i in 0 until response.length()) {
                    val productObject = response.getJSONObject(i)
                    val product = Producto(
                        productObject.getString("code"),
                        productObject.getString("name"),
                        productObject.getDouble("price"),
                        productObject.getInt("stock")
                    )
                    productList.add(product)
                }
                adapter.notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(this, "Error: ${error.networkResponse?.statusCode} - ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }
}