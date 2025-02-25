package com.example.proyecto_final_android

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class VentaLista(private val context: Activity, private val sales: List<Venta>) :
    ArrayAdapter<Venta>(context, R.layout.layout_listar_ventas, sales) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.layout_listar_ventas, null, true)

        val textNumberSale = listViewItem.findViewById<TextView>(R.id.textNumberSale)
        val textViewDni = listViewItem.findViewById<TextView>(R.id.textViewDni)
        val textViewDateLocal = listViewItem.findViewById<TextView>(R.id.textViewDateLocal)
        val textViewClient = listViewItem.findViewById<TextView>(R.id.textViewClient)
        val textViewProduct = listViewItem.findViewById<TextView>(R.id.textViewProduct)
        val textViewQuantity = listViewItem.findViewById<TextView>(R.id.textViewQuantity)
        val textViewTotal = listViewItem.findViewById<TextView>(R.id.textViewTotal)
        val textViewDiscount = listViewItem.findViewById<TextView>(R.id.textViewDiscount)

        val sale = sales[position]
        textNumberSale.text = sale.number_sale
        textViewDni.text = sale.dni
        textViewDateLocal.text = sale.date
        textViewClient.text = sale.client
        textViewProduct.text = sale.product.name
        textViewQuantity.text = sale.quantity.toString()
        textViewTotal.text = sale.total.toString()
        textViewDiscount.text = sale.discount.toString()

        return listViewItem
    }
}