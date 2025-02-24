package com.example.proyecto_final_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ListarVentas : AppCompatActivity() {

    lateinit var btnregresar3: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_ventas)

        btnregresar3 = findViewById(R.id.btnregresar3)

        btnregresar3.setOnClickListener{
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
    }
}