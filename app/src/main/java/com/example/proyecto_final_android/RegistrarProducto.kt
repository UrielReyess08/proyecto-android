package com.example.proyecto_final_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class RegistrarProducto : AppCompatActivity() {

    lateinit var btnregresar4: Button
    lateinit var btnChangeStatus: Button
    lateinit var btnAddProduct: Button
    lateinit var btnSearchProduct: Button
    lateinit var btnUpdateProduct: Button
    lateinit var btnClearFields: Button
    lateinit var editTextCode: EditText
    lateinit var editTextName: EditText
    lateinit var editTextPrice: EditText
    lateinit var editTextStock: EditText
    lateinit var radioGroupSize: RadioGroup
    var productId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_producto)

        btnregresar4 = findViewById(R.id.btnregresar4)
        btnAddProduct = findViewById(R.id.btnAddProduct)
        btnSearchProduct = findViewById(R.id.btnSearchProduct)
        btnUpdateProduct = findViewById(R.id.btnUpdateProduct)
        btnChangeStatus = findViewById(R.id.btnChangeStatus)
        btnClearFields = findViewById(R.id.btnClearFields)
        editTextCode = findViewById(R.id.editTextCode)
        editTextName = findViewById(R.id.editTextName)
        editTextPrice = findViewById(R.id.editTextPrice)
        editTextStock = findViewById(R.id.editTextStock)
        radioGroupSize = findViewById(R.id.radioGroupSize)

        btnregresar4.setOnClickListener{
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        btnAddProduct.setOnClickListener{
            val code = editTextCode.text.toString()
            val name = editTextName.text.toString()
            val price = editTextPrice.text.toString()
            val stock = editTextStock.text.toString()
            val selectedSizeId = radioGroupSize.checkedRadioButtonId

            if (code.isEmpty() || name.isEmpty() || price.isEmpty() || stock.isEmpty() || selectedSizeId == -1) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedSize = findViewById<RadioButton>(selectedSizeId).text.toString()
            addProduct(code, name, price.toDouble(), stock.toInt(), selectedSize)
        }

        btnClearFields.setOnClickListener {
            clearFields()
        }

        btnChangeStatus.setOnClickListener {
            if (productId != null) {
                changeProductStatus(productId!!)
            } else {
                Toast.makeText(this, "Por favor busque un producto primero", Toast.LENGTH_SHORT).show()
            }
        }

        btnUpdateProduct.setOnClickListener {
            val code = editTextCode.text.toString()
            val name = editTextName.text.toString()
            val price = editTextPrice.text.toString()
            val stock = editTextStock.text.toString()
            val selectedSizeId = radioGroupSize.checkedRadioButtonId

            if (productId == null || code.isEmpty() || name.isEmpty() || price.isEmpty() || stock.isEmpty() || selectedSizeId == -1) {
                Toast.makeText(this, "Por favor complete todos los campos y busque un producto", Toast.LENGTH_SHORT).show()
               return@setOnClickListener
            }

            val selectedSize = findViewById<RadioButton>(selectedSizeId).text.toString()
            updateProduct(productId!!, code, name, price.toDouble(), stock.toInt(), selectedSize)
        }

        btnSearchProduct.setOnClickListener {
            val code = editTextCode.text.toString()
            if (code.isNotEmpty()) {
                searchProductByCode(code)
            } else {
                Toast.makeText(this, "Por favor ingrese un código", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun addProduct(code: String, name: String, price: Double, stock: Int, size: String) {
        val url = EndPoints.SAVE_PRODUCT
        val requestQueue = Volley.newRequestQueue(this)

        val jsonBody = JSONObject()
        jsonBody.put("code", code)
        jsonBody.put("name", name)
        jsonBody.put("price", price)
        jsonBody.put("stock", stock)
        jsonBody.put("size", size)

        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                Toast.makeText(this, "Producto agregado correctamente", Toast.LENGTH_SHORT).show()
            },
            { error ->
                if (error.networkResponse?.statusCode == 409) {
                    Toast.makeText(this, "Este código ya ha sido registrado", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Error: ${error.networkResponse?.statusCode} - ${error.message}", Toast.LENGTH_LONG).show()
                }
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

    private fun searchProductByCode(code: String) {
        val url = "${EndPoints.GET_CODE_PRODUCTS}/$code"
        val requestQueue = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                if (response.length() > 0) {
                    val productObject = response.getJSONObject(0)
                    productId = productObject.getInt("id")
                    editTextName.setText(productObject.getString("name"))
                    editTextPrice.setText(productObject.getDouble("price").toString())
                    editTextStock.setText(productObject.getInt("stock").toString())
                    val size = productObject.getString("size")
                    when (size) {
                        "Personal" -> radioGroupSize.check(R.id.radioPersonal)
                        "Mediano" -> radioGroupSize.check(R.id.radioMedium)
                        "Familiar" -> radioGroupSize.check(R.id.radioFamily)
                    }
                } else {
                    Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error: ${error.networkResponse?.statusCode} - ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        requestQueue.add(request)
    }

    private fun updateProduct(id: Int, code: String, name: String, price: Double, stock: Int, size: String) {
        val url = "${EndPoints.UPDATE_PRODUCT}/$id"
        val requestQueue = Volley.newRequestQueue(this)

        val jsonBody = JSONObject()
        jsonBody.put("id", id)
        jsonBody.put("code", code)
        jsonBody.put("name", name)
        jsonBody.put("price", price)
        jsonBody.put("stock", stock)
        jsonBody.put("size", size)

        val request = object : StringRequest(
            Request.Method.PUT, url,
            { response ->
                Toast.makeText(this, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show()
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

    private fun changeProductStatus(id: Int) {
        val url = "${EndPoints.DELETE_PRODUCT}/$id"
        val requestQueue = Volley.newRequestQueue(this)

        val jsonBody = JSONObject()
        jsonBody.put("status", false)

        val request = object : StringRequest(
            Request.Method.PUT, url,
            { response ->
                Toast.makeText(this, "Producto eliminado correctamente", Toast.LENGTH_SHORT).show()
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

    private fun clearFields() {
        editTextCode.text.clear()
        editTextName.text.clear()
        editTextPrice.text.clear()
        editTextStock.text.clear()
        radioGroupSize.clearCheck()
    }

}