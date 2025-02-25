package com.example.proyecto_final_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class RegistrarProducto : AppCompatActivity() {

    lateinit var btnregresar4: Button
    lateinit var btnAddProduct: Button
    lateinit var editTextCode: EditText
    lateinit var editTextName: EditText
    lateinit var editTextPrice: EditText
    lateinit var editTextStock: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_producto)

        btnregresar4 = findViewById(R.id.btnregresar4)
        btnAddProduct = findViewById(R.id.btnAddProduct)
        editTextCode = findViewById(R.id.editTextCode)
        editTextName = findViewById(R.id.editTextName)
        editTextPrice = findViewById(R.id.editTextPrice)
        editTextStock = findViewById(R.id.editTextStock)

        btnregresar4.setOnClickListener{
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        btnAddProduct.setOnClickListener{
            val code = editTextCode.text.toString()
            val name = editTextName.text.toString()
            val price = editTextPrice.text.toString()
            val stock = editTextStock.text.toString()

            if (code.isEmpty() || name.isEmpty() || price.isEmpty() || stock.isEmpty()) {
                Toast.makeText(this, "Porfavor complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                addProduct(code, name, price.toDouble(), stock.toInt())
            }
        }
    }

    private fun addProduct(code: String, name: String, price: Double, stock: Int) {
        val url = EndPoints.SAVE_PRODUCT
        val requestQueue = Volley.newRequestQueue(this)

        val jsonBody = JSONObject()
        jsonBody.put("code", code)
        jsonBody.put("name", name)
        jsonBody.put("price", price)
        jsonBody.put("stock", stock)

        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                Toast.makeText(this, "Producto agregado correctamente", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(this, "Error: ${error.networkResponse?.statusCode} - ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return jsonBody.toString().toByteArray(Charsets.UTF_8)
            }
        }

        requestQueue.add(request)
    }

}