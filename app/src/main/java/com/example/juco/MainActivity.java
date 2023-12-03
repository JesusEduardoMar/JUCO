package com.example.juco;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private EditText nombreEditText;
    private EditText direccionEditText;
    private EditText mailEditText;
    private EditText telEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        nombreEditText = findViewById(R.id.nombreEditText);
        direccionEditText = findViewById(R.id.direccionEditText);
        mailEditText = findViewById(R.id.mailEditText);
        telEditText = findViewById(R.id.telEditText);

        Button sendButton = findViewById(R.id.updateButton);
        Button clearButton = findViewById(R.id.nextButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarDatos();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiar();
            }
        });
    }

    public void limpiar() {
        nombreEditText.setText("");
        direccionEditText.setText("");
        mailEditText.setText("");
        telEditText.setText("");

        Toast.makeText(MainActivity.this, "Datos borrados", Toast.LENGTH_SHORT).show();
    }

    public void enviarDatos() {
        String nombre = nombreEditText.getText().toString();
        String direccion = direccionEditText.getText().toString();
        String mail = mailEditText.getText().toString();
        String tel = telEditText.getText().toString();

        // Realizar la solicitud HTTP en segundo plano
        new SendDataAsyncTask().execute(nombre, direccion, mail, tel);
    }

    private class SendDataAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String nombre = params[0];
            String direccion = params[1];
            String mail = params[2];
            String tel = params[3];

            try {
                // URL de tu API en PHP
                URL url = new URL("http://juco.x10.mx/cotizacion_info.php");

                // Abrir la conexión HTTP
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                // Crear los datos a enviar
                String data = URLEncoder.encode("nombre", "UTF-8") + "=" + URLEncoder.encode(nombre, "UTF-8") +
                        "&" + URLEncoder.encode("direccion", "UTF-8") + "=" + URLEncoder.encode(direccion, "UTF-8") +
                        "&" + URLEncoder.encode("mail", "UTF-8") + "=" + URLEncoder.encode(mail, "UTF-8") +
                        "&" + URLEncoder.encode("cel", "UTF-8") + "=" + URLEncoder.encode(tel, "UTF-8");

                // Escribir los datos en el flujo de salida
                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = data.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // Obtener la respuesta del servidor
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Leer la respuesta del servidor
                    try (InputStreamReader in = new InputStreamReader(urlConnection.getInputStream())) {
                        BufferedReader reader = new BufferedReader(in);
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        // Parsear la respuesta JSON
                        JSONObject jsonResponse = new JSONObject(response.toString());

                        // Obtener el ID del JSON
                        String id = jsonResponse.optString("id");
                        Cotizacion cotizacion = new Cotizacion(id);
                        cotizacion.setCotizacion_info_id(id);

                        if(id!=null){
                            Intent intent = new Intent(MainActivity.this, ProductosActivity.class);
                            intent.putExtra("cotizacion_info_id", id);
                            intent.putExtra("nombre",nombre);
                            intent.putExtra("direccion",direccion);
                            intent.putExtra("mail",mail);
                            intent.putExtra("tel",tel);
                            startActivity(intent);
                        }
                        return "Datos enviados correctamente. ID: " + id;
                    }
                } else {
                    // Error en la solicitud
                    return "Error en la solicitud: " + responseCode;
                }
            } catch (Exception e) {
                return "Excepción: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Mostrar el resultado en un Toast o en la interfaz de usuario según sea necesario
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();

        }
    }
}
