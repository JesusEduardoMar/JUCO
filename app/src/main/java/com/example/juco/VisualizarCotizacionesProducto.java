package com.example.juco;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VisualizarCotizacionesProducto extends AppCompatActivity {
    private ListView cotizacionesListView;
    private EditText totalSumTextView;
    private EditText descuentoSumTextView;
    private String jsonResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_cotizaciones_producto);

        Button crearPdf = findViewById(R.id.crearPdf);
        cotizacionesListView = findViewById(R.id.cotizacionesListView);
        totalSumTextView = findViewById(R.id.totalSumTextView);
        descuentoSumTextView = findViewById(R.id.descuentoSumTextView);

        crearPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imprimirPdf();
            }
        });

//       Obtener el id de la intent
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        // Realiza la solicitud HTTP en segundo plano http://juco.x10.mx/obtener_cotizaciones_producto.php
        new FetchCotizacionesTask().execute(id);
    }

    private class FetchCotizacionesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String id = params[0];
            try {
                URL url = new URL("http://juco.x10.mx/obtener_cotizaciones_producto.php");

                // Abrir la conexión HTTP
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                // Crear los datos a enviar usando Uri.Builder
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("id", id);
                String query = builder.build().getEncodedQuery();

                // Escribir los datos en el flujo de salida
                try (OutputStream os = urlConnection.getOutputStream()) {
                    os.write(query.getBytes(StandardCharsets.UTF_8));
                }

                // Obtener la respuesta del servidor
                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        return stringBuilder.toString();
                    }
                } else {
                    // Manejar el error del servidor
                    return null;
                }
            } catch (IOException e) {
                // Manejar otras excepciones
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Procesa el resultado JSON y actualiza el ListView
                jsonResult = result;
                updateListView(result);
            } else {
                Toast.makeText(VisualizarCotizacionesProducto.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
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

                double sumaTotal = 0;
                double des = 0;



                for (int i = 0; i < cotizacionesArray.length(); i++) {
                    double totalProductos = 0;
                    JSONObject cotizacion = cotizacionesArray.getJSONObject(i);
                    String nombreProducto = cotizacion.optString("product_name", "Nombre no disponible");
                    String color = cotizacion.optString("color", "Color no disponible");
                    String modelo = cotizacion.optString("modelo", "Modelo no disponible");
                    String largo = cotizacion.optString("large", "Largo no disponible");
                    String ancho = cotizacion.optString("width", "Ancho no disponible");
                    String metrosCuadrado = cotizacion.optString("square_meter", "Metros cuadrados no disponible");
                    String precioPorMetroc = cotizacion.optString("price_per_square_meter", "Precio no disponible");
                    String totalProducto = cotizacion.optString("total_price", "Total no disponible");
                    String Descuento = cotizacion.optString("discount", "Descuento no disponible");

                    totalProductos= Double.parseDouble(metrosCuadrado)*Double.parseDouble(precioPorMetroc);
                    sumaTotal += Integer.parseInt(totalProducto);
                    des = Double.parseDouble(Descuento);


                    cotizacionesList.add("Producto: " + (i + 1) + "\nNombre del producto: " + nombreProducto + "\nColor: " + color + "\nModelo: " + modelo
                            + "\nLargo: " + largo + "\nAncho: " + ancho + "\nMetros cuadrados: " + metrosCuadrado + "\nPrecio por m^2: " + precioPorMetroc
                            + "\nTotal Producto: " + totalProductos);
                }


                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cotizacionesList);
                cotizacionesListView.setAdapter(adapter);

                // Mostrar la suma total en el TextView correspondiente
                descuentoSumTextView.setText(String.valueOf(des));
                totalSumTextView.setText(String.valueOf(sumaTotal));
                // Maneja el clic en elementos de la lista si es necesario
                cotizacionesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Puedes agregar aquí el código para manejar el clic en elementos de la lista
                        // Por ejemplo, mostrar detalles de la cotización
                    }
                });
            } else {
                Toast.makeText(VisualizarCotizacionesProducto.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void imprimirPdf() {

        List<JSONObject> listaProductos = obtenerListaProductosDesdeJson(jsonResult);
        Intent intent = getIntent();
        // Crear un nuevo documento PDF
        PdfDocument document = new PdfDocument();

        // Crear la configuración de la página (tamaño carta)
        int pageWidth = 612;
        int pageHeight = 1000;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();

        // Crear una nueva página
        PdfDocument.Page page = document.startPage(pageInfo);

        // Obtener el lienzo para dibujar en la página
        Canvas canvas = page.getCanvas();

        // Configurar la fuente y el tamaño de texto
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);
        paint.setAntiAlias(true);

        Bitmap bitmap, bitmapScaled;

        // Definir las coordenadas para el logo
        int logoX = 50;
        int logoY = 50;

        // Dibujar el logo en la posición especificada
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        bitmapScaled = Bitmap.createScaledBitmap(bitmap, 80, 80, false);
        canvas.drawBitmap(bitmapScaled, logoX, logoY, paint);

        // Definir las coordenadas para el texto con alineación central
        int centerX = pageWidth / 2;
        int centerY = 50;

        // Dibujar el texto con alineación central
        String centralText = "Av. Ayuntamiento Nº 734, Barrio La Salud, C.P. 20240";
        centerY = drawMultilineTextCentered(canvas, centralText, centerX, centerY, paint, pageWidth);

        centralText = "Aguascalientes Ags.";
        centerY = drawMultilineTextCentered(canvas, centralText, centerX, centerY, paint, pageWidth);

        centralText = "Teléfono 449 239 5369 Celular 449 231 9171";
        centerY = drawMultilineTextCentered(canvas, centralText, centerX, centerY, paint, pageWidth);

        centralText = "e-mail decoracionesjuco@outlook.com";
        centerY = drawMultilineTextCentered(canvas, centralText, centerX, centerY, paint, pageWidth);

        // Definir las coordenadas para el texto con alineación a la derecha
        int rightX = pageWidth - 50;
        int rightY = 150;

        // Dibujar el texto con alineación a la derecha
        String rightText = "AGUASCALIENTES AGS " + getCurrentDate();
        canvas.drawText(rightText, rightX - paint.measureText(rightText), rightY, paint);

        // Definir las coordenadas para el texto con alineación a la izquierda
        int leftX = 50;
        int leftY = centerY + 20;

        // Dibujar el texto con alineación a la izquierda

        String leftText = "NOMBRE: " + intent.getStringExtra("nombre") + "\n"
                + "DIRECCIÓN: " + intent.getStringExtra("direccion") + "\n"
                + "MAIL: " + intent.getStringExtra("mail") + "\n"
                + "TEL: " + intent.getStringExtra("cel");
        drawMultilineText(canvas, leftText, leftX, leftY, paint, pageWidth - 2 * leftX);

        // Definir los márgenes y el espacio entre filas en la tabla
        int tableX = 50;
        int tableY = centerY + 100;
        int columnWidth = (pageWidth - 2 * tableX) / 8;
        int rowHeight = 40; // Ajusta el valor según tu preferencia

        // Dibujar los datos de la tabla
        String[] tableData = {
                "PRODUCTO",
                "MODELO",
                "COLOR",
                "LARGO",
                "ANCHO",
                "M2",
                "PRECIO",
                "IMPORTE"
        };
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);

        // Dibujar los bordes de la tabla
        canvas.drawRect(tableX, tableY, tableX + columnWidth * tableData.length, tableY + rowHeight, paint);
        for (int i = 0; i < tableData.length; i++) {
            float textWidth = paint.measureText(tableData[i]);
            float x = tableX + i * columnWidth + (columnWidth - textWidth) / 2;
            float y = tableY + rowHeight - (rowHeight - paint.getFontSpacing()) / 2;
            canvas.drawText(tableData[i], x, y, paint);
            canvas.drawLine(tableX + (i + 1) * columnWidth, tableY, tableX + (i + 1) * columnWidth, tableY + rowHeight, paint);
        }


        // Dibujar los datos de la tabla
        for (int i = 0; i < listaProductos.size(); i++) {
            JSONObject producto = listaProductos.get(i);
            String productName = producto.optString("product_name", "Nombre no disponible");
            String modelo = producto.optString("modelo", "Modelo no disponible");
            String color = producto.optString("color", "Color no disponible");
            double large = producto.optDouble("large", 0);
            double widt = producto.optDouble("width", 0);
            double squareMeter = producto.optDouble("square_meter", 0);
            double pricePerSquareMeter = producto.optDouble("price_per_square_meter", 0);
            double totalPrice = squareMeter * pricePerSquareMeter;

            String[] rowData = {
                    productName,
                    modelo,
                    color,
                    String.valueOf(large),
                    String.valueOf(widt),
                    String.valueOf(squareMeter),
                    String.valueOf(pricePerSquareMeter),
                    String.valueOf(totalPrice)
            };

            canvas.drawRect(tableX, tableY + (i + 1) * rowHeight, tableX + columnWidth * rowData.length, tableY + (i + 2) * rowHeight, paint);
            for (int j = 0; j < rowData.length; j++) {
                float textWidth = paint.measureText(rowData[j]);
                float x = tableX + j * columnWidth + (columnWidth - textWidth) / 2;
                float y = tableY + (i + 2) * rowHeight - (rowHeight - paint.getFontSpacing()) / 2;
                canvas.drawText(rowData[j], x, y, paint);
                canvas.drawLine(tableX + (j + 1) * columnWidth, tableY + (i + 1) * rowHeight, tableX + (j + 1) * columnWidth, tableY + (i + 2) * rowHeight, paint);
            }
        }
        // Definir las coordenadas para la segunda tabla
        int subtotalTableY = tableY + (listaProductos.size() + 1) * rowHeight + 20;
        int subtotalRowY = subtotalTableY + rowHeight;

// Calcular el subtotal
        double subtotal = 0;
        for (JSONObject producto : listaProductos) {
            double squareMeter = producto.optDouble("square_meter", 0);
            double pricePerSquareMeter = producto.optDouble("price_per_square_meter", 0);
            subtotal += squareMeter * pricePerSquareMeter;
        }

// Obtener el descuento ingresado (si lo hay)
        double descuento = listaProductos.isEmpty() ? 0 : listaProductos.get(0).optDouble("discount", 0);

// Calcular el total
        double total = subtotal - ((descuento / 100) * subtotal);

// Definir los datos de la segunda tabla
        String[] subtotalData = {
                "SUBTOTAL",
                "DESC(%)",
                "TOTAL"
        };

        String[] totalData = {
                String.valueOf(subtotal),
                String.valueOf(descuento),
                String.valueOf(total)
        };

// Dibujar la segunda tabla
        canvas.drawRect(tableX, subtotalTableY, tableX + columnWidth * subtotalData.length, subtotalTableY + rowHeight, paint);
        for (int i = 0; i < subtotalData.length; i++) {
            float textWidth = paint.measureText(subtotalData[i]);
            float x = tableX + i * columnWidth + (columnWidth - textWidth) / 2;
            float y = subtotalRowY - (rowHeight - paint.getFontSpacing()) / 2;
            canvas.drawText(subtotalData[i], x, y, paint);
            canvas.drawLine(tableX + (i + 1) * columnWidth, subtotalTableY, tableX + (i + 1) * columnWidth, subtotalRowY, paint);
        }

        canvas.drawRect(tableX, subtotalRowY, tableX + columnWidth * totalData.length, subtotalRowY + rowHeight, paint);
        for (int i = 0; i < totalData.length; i++) {
            float textWidth = paint.measureText(totalData[i]);
            float x = tableX + i * columnWidth + (columnWidth - textWidth) / 2;
            float y = subtotalRowY + rowHeight - (rowHeight - paint.getFontSpacing()) / 2;
            canvas.drawText(totalData[i], x, y, paint);
            canvas.drawLine(tableX + (i + 1) * columnWidth, subtotalRowY, tableX + (i + 1) * columnWidth, subtotalRowY + rowHeight, paint);
        }
        // Definir las coordenadas para el texto adicional
        int additionalTextX = 50;
        int additionalTextY = subtotalRowY + rowHeight + 20;

// Definir el texto adicional con formato de negrita
        String additionalTextBold = "EL COSTO INCLUYE IVA\n"
                + "PRECIO SUJETOS A CAMBIO SIN PREVIO AVISO\n"
                + "LOS COLORES PUEDEN VARIAR DE LOTE A LOTE\n"
                + "EL PEDIDO ES INCANCELABLE POR SER FABRICADO A LA MEDIDA\n"
                + "LAS PERSIANAS BLACKOUT NO DAN OSCURIDAD TOTAL\n"
                + "NO INCLUYE MOVIMIENTO DE MUEBLES\n"
                + "EL ÁREA DE INSTALACIÓN DEBERÁ ESTAR PREVIAMENTE DESALOJADA\n"
                + "EL ALCANCE SE LIMITA AL MATERIAL AQUÍ DESCRITO, CUALQUIER DESVIACIÓN Y/O EQUIPO ADICIONAL REQUERIRÁ DE UNA RENEGOCIACIÓN DEL\n"
                + "PRECIO Y TIEMPO DE ENTREGA\n"
                + "CONDICIONES COMERCIALES: 60 % DE ANTICIPO, RESTO CONTRA ENTREGA\n"
                + "TIEMPO DE ENTREGA: DE 5 A 8 DÍAS HÁBILES";
        Paint boldPaint = new Paint();
        boldPaint.setColor(Color.BLACK);
        boldPaint.setTextSize(10);


// Dibujar el texto adicional en negrita
        drawMultilineText(canvas, additionalTextBold, additionalTextX, additionalTextY, boldPaint, pageWidth - 2 * additionalTextX);

// Definir las coordenadas para el segundo texto adicional (transferencia bancaria)
        // Definir las coordenadas para el segundo texto adicional (transferencia bancaria)
        int transferTextX = additionalTextX;
        int transferTextY = additionalTextY + 200;

// Definir el segundo texto adicional con formato de negrita
        String transferTextBold = "TRANSFERENCIA A LA CUENTA 65505498767\n"
                + "CLABE 014010655054987678\n"
                + "BANCO SANTANDER\n"
                + "ACEPTO TÉRMINOS Y CONDICIONES Y PAGARÉ A LA ORDEN DE DECORACIONES JUCO SA DE CV LA CANTIDAD\n"
                + "QUE AVALA ESTA COTIZACIÓN DE LO CONTRARIO CAUSARÁ UN INTERÉS DEL 5% MENSUAL HASTA EL DÍA SE\n"
                + "SU LIQUIDACIÓN";

// Dibujar el segundo texto adicional en negrita
        drawMultilineText(canvas, transferTextBold, transferTextX, transferTextY, boldPaint, pageWidth - 2 * transferTextX);

// Definir las coordenadas para el tercer texto adicional (firma y nombre)
        int signatureTextX = pageWidth / 2;
        int signatureTextY = pageHeight - 100;

// Definir el tercer texto adicional sin formato de negrita
        String signatureText = "_____________________________________\n"
                + "FIRMA Y NOMBRE\n"
                + "https://www.decoracionesjuco.com";

// Dibujar el tercer texto adicional sin negrita
        drawMultilineTextCentered(canvas, signatureText, signatureTextX, signatureTextY, boldPaint, pageWidth - 2 * signatureTextX);

        // Finalizar la página
        document.finishPage(page);
        Intent intentidbd = getIntent();
        String cotizacion_info_id = intentidbd.getStringExtra("cotizacion_info_id");

        // Guardar el documento PDF en el almacenamiento externo
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(directory, "Cotizacion" + cotizacion_info_id + ".pdf");
        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "Se creó el PDF correctamente", Toast.LENGTH_LONG).show();
            Uri pdfUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);

            // Abrir el PDF con una aplicación de visualización de PDF
            Intent intentPdf = new Intent(Intent.ACTION_VIEW);
            intentPdf.setDataAndType(pdfUri, "application/pdf");
            intentPdf.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intentPdf);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear el PDF", Toast.LENGTH_LONG).show();
        }

        document.close();
    }

    private int drawMultilineTextCentered(Canvas canvas, String text, int x, int y, Paint paint, int maxWidth) {
        String[] lines = text.split("\n");
        float lineHeight = paint.getFontSpacing();
        int totalHeight = lines.length * (int) lineHeight;

        for (String line : lines) {
            float textWidth = paint.measureText(line);
            float textX = x - textWidth / 2;
            float textY = y + lineHeight / 2;
            canvas.drawText(line, textX, textY, paint);
            y += lineHeight;
        }

        return y + totalHeight / 2;
    }

    private List<JSONObject> obtenerListaProductosDesdeJson(String jsonResult) {
        List<JSONObject> listaProductos = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            String status = jsonObject.getString("status");

            if ("success".equals(status)) {
                JSONArray cotizacionesArray = jsonObject.getJSONArray("cotizaciones");

                for (int i = 0; i < cotizacionesArray.length(); i++) {
                    JSONObject cotizacion = cotizacionesArray.getJSONObject(i);

                    // Agregar directamente el objeto JSONObject a la lista
                    listaProductos.add(cotizacion);
                }
            } else {
                Toast.makeText(this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listaProductos;
    }

    private void drawMultilineText(Canvas canvas, String text, float x, float y, Paint paint, float maxWidth) {
        String[] lines = text.split("\n");
        float lineHeight = paint.getFontSpacing();

        for (String line : lines) {
            String[] words = line.split(" ");
            float spaceWidth = paint.measureText(" ");

            float lineWidth = 0;
            StringBuilder lineBuilder = new StringBuilder();

            for (String word : words) {
                float wordWidth = paint.measureText(word);

                if (lineWidth + wordWidth <= maxWidth) {
                    lineBuilder.append(word).append(" ");
                    lineWidth += wordWidth + spaceWidth;
                } else {
                    float textX = x;
                    float textY = y + lineHeight;
                    canvas.drawText(lineBuilder.toString(), textX, textY, paint);
                    y += lineHeight;

                    lineBuilder = new StringBuilder(word).append(" ");
                    lineWidth = wordWidth + spaceWidth;
                }
            }

            float textX = x;
            float textY = y + lineHeight;
            canvas.drawText(lineBuilder.toString(), textX, textY, paint);
            y += lineHeight;
        }

    }

    public static String getCurrentDate() {
        // Obtén la fecha actual
        Date currentDate = new Date();

        // Define el formato de fecha deseado
        SimpleDateFormat dateFormat = new SimpleDateFormat(" 'al dia' dd 'de' MMMM 'del' yyyy");

        // Formatea la fecha según el formato
        String formattedDate = dateFormat.format(currentDate);

        return formattedDate;
    }
}