package com.example.proyecto_final_android

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ProductoLista(private val context: Activity, internal var productos: List<Producto>) : ArrayAdapter<Producto>(context, R.layout.layout_listar_productos, productos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val verProductos = inflater.inflate(R.layout.layout_listar_productos, null, true)

        val txtVerCodigo = verProductos.findViewById<TextView>(R.id.txtVercodigo)
        val txtVerNombre = verProductos.findViewById<TextView>(R.id.txtVerNombre)
        val txtVerPrecio = verProductos.findViewById<TextView>(R.id.txtVerPrecio)
        val txtVerStock = verProductos.findViewById<TextView>(R.id.txtVerStock)
        val txtVerSize = verProductos.findViewById<TextView>(R.id.txtVerSize)

        val producto = productos[position]
        txtVerCodigo.text = producto.code
        txtVerNombre.text = producto.name
        txtVerPrecio.text = producto.price.toString()
        txtVerStock.text = producto.stock.toString()
        txtVerSize.text = producto.size
        return verProductos
    }
}