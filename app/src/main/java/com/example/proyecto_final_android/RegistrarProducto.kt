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

    lateinit var btnRegresar: Button
    lateinit var btnEliminarProducto: Button
    lateinit var btnAgregarProducto: Button
    lateinit var btnBuscarProducto: Button
    lateinit var btnEditarProducto: Button
    lateinit var btnLimpiarCampos: Button
    lateinit var txtCodigo: EditText
    lateinit var txtNombre: EditText
    lateinit var txtPrecio: EditText
    lateinit var txtStock: EditText
    lateinit var rgTipo: RadioGroup
    var productId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_producto)

        btnRegresar = findViewById(R.id.btnRegresar)
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto)
        btnBuscarProducto = findViewById(R.id.btnBuscarProducto)
        btnEditarProducto = findViewById(R.id.btnEditarProducto)
        btnEliminarProducto = findViewById(R.id.btnEliminarProducto)
        btnLimpiarCampos = findViewById(R.id.btnLimpiarCampos)
        txtCodigo = findViewById(R.id.txtCodigo)
        txtNombre = findViewById(R.id.txtNombre)
        txtPrecio = findViewById(R.id.txtPrecio)
        txtStock = findViewById(R.id.txtStock)
        rgTipo = findViewById(R.id.rgTipo)

        btnRegresar.setOnClickListener{
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        btnAgregarProducto.setOnClickListener{
            val codigo = txtCodigo.text.toString()
            val nombre = txtNombre.text.toString()
            val precio = txtPrecio.text.toString()
            val stock = txtStock.text.toString()
            val idTamañoSeleccionado = rgTipo.checkedRadioButtonId

            if (codigo.isEmpty() || nombre.isEmpty() || precio.isEmpty() || stock.isEmpty() || idTamañoSeleccionado == -1) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tamañoSeleccionado = findViewById<RadioButton>(idTamañoSeleccionado).text.toString()
            agregarProducto(codigo, nombre, precio.toDouble(), stock.toInt(), tamañoSeleccionado)
        }

        btnLimpiarCampos.setOnClickListener {
            limpiar()
        }

        btnEliminarProducto.setOnClickListener {
            if (productId != null) {
                eliminarProducto(productId!!)
            } else {
                Toast.makeText(this, "Por favor busque un producto primero", Toast.LENGTH_SHORT).show()
            }
        }

        btnEditarProducto.setOnClickListener {
            val codigo = txtCodigo.text.toString()
            val nombre = txtNombre.text.toString()
            val precio = txtPrecio.text.toString()
            val stock = txtStock.text.toString()
            val idTamañoSeleccionado = rgTipo.checkedRadioButtonId

            if (productId == null || codigo.isEmpty() || nombre.isEmpty() || precio.isEmpty() || stock.isEmpty() || idTamañoSeleccionado == -1) {
                Toast.makeText(this, "Por favor complete todos los campos y busque un producto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tamañoSeleccionado = findViewById<RadioButton>(idTamañoSeleccionado).text.toString()
            editarProducto(productId!!, codigo, nombre, precio.toDouble(), stock.toInt(), tamañoSeleccionado)
        }

        btnBuscarProducto.setOnClickListener {
            val codigo = txtCodigo.text.toString()
            if (codigo.isNotEmpty()) {
                buscarProductoPorCodigo(codigo)
            } else {
                Toast.makeText(this, "Por favor ingrese un código", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun agregarProducto(codigo: String, nombre: String, precio: Double, stock: Int, tamaño: String) {
        val url = EndPoints.SAVE_PRODUCT
        val requestQueue = Volley.newRequestQueue(this)

        val jsonBody = JSONObject()
        jsonBody.put("code", codigo)
        jsonBody.put("name", nombre)
        jsonBody.put("price", precio)
        jsonBody.put("stock", stock)
        jsonBody.put("size", tamaño)

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

    private fun buscarProductoPorCodigo(codigo: String) {
        val url = "${EndPoints.GET_CODE_PRODUCTS}/$codigo"
        val requestQueue = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                if (response.length() > 0) {
                    val productObject = response.getJSONObject(0)
                    productId = productObject.getInt("id")
                    txtNombre.setText(productObject.getString("name"))
                    txtPrecio.setText(productObject.getDouble("price").toString())
                    txtStock.setText(productObject.getInt("stock").toString())
                    val tamaño = productObject.getString("size")
                    when (tamaño) {
                        "Personal" -> rgTipo.check(R.id.rbPersonal)
                        "Mediano" -> rgTipo.check(R.id.rdMediano)
                        "Familiar" -> rgTipo.check(R.id.rdFamiliar)
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

    private fun editarProducto(id: Int, codigo: String, nombre: String, precio: Double, stock: Int, tamaño: String) {
        val url = "${EndPoints.UPDATE_PRODUCT}/$id"
        val requestQueue = Volley.newRequestQueue(this)

        val jsonBody = JSONObject()
        jsonBody.put("id", id)
        jsonBody.put("code", codigo)
        jsonBody.put("name", nombre)
        jsonBody.put("price", precio)
        jsonBody.put("stock", stock)
        jsonBody.put("size", tamaño)

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

    private fun eliminarProducto(id: Int) {
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

    private fun limpiar() {
        txtCodigo.text.clear()
        txtNombre.text.clear()
        txtPrecio.text.clear()
        txtStock.text.clear()
        rgTipo.clearCheck()
    }
}