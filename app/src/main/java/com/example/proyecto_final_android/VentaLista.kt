package com.example.proyecto_final_android

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class VentaLista(private val context: Activity, private val ventas: MutableList<Venta>) :
    ArrayAdapter<Venta>(context, R.layout.layout_listar_ventas, ventas) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.layout_listar_ventas, null, true)

        val txtNumeroVenta = listViewItem.findViewById<TextView>(R.id.txtNumeroVenta)
        val txtDni = listViewItem.findViewById<TextView>(R.id.txtDni)
        val txtFechaVenta = listViewItem.findViewById<TextView>(R.id.txtFechaVenta)
        val txtCliente = listViewItem.findViewById<TextView>(R.id.txtCliente)
        val txtProducto = listViewItem.findViewById<TextView>(R.id.txtProducto)
        val txtCantidad = listViewItem.findViewById<TextView>(R.id.txtCantidad)
        val txtTotal = listViewItem.findViewById<TextView>(R.id.txtTotal)
        val txtDescuento = listViewItem.findViewById<TextView>(R.id.txtDescuento)
        val btnEliminarVenta = listViewItem.findViewById<Button>(R.id.btneliminarventa)

        val venta = ventas[position]
        txtNumeroVenta.text = venta.number_sale
        txtDni.text = venta.dni
        txtFechaVenta.text = venta.date
        txtCliente.text = venta.client
        txtProducto.text = venta.product.name
        txtCantidad.text = venta.quantity.toString()
        txtTotal.text = venta.total.toString()
        txtDescuento.text = venta.discount.toString()

        btnEliminarVenta.setOnClickListener {
            eliminarVenta(venta.number_sale, position)
        }

        return listViewItem
    }

    private fun eliminarVenta(numeroVenta: String, position: Int) {
        val url = "${EndPoints.DISABLE_SALE}/$numeroVenta"

        val request = StringRequest(
            Request.Method.PUT, url,
            { response ->
                Toast.makeText(context, "Venta desactivada correctamente", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(
                    context,
                    "Error al desactivar: ${error.networkResponse?.statusCode ?: "null"}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        Volley.newRequestQueue(context).add(request)
    }
}