package com.example.juco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class EditarCotizacionesUpdate extends AppCompatActivity {
    private EditText nombreEditText;
    private EditText direccionEditText;
    private EditText mailEditText;
    private EditText telEditText;
    //private EditText idEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cotizaciones_update);

        nombreEditText = findViewById(R.id.nombreEditText);
        direccionEditText = findViewById(R.id.direccionEditText);
        mailEditText = findViewById(R.id.mailEditText);
        telEditText = findViewById(R.id.telEditText);
        //idEditText = findViewById(R.id.idEditText);

        Intent intent = getIntent();

        nombreEditText.setText(intent.getStringExtra("nombre"));
        direccionEditText.setText(intent.getStringExtra("direccion"));
        mailEditText.setText(intent.getStringExtra("mail"));
        telEditText.setText(intent.getStringExtra("cel"));
        //idEditText.setText(intent.getStringExtra("id"));

        Button sendButton = findViewById(R.id.updateButton);
        Button clearButton = findViewById(R.id.nextButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarDatos();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiar();
            }
        });
    }

    public void actualizarDatos() {
        String nombre = nombreEditText.getText().toString().trim();
        String direccion = direccionEditText.getText().toString().trim();
        String mail = mailEditText.getText().toString().trim();
        String cel = telEditText.getText().toString().trim();
        //int id = Integer.parseInt(idEditText.getText().toString().trim()); // Descomentar si es necesario

        // Obtener el id de la intent
        Intent intent = getIntent();
        int id = Integer.parseInt(intent.getStringExtra("id"));

        String url = "http://juco.x10.mx/actualizar_cotizaciones.php"; // Reemplaza con la URL de tu API

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Manejar la respuesta del servidor
                        Toast.makeText(EditarCotizacionesUpdate.this, "Datos actualizados con exito", Toast.LENGTH_SHORT).show();
                        Intent intentInicio = new Intent(EditarCotizacionesUpdate.this, MainMenuActivity.class);
                        startActivity(intentInicio);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar errores de la solicitud
                        Toast.makeText(EditarCotizacionesUpdate.this, "Error en la solicitud: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", nombre);
                params.put("direccion", direccion);
                params.put("mail", mail);
                params.put("cel", cel);
                params.put("id", String.valueOf(id));

                return params;
            }
        };

        // Agregar la solicitud a la cola de solicitudes
        Volley.newRequestQueue(this).add(stringRequest);
    }

    public void limpiar() {
        nombreEditText.setText("");
        direccionEditText.setText("");
        mailEditText.setText("");
        telEditText.setText("");

        Toast.makeText(EditarCotizacionesUpdate.this, "Datos borrados", Toast.LENGTH_SHORT).show();
    }
}