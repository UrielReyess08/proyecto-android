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
import org.json.JSONArray

class ListarProductos : AppCompatActivity() {

    lateinit var btnRegresar: Button
    lateinit var btnBuscar: Button
    lateinit var txtCodigoProducto: EditText
    lateinit var listaProductos: ListView
    lateinit var productos: ArrayList<Producto>
    lateinit var adaptador: ProductoLista

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_productos)

        btnRegresar = findViewById(R.id.btnregresar2)
        listaProductos = findViewById(R.id.listaProductos)
        productos = ArrayList()
        btnBuscar = findViewById(R.id.btnBuscar)
        txtCodigoProducto = findViewById(R.id.txtCodigoProducto)

        adaptador = ProductoLista(this, productos)
        listaProductos.adapter = adaptador

        btnBuscar.setOnClickListener {
            val codigo = txtCodigoProducto.text.toString()
            if (codigo.isNotEmpty()) {
                buscarProductoPorCodigo(codigo)
            } else {
                Toast.makeText(this, "Por favor ingrese un código", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegresar.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        cargarProductos()
    }

    private fun cargarProductos() {
        val request = JsonArrayRequest(
            Request.Method.GET, EndPoints.GET_ALL_PRODUCTS, null,
            { response: JSONArray ->
                productos.clear()
                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)
                    if (jsonObject.getBoolean("status")) {
                        val producto = Producto(
                            jsonObject.getString("code"),
                            jsonObject.getString("name"),
                            jsonObject.getDouble("price"),
                            jsonObject.getInt("stock"),
                            jsonObject.getString("size")
                        )
                        productos.add(producto)
                    }
                }
                adaptador.notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(this, "Error: ${error.networkResponse?.statusCode} - ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun buscarProductoPorCodigo(codigo: String) {
        val url = "${EndPoints.GET_CODE_PRODUCTS}/$codigo"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response: JSONArray ->
                productos.clear()
                for (i in 0 until response.length()) {
                    val productObject = response.getJSONObject(i)
                    val producto = Producto(
                        productObject.getString("code"),
                        productObject.getString("name"),
                        productObject.getDouble("price"),
                        productObject.getInt("stock"),
                        productObject.getString("size")
                    )
                    productos.add(producto)
                }
                adaptador.notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(this, "Error: ${error.networkResponse?.statusCode} - ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }
}