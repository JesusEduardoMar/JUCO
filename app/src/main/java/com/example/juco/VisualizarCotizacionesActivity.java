package com.example.juco;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class VisualizarCotizacionesActivity extends AppCompatActivity {
    private ListView cotizacionesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_cotizaciones);

        cotizacionesListView = findViewById(R.id.cotizacionesListView);

        // Realiza la solicitud HTTP en segundo plano
        new FetchCotizacionesTask().execute("http://juco.x10.mx/obtener_cotizaciones.php");
    }

    // Clase AsyncTask para realizar la solicitud HTTP en segundo plano
    private class FetchCotizacionesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Procesa el resultado JSON y actualiza el ListView
                updateListView(result);
            } else {
                Toast.makeText(VisualizarCotizacionesActivity.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateListView(String jsonResult) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            String status = jsonObject.getString("status");

            if ("success".equals(status)) {
                JSONArray cotizacionesArray = jsonObject.getJSONArray("cotizaciones");
                ArrayList<String> cotizacionesList = new ArrayList<>();

                for (int i = 0; i < cotizacionesArray.length(); i++) {
                    JSONObject cotizacion = cotizacionesArray.getJSONObject(i);
                    String nombre = cotizacion.optString("nombre", "Nombre no disponible");
                    String direccion = cotizacion.optString("direccion", "Dirección no disponible");
                    String mail = cotizacion.optString("mail", "Email no disponible");
                    String cel = cotizacion.optString("cel", "Celular no disponible");
                    String fecha = cotizacion.optString("fecha_cotizacion", "Fecha no disponible");
                    cotizacionesList.add("Nombre: " + nombre + "\nDirección: " + direccion+ "\nEmail: "+ mail + "\nCelular: "+cel+ "\nFecha: "+fecha);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cotizacionesList);
                cotizacionesListView.setAdapter(adapter);

                // Maneja el clic en elementos de la lista si es necesario
                cotizacionesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Puedes agregar aquí el código para manejar el clic en elementos de la lista
                        // Por ejemplo, mostrar detalles de la cotización
                    }
                });
            } else {
                Toast.makeText(VisualizarCotizacionesActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
