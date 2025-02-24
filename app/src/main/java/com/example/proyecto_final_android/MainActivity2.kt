package com.example.proyecto_final_android

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
    }

    fun registrarVenta(): Boolean {
        val registrarVenta = Intent(this, RegistrarVenta::class.java)
        startActivity(registrarVenta)
        return true
    }

    fun registrarProducto(): Boolean {
        val registrarProducto = Intent(this, RegistrarProducto::class.java)
        startActivity(registrarProducto)
        return true
    }

    fun listaVenta(): Boolean {
        val listaVenta = Intent(this, ListarVentas::class.java)
        startActivity(listaVenta)
        return true
    }

    fun listaProducto(): Boolean {
        val listaProducto = Intent(this, ListarProductos::class.java)
        startActivity(listaProducto)
        return true
    }

    fun somos(): Boolean {
        val somos = Intent(this, MainActivity3::class.java)
        startActivity(somos)
        return true
    }

    //FUNCIONES PARA PROGRAMAR LOS MENUS
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ubicacion -> {
                val gmmIntentUri = Uri.parse("geo:0,0?q=-12.11484673787058, -76.99115544446025(Pizzeria Mammamía)")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
                return true
            }
            R.id.quienes -> return somos()
            R.id.cerrar -> return salir()
            R.id.listap -> return listaProducto()
            R.id.listav -> return listaVenta()
            R.id.registrarp -> return registrarProducto()
            R.id.registrarv -> return registrarVenta()
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun salir():Boolean {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setMessage("Fin de la APP")
            .setTitle("Cerrar App")
            .setPositiveButton(android.R.string.yes) { _, _ ->
                Toast.makeText(applicationContext, "Se cerró la aplicación", Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
            .setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
        return true
    }
}