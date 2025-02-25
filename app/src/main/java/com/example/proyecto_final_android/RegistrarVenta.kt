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

    lateinit var txtclient: EditText
    lateinit var cbolista: Spinner
    lateinit var txttotal: EditText
    lateinit var txtquantity: EditText
    lateinit var btnAddSave: Button
    lateinit var cbdiscount: CheckBox
    lateinit var btnnew : Button

    lateinit var txtsubtotal: EditText
    lateinit var txtigv: EditText

    lateinit var txtnumbersale: EditText



    var productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_venta)

        txtdni = findViewById(R.id.txtdni)
        txtclient = findViewById(R.id.txtclient)
        txtnumbersale = findViewById(R.id.txtnumbersale)

        cbolista = findViewById(R.id.cbolista)

        txtquantity = findViewById(R.id.txtquantity)
        btnAddSave = findViewById(R.id.btnAddSave)
        btnregresar5 = findViewById(R.id.btnregresar5)
        btnnew = findViewById(R.id.btnnew)


        txtsubtotal = findViewById(R.id.txtsubtotal)
        txtigv = findViewById(R.id.txtigv)
        txttotal = findViewById(R.id.txttotal)




        cbdiscount = findViewById(R.id.cbdiscount)


        // Cargar productos en el Spinner
        loadProducts()

        // Calcular el total cuando cambie la cantidad
        txtquantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calculateSubTotal()
                calculateIGV()
                calculateTotal()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        btnAddSave.setOnClickListener {
            saveSale()
        }
        btnnew.setOnClickListener{
            new()
        }
        cbdiscount.setOnCheckedChangeListener { _, _ ->
            calculateTotal()  // Recalcula el total con o sin descuento
        }

        btnregresar5.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
    }

    // Función para obtener productos de la API
    private fun loadProducts() {
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
                items.add("${product.name} - S/. ${product.price}")
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            cbolista.adapter = adapter

        }, { error ->
            Toast.makeText(this, "Error al cargar productos: ${error.message}", Toast.LENGTH_LONG).show()
        })

        queue.add(request)
    }

    // Función para calcular el total basado en el producto seleccionado y la cantidad
    private fun calculateSubTotal() {
        val quantity = txtquantity.text.toString().toIntOrNull() ?: 0
        val selectedIndex = cbolista.selectedItemPosition
        if (selectedIndex >= 0 && selectedIndex < productList.size) {
            val product = productList[selectedIndex]
            val subtotal = product.price *  quantity
            txtsubtotal.setText(subtotal.toString())
        }
    }

    private fun calculateIGV() {
        val subtotal = txtsubtotal.text.toString().toDoubleOrNull() ?: 0.0
        val igv = subtotal * 0.18
        txtigv.setText(igv.toString())
    }

    private fun calculateTotal() {
        val subtotal = txtsubtotal.text.toString().toDoubleOrNull() ?: 0.0
        val igv = txtigv.text.toString().toDoubleOrNull() ?: 0.0
        var descuento = 0.0

        // Aplica descuento si el CheckBox está marcado (10% sobre el subtotal)
        if (cbdiscount.isChecked) {
            descuento = subtotal * 0.10
        }

        val total = subtotal - descuento + igv

        // Formatear a dos decimales
        val totalFormateado = String.format("%.2f", total)

        txttotal.setText(totalFormateado)
    }

    // Función para obtener la fecha actual en formato "YYYY-MM-DD"
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }




    // Función para registrar la venta en la API
    // Función para registrar la venta en la API
    private fun saveSale() {
        val queue = Volley.newRequestQueue(this)

       // val numberSale = "V${System.currentTimeMillis()}" // Genera un número de venta único
        val numbersale = txtnumbersale.text.toString()
        val dni = txtdni.text.toString()
        val date = getCurrentDate()
        val client = txtclient.text.toString()
        val quantity = txtquantity.text.toString().toIntOrNull() ?: 1
        val selectedIndex = cbolista.selectedItemPosition

        if (numbersale.isEmpty() || dni.isEmpty() || client.isEmpty() || quantity <= 0 || selectedIndex < 0) {
            Toast.makeText(this, "Por favor, complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            return
        }

        val product = productList[selectedIndex]
        val subtotal = product.price * quantity
        val igv = subtotal * 0.18
        var total = subtotal + igv

        // Aplica descuento si el CheckBox está marcado
        val discount = if (cbdiscount.isChecked) subtotal * 0.10 else 0.0
        total -= discount // Aplica el descuento al total

        val jsonVenta = JSONObject().apply {
            put("numberSale", numbersale)
            put("dni", dni)
            put("date", date)  // En formato "YYYY-MM-DD"
            put("client", client)
            put("product", JSONObject().apply {
                put("id", product.id)
            })
            put("quantity", quantity)
            put("subtotal", subtotal)
            put("igv", igv)
            put("discount", discount) // Ahora el descuento se envía a la API
            put("total", total) // Total ajustado con el descuento
        }

        val request = JsonObjectRequest(Request.Method.POST, EndPoints.SAVE_SALE, jsonVenta, { response ->
            Toast.makeText(this, "Venta registrada con éxito", Toast.LENGTH_LONG).show()
            finish()
        }, { error ->
            Toast.makeText(this, "Error al registrar la venta: ${error.message}", Toast.LENGTH_LONG).show()
        })

        queue.add(request)
    }
    fun nuevo() {
        txtdni.setText("")
        txtclient.setText("")
        txtnumbersale.setText("")
        txtquantity.setText("")
        txtsubtotal.setText("")
        txtigv.setText("")
        txttotal.setText("")

        cbdiscount.isChecked = false

        // Reinicia el Spinner al primer elemento
        if (cbolista.adapter != null && cbolista.adapter.count > 0) {
            cbolista.setSelection(0)
        }

        txtdni.requestFocus()
    }
    fun new() {
        txtdni.setText("")
        txtclient.setText("")
        txtnumbersale.setText("")
        txtquantity.setText("")
        txtsubtotal.setText("")
        txtigv.setText("")
        txttotal.setText("")

        cbdiscount.isChecked = false

        // Reinicia el Spinner al primer elemento
        if (cbolista.adapter != null && cbolista.adapter.count > 0) {
            cbolista.setSelection(0)
        }

        txtdni.requestFocus()
    }



}

// Clase Producto con los mismos campos que el backend
data class Product(
    val id: Int,
    val code: String,
    val name: String,
    val price: Double,
    val stock: Int
)
