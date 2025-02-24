package com.example.proyecto_final_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray

class ListarVentas : AppCompatActivity() {

    lateinit var btnregresar3: Button
    lateinit var listViewSales: ListView
    lateinit var salesList: ArrayList<Venta>
    lateinit var adapter: VentaLista

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_ventas)

        btnregresar3 = findViewById(R.id.btnregresar3)
        listViewSales = findViewById(R.id.listViewSales)
        salesList = ArrayList()

        adapter = VentaLista(this, salesList)
        listViewSales.adapter = adapter

        btnregresar3.setOnClickListener{
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        loadSales()
    }

    private fun loadSales() {
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
                            productObject.getInt("stock")
                        )
                        val sale = Venta(
                            jsonObject.getString("dni"),
                            jsonObject.getString("date"),
                            jsonObject.getString("client"),
                            product,
                            jsonObject.getInt("quantity"),
                            jsonObject.getDouble("total"),
                            jsonObject.getBoolean("discount")
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
}