package com.example.proyecto_final_android

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class RegistrarVenta : AppCompatActivity() {

    lateinit var btnregresar5: Button
    lateinit var txtdni: EditText
    lateinit var txtcliente: EditText
    lateinit var cbolista: Spinner
    lateinit var txttotal: EditText
    lateinit var txtcantidad: EditText
    lateinit var btnGuardarventa: Button
    lateinit var cbdescuento: CheckBox
    lateinit var btnnuevo : Button
    lateinit var txtsubtotal: EditText
    lateinit var txtigv: EditText
    lateinit var txtnumeroventa: EditText
    var productList = mutableListOf<Product>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_venta)
        txtdni = findViewById(R.id.txtdni)
        txtcliente = findViewById(R.id.txtcliente)
        txtnumeroventa = findViewById(R.id.txtnumeroventa)
        cbolista = findViewById(R.id.cbolista)
        txtcantidad = findViewById(R.id.txtcantidad)
        btnGuardarventa = findViewById(R.id.btnGuardarventa)
        btnregresar5 = findViewById(R.id.btnregresar5)
        btnnuevo = findViewById(R.id.btnnuevo)
        txtsubtotal = findViewById(R.id.txtsubtotal)
        txtigv = findViewById(R.id.txtigv)
        txttotal = findViewById(R.id.txttotal)
        cbdescuento = findViewById(R.id.cbdescuento)
        CargarProductos()
        txtcantidad.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calcularSubTotal()
                calcularIGV()
                calcularTotal()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnGuardarventa.setOnClickListener {
            Guardarventa()
        }
        btnnuevo.setOnClickListener{
            nuevo()
        }
        cbdescuento.setOnCheckedChangeListener { _, _ ->
            calcularTotal()
        }

        btnregresar5.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
    }


    private fun CargarProductos() {
        val queue = Volley.newRequestQueue(this)
        val request = JsonArrayRequest(Request.Method.GET, EndPoints.GET_ALL_PRODUCTS, null, { response ->
            productList.clear()
            val items = mutableListOf<String>()

            for (i in 0 until response.length()) {
                val item = response.getJSONObject(i)
                val product = Product(
                    id = item.getInt("id"),
                    code = item.getString("code"),
                    name = item.getString("name"),
                    price = item.getDouble("price"),
                    stock = item.getInt("stock")
                )
                productList.add(product)
                items.add("${product.name} - S/. ${product.price} - Stock:${product.stock}")
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            cbolista.adapter = adapter

        }, { error ->
            Toast.makeText(this, "Error al cargar productos: ${error.message}", Toast.LENGTH_LONG).show()
        })

        queue.add(request)
    }


    private fun calcularSubTotal() {
        val quantity = txtcantidad.text.toString().toIntOrNull() ?: 0
        val selectedIndex = cbolista.selectedItemPosition
        if (selectedIndex >= 0 && selectedIndex < productList.size) {
            val product = productList[selectedIndex]
            val subtotal = product.price *  quantity
            txtsubtotal.setText(subtotal.toString())
        }
    }

    private fun calcularIGV() {
        val subtotal = txtsubtotal.text.toString().toDoubleOrNull() ?: 0.0
        val igv = subtotal * 0.18
        txtigv.setText(igv.toString())
    }

    private fun calcularTotal() {
        val subtotal = txtsubtotal.text.toString().toDoubleOrNull() ?: 0.0
        val igv = txtigv.text.toString().toDoubleOrNull() ?: 0.0
        var descuento = 0.0


        if (cbdescuento.isChecked) {
            descuento = subtotal * 0.10
        }

        val total = subtotal - descuento + igv


        val totalFormateado = String.format("%.2f", total)

        txttotal.setText(totalFormateado)
    }
    private fun obtenerFecha(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun Guardarventa() {
        val queue = Volley.newRequestQueue(this)


        val numbersale = txtnumeroventa.text.toString()
        val dni = txtdni.text.toString()
        val date = obtenerFecha()
        val client = txtcliente.text.toString()
        val quantity = txtcantidad.text.toString().toIntOrNull() ?: 1
        val selectedIndex = cbolista.selectedItemPosition

        if (numbersale.isEmpty() || dni.isEmpty() || client.isEmpty() || quantity <= 0 || selectedIndex < 0) {
            Toast.makeText(this, "Por favor, complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            return
        }

        val product = productList[selectedIndex]
        val subtotal = product.price * quantity
        val igv = subtotal * 0.18
        var total = subtotal + igv


        val discount = if (cbdescuento.isChecked) subtotal * 0.10 else 0.0
        total -= discount

        val jsonVenta = JSONObject().apply {
            put("numberSale", numbersale)
            put("dni", dni)
            put("date", date)
            put("client", client)
            put("product", JSONObject().apply {
                put("id", product.id)
            })
            put("quantity", quantity)
            put("subtotal", subtotal)
            put("igv", igv)
            put("discount", discount)
            put("total", total)
        }

        val request = JsonObjectRequest(Request.Method.POST, EndPoints.SAVE_SALE, jsonVenta,
            { response ->
                Toast.makeText(this, "Venta registrada con éxito", Toast.LENGTH_LONG).show()
            },
            { error ->
                Toast.makeText(this,
                    if (error.networkResponse?.data != null &&
                        String(error.networkResponse.data, Charsets.UTF_8).contains("Stock insuficiente", ignoreCase = true))
                        "No hay stock disponible"
                    else
                        "Error al registrar la venta",
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        queue.add(request)

    }
    fun nuevo() {
        txtdni.setText("")
        txtcliente.setText("")
        txtnumeroventa.setText("")
        txtcantidad.setText("")
        txtsubtotal.setText("")
        txtigv.setText("")
        txttotal.setText("")
        cbdescuento.isChecked = false


        if (cbolista.adapter != null && cbolista.adapter.count > 0) {
            cbolista.setSelection(0)
        }

        txtdni.requestFocus()
    }

}

data class Product(
    val id: Int,
    val code: String,
    val name: String,
    val price: Double,
    val stock: Int
)