package com.example.juco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

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

        EditText largeEditText = findViewById(R.id.largeEditText);
        EditText widthEditText = findViewById(R.id.widthEditText);


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

        // Agregar TextWatcher a largeEditText y widthEditText
        largeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No se necesita implementar
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                actualizarSquareMeter();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No se necesita implementar
            }
        });

        widthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No se necesita implementar
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                actualizarSquareMeter();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No se necesita implementar
            }
        });
    }

    private void actualizarSquareMeter() {
        EditText largeEditText = findViewById(R.id.largeEditText);
        EditText widthEditText = findViewById(R.id.widthEditText);
        EditText squareMeterEditText = findViewById(R.id.squareMeterEditText);

        double large = 0;
        double width = 0;

        if (!largeEditText.getText().toString().isEmpty()) {
            large = Double.parseDouble(largeEditText.getText().toString());
        }

        if (!widthEditText.getText().toString().isEmpty()) {
            width = Double.parseDouble(widthEditText.getText().toString());
        }

        double squareMeter = large * width;
        squareMeterEditText.setText(String.valueOf(squareMeter));
    }

    private void agregarProducto() {
        // Obtener referencias a los elementos de la interfaz de usuario
        EditText productNameEditText = findViewById(R.id.productNameEditText);
        EditText colorEditText = findViewById(R.id.colorEditText);
        EditText modeloEditText = findViewById(R.id.modeloEditText);
        EditText largeEditText = findViewById(R.id.largeEditText);
        EditText widthEditText = findViewById(R.id.widthEditText);
        //EditText squareMeterEditText = findViewById(R.id.squareMeterEditText);
        EditText pricePerSquareMeterEditText = findViewById(R.id.pricePerSquareMeterEditText);
        EditText discountEditText = findViewById(R.id.discountEditText);

        Intent intent = getIntent();
        String cotizacion_info_id = intent.getStringExtra("cotizacion_info_id");

        //Cotizacion cotizacion = new Cotizacion();
        // Obtener valores de los campos de texto
        //String cotizacion_info_id = cotizacion.getCotizacion_info_id();
        String productName = productNameEditText.getText().toString();
        String color = colorEditText.getText().toString();
        String modelo = modeloEditText.getText().toString();
        double large = Double.parseDouble(largeEditText.getText().toString());
        double width = Double.parseDouble(widthEditText.getText().toString());
        double squareMeter = large * width;
        double pricePerSquareMeter = Double.parseDouble(pricePerSquareMeterEditText.getText().toString());
        double discount = 0;
        if (!discountEditText.getText().toString().isEmpty()) {
            discount = Double.parseDouble(discountEditText.getText().toString());
        }
        double total = squareMeter * pricePerSquareMeter * (1 -  discount / 100);

        // Crear objeto Producto
        Producto producto = new Producto(cotizacion_info_id, productName, color, modelo, large, width, squareMeter, pricePerSquareMeter, total, discount);

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
        // Crear un JSONArray con los productos para enviar a la API
        JSONArray jsonArray = new JSONArray();
        for (Producto producto : listaProductos) {
            // Crear un objeto JSONObject para representar el producto
            JSONObject productoJson = producto.toJson();
            jsonArray.put(productoJson);
        }
        // URL de tu API para guardar productos
        String apiUrl = "http://192.168.100.58/JUCO/cotizacion_productos.php";  // Reemplaza con la URL de tu API

        // Ejecutar la tarea asíncrona para enviar los productos a la API
        try {
            boolean resultado = new GuardarProductosTask().execute(apiUrl, jsonArray.toString()).get();

            if (resultado) {
                // Operación exitosa
                Toast.makeText(ProductosActivity.this, "Productos guardados exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                // Error en la operación
                Toast.makeText(ProductosActivity.this, "Error al guardar los productos", Toast.LENGTH_SHORT).show();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // Clase AsyncTask para realizar la solicitud HTTP en segundo plano
    private static class GuardarProductosTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
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
                    return true;
                } else {
                    // Hubo un error en la solicitud
                    Log.e("GuardarProductos", "Error en la solicitud HTTP: " + responseCode);
                    return false;
                }

                // ... (código anterior)

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
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