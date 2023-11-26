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
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
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
        double total = squareMeter * pricePerSquareMeter * (1 - discount / 100);

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
                imprimirPdf(listaProductos);
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

    public void imprimirPdf(List<Producto> listaProductos) {

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
        String leftText = "NOMBRE: " + intent.getStringExtra("nombre")+ "\n"
                + "DIRECCIÓN: " +  intent.getStringExtra("direccion") + "\n"
                + "MAIL: " + intent.getStringExtra("mail") + "\n"
                + "TEL: " +  intent.getStringExtra("tel");
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

        // Obtener los datos de la clase Decoracion
        //List<Decoracion> decoraciones = getDecoraciones(); // Obtén la lista de decoraciones desde donde corresponda

        // Dibujar los datos de la tabla
        for (int i = 0; i < listaProductos.size(); i++) {
            Producto producto = listaProductos.get(i);
            String productName = producto.getProductName();
            String modelo = producto.getModelo();
            String color = producto.getColor();
            double large = producto.getLarge();
            double widt = producto.getWidth();
            double squareMeter = producto.getSquareMeter();
            double pricePerSquareMeter = producto.getPricePerSquareMeter();
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
        for (Producto producto : listaProductos) {
            subtotal += producto.getSquareMeter() * producto.getPricePerSquareMeter();
        }

// Obtener el descuento ingresado (si lo hay)
        Producto ultimoProducto = listaProductos.get(0);
        double descuento = ultimoProducto.getDiscount();

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

        // Guardar el documento PDF en el almacenamiento externo
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(directory, "Archivo.pdf");
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