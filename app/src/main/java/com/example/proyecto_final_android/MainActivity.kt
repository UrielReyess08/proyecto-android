package com.example.proyecto_final_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import com.android.volley.Request
class MainActivity : AppCompatActivity() {

    lateinit var btnsesion: Button
    lateinit var txtname: EditText
    lateinit var txtpassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtname = findViewById(R.id.txtname)
        txtpassword = findViewById(R.id.txtpassword)
        btnsesion = findViewById(R.id.btnsesion)


        btnsesion.setOnClickListener {
            loginUsuario()
        }
    }
    private fun loginUsuario() {
        val nombre = txtname.text.toString().trim()
        val contrasena = txtpassword.text.toString().trim()

        if (nombre.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        val stringRequest = object : StringRequest(
            Request.Method.POST, EndPoints.GET_USER_LOGIN,
            Response.Listener<String> { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val id = jsonObject.getInt("id")
                    val name = jsonObject.getString("name")

                    Toast.makeText(this, "Bienvenido, $name", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity2::class.java)
                    startActivity(intent)
                    finish()

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                val errorMessage = error.networkResponse?.statusCode?.let {
                    when (it) {
                        401 -> "Usuario o contraseña incorrectos"
                        500 -> "Error del servidor"
                        else -> "Error desconocido"
                    }
                } ?: "No se pudo conectar con el servidor"

                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                return hashMapOf(
                    "name" to nombre,
                    "password" to contrasena
                )
            }
        }

        // Agregar la solicitud a la cola de Volley
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }

}