package com.example.proyecto_final_android

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray

class ListarVentas : AppCompatActivity() {

    lateinit var btnregresar3: Button
    lateinit var btnBuscar1: Button
    lateinit var editTextListSaleCode: EditText
    lateinit var listViewSales: ListView
    lateinit var salesList: ArrayList<Venta>
    lateinit var adapter: VentaLista


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_ventas)

        editTextListSaleCode = findViewById(R.id.editTextListSaleCode)
        btnBuscar1 = findViewById(R.id.btnBuscar1)
        btnregresar3 = findViewById(R.id.btnregresar3)
        listViewSales = findViewById(R.id.listViewSales)
        salesList = ArrayList()

        adapter = VentaLista(this, salesList)
        listViewSales.adapter = adapter

        btnBuscar1.setOnClickListener{
            val code = editTextListSaleCode.text.toString()
            if (code.isNotEmpty()) {
                buscarVentaporCodigo(code)
            } else {
                Toast.makeText(this, "Por favor ingrese un codigo", Toast.LENGTH_SHORT).show()
            }
        }

        btnregresar3.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        cargarVenta()
    }

    private fun cargarVenta() {
        val request = StringRequest(
            Request.Method.GET, EndPoints.GET_ALL_SALES,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    salesList.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val productObject = jsonObject.getJSONObject("product")
                        val product = Producto(
                            productObject.getString("code"),
                            productObject.getString("name"),
                            productObject.getDouble("price"),
                            productObject.getInt("stock"),
                            productObject.getString("size")
                        )
                        val sale = Venta(
                            jsonObject.getString("numberSale"),
                            jsonObject.getString("dni"),
                            jsonObject.getString("date"),
                            jsonObject.getString("client"),
                            product,
                            jsonObject.getInt("quantity"),
                            jsonObject.getDouble("total"),
                            jsonObject.getDouble("discount")
                        )
                        salesList.add(sale)
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(this, "Error: ${error.networkResponse?.statusCode} - ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }
    private fun buscarVentaporCodigo(code: String) {
        val url = "${EndPoints.GET_NUMBER_SALES}/$code"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    salesList.clear()

                    val productObject = response.getJSONObject("product")
                    val product = Producto(
                        productObject.getString("code"),
                        productObject.getString("name"),
                        productObject.getDouble("price"),
                        productObject.getInt("stock"),
                        productObject.getString("size")
                    )
                    val sale = Venta(
                        response.getString("numberSale"),
                        response.getString("dni"),
                        response.getString("date"),
                        response.getString("client"),
                        product,
                        response.getInt("quantity"),
                        response.getDouble("total"),
                        response.getDouble("discount")
                    )
                    salesList.add(sale)

                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al procesar datos", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            },
            { error ->

                if (error.networkResponse?.statusCode == 404) {
                    Toast.makeText(this, "Venta no encontrada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error: ${error.networkResponse?.statusCode} - ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
        )

        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }


}