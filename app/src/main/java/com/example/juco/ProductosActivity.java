package com.example.juco;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProductosActivity extends AppCompatActivity {
    private List<Producto> listaProductos = new ArrayList<>();
    private TableLayout tableLayout;
    private EditText totalSumTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        tableLayout = findViewById(R.id.tableLayout);
        totalSumTextView = findViewById(R.id.totalSumTextView);
        Button sendButton = findViewById(R.id.sendButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        Button nextButton = findViewById(R.id.nextButton);

        // Configurar el clic del botón Agregar
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarProducto();
            }
        });

        // Configurar el clic del botón Cancelar
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiarTabla();
            }
        });

        // Configurar el clic del botón Siguiente
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarProductosEnBD();
            }
        });
    }
    private void agregarProducto() {
        // Obtener referencias a los elementos de la interfaz de usuario
        EditText productNameEditText = findViewById(R.id.productNameEditText);
        EditText colorEditText = findViewById(R.id.colorEditText);
        EditText modeloEditText = findViewById(R.id.modeloEditText);
        EditText largeEditText = findViewById(R.id.largeEditText);
        EditText widthEditText = findViewById(R.id.widthEditText);
        EditText squareMeterEditText = findViewById(R.id.squareMeterEditText);
        EditText pricePerSquareMeterEditText = findViewById(R.id.pricePerSquareMeterEditText);
        EditText discountEditText = findViewById(R.id.discountEditText);

        // Obtener valores de los campos de texto
        String productName = productNameEditText.getText().toString();
        String color = colorEditText.getText().toString();
        String modelo = modeloEditText.getText().toString();
        double large = Double.parseDouble(largeEditText.getText().toString());
        double width = Double.parseDouble(widthEditText.getText().toString());
        double squareMeter = large * width;
        double pricePerSquareMeter = Double.parseDouble(pricePerSquareMeterEditText.getText().toString());
        double total = squareMeter * pricePerSquareMeter * (1 - Double.parseDouble(discountEditText.getText().toString()) / 100);

        // Crear objeto Producto
        Producto producto = new Producto(productName, color, modelo, large, width, squareMeter, pricePerSquareMeter, total);

        // Agregar el producto a la lista
        listaProductos.add(producto);

        // Actualizar la tabla y la suma total
        actualizarTabla();
    }

    private void actualizarTabla() {
        // Limpiar la tabla antes de actualizarla
        tableLayout.removeAllViews();

        // Crear una fila de encabezado
        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        // Crear encabezados de columna
        agregarTextViewATableRow("Producto", headerRow);
        agregarTextViewATableRow("Color", headerRow);
        agregarTextViewATableRow("Modelo", headerRow);
        agregarTextViewATableRow("Largo", headerRow);
        agregarTextViewATableRow("Ancho", headerRow);
        agregarTextViewATableRow("m^2", headerRow);
        agregarTextViewATableRow("Precio U", headerRow);
        agregarTextViewATableRow("Precio Total", headerRow);

        // Agregar la fila de encabezado a la tabla
        tableLayout.addView(headerRow);

        // Iterar sobre la lista de productos y agregar cada producto a la tabla
        for (Producto producto : listaProductos) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            agregarTextViewATableRow(producto.getProductName(), tableRow);
            agregarTextViewATableRow(producto.getColor(), tableRow);
            agregarTextViewATableRow(producto.getModelo(), tableRow);
            agregarTextViewATableRow(String.valueOf(producto.getLarge()), tableRow);
            agregarTextViewATableRow(String.valueOf(producto.getWidth()), tableRow);
            agregarTextViewATableRow(String.valueOf(producto.getSquareMeter()), tableRow);
            agregarTextViewATableRow(String.valueOf(producto.getPricePerSquareMeter()), tableRow);
            agregarTextViewATableRow(String.valueOf(producto.getTotal()), tableRow);

            // Agregar la fila a la tabla
            tableLayout.addView(tableRow);
        }

        // Calcular la suma total
        double sumaTotal = 0;
        for (Producto producto : listaProductos) {
            sumaTotal += producto.getTotal();
        }

        // Mostrar la suma total en el TextView correspondiente
        totalSumTextView.setText(String.valueOf(sumaTotal));
    }

    private void limpiarTabla() {
        // Limpiar la lista de productos
        listaProductos.clear();

        // Actualizar la tabla y la suma total
        actualizarTabla();
    }
    private void guardarProductosEnBD() {
        // Crear un JSONArray con los productos para enviar a la API
        JSONArray jsonArray = new JSONArray();
        for (Producto producto : listaProductos) {
            jsonArray.put(producto.toJson());
        }

        // URL de tu API para guardar productos
        String apiUrl = "http://192.168.75.172/JUCO/cotizacion_productos.php";  // Reemplaza con la URL de tu API

        // Ejecutar la tarea asíncrona para enviar los productos a la API
        new GuardarProductosTask().execute(apiUrl, jsonArray.toString());
    }

    // Clase AsyncTask para realizar la solicitud HTTP en segundo plano
    private static class GuardarProductosTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String apiUrl = params[0];
            String jsonProductos = params[1];

            try {
                // Crear la URL y la conexión HTTP
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Enviar los datos JSON al servidor
                OutputStream os = connection.getOutputStream();
                os.write(jsonProductos.getBytes());
                os.flush();
                os.close();

                // Obtener la respuesta del servidor
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // La solicitud fue exitosa
                    // Puedes manejar la respuesta del servidor aquí si es necesario
                    Log.d("GuardarProductos", "Productos guardados exitosamente");
                } else {
                    // Hubo un error en la solicitud
                    Log.e("GuardarProductos", "Error en la solicitud HTTP: " + responseCode);
                }

                // Desconectar la conexión
                connection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private void agregarTextViewATableRow(String texto, TableRow tableRow) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView.setPadding(5, 5, 5, 5);
        textView.setText(texto);
        tableRow.addView(textView);
    }


}