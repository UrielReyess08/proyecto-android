package com.example.proyecto_final_android

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ProductoLista(private val context: Activity, internal var products: List<Producto>) : ArrayAdapter<Producto>(context, R.layout.layout_listar_productos, products) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.layout_listar_productos, null, true)

        val textViewCode = listViewItem.findViewById<TextView>(R.id.textViewCode)
        val textViewName = listViewItem.findViewById<TextView>(R.id.textViewName)
        val textViewPrice = listViewItem.findViewById<TextView>(R.id.textViewPrice)
        val textViewStock = listViewItem.findViewById<TextView>(R.id.textViewStock)
        val textViewSize = listViewItem.findViewById<TextView>(R.id.textViewSize)

        val product = products[position]
        textViewCode.text = product.code
        textViewName.text = product.name
        textViewPrice.text = product.price.toString()
        textViewStock.text = product.stock.toString()
        textViewSize.text = product.size
        return listViewItem
    }
}