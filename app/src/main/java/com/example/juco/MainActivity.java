package com.example.juco;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    String titleText = "Decoraciones JUCO";
    private List<Decoracion> decoracionList; // Lista de objetos Decoracion
    private double totalSum;

    private EditText nombreEditText;
    private EditText direccionEditText;
    private EditText mailEditText;
    private EditText telEditText;

    private EditText productNameEditText;
    private EditText colorEditText;
    private EditText modeloEditText;
    private EditText squareMeterEditText;
    private EditText pricePerSquareMeterEditText;
    private TextView totalSumTextView;
    private EditText discountEditText;
    private EditText largeEditText;
    private EditText widthEditText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        decoracionList = new ArrayList<>();
        totalSum = 0;
        nombreEditText = findViewById(R.id.nombreEditText);
        direccionEditText= findViewById(R.id.direccionEditText);
        mailEditText= findViewById(R.id.mailEditText);
        telEditText= findViewById(R.id.telEditText);
        productNameEditText = findViewById(R.id.productNameEditText);
        colorEditText = findViewById(R.id.colorEditText);
        modeloEditText = findViewById(R.id.modeloEditText);
        squareMeterEditText = findViewById(R.id.squareMeterEditText);
        pricePerSquareMeterEditText = findViewById(R.id.pricePerSquareMeterEditText);
        totalSumTextView = findViewById(R.id.totalSumTextView);

        largeEditText = findViewById(R.id.largeEditText);
        widthEditText= findViewById(R.id.widthEditText);


        discountEditText = findViewById(R.id.discountEditText);

        Button sendButton = findViewById(R.id.sendButton);
        Button printButton = findViewById(R.id.printButton);

        Button clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDecoraciones();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aggregate();
                printResult();
            }
        });

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imprintPdf();
            }
        });

        widthEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String widthString = widthEditText.getText().toString();
                    if (!widthString.isEmpty()) {
                        double large = Double.parseDouble(largeEditText.getText().toString());
                        double width = Double.parseDouble(widthString);
                        double squareMeter = large * width;
                        squareMeterEditText.setText(String.valueOf(squareMeter));
                    } else {
                        squareMeterEditText.setText("");
                    }
                }
            }
        });
        largeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String widthString = widthEditText.getText().toString();
                    if (!widthString.isEmpty()) {
                        double large = Double.parseDouble(largeEditText.getText().toString());
                        double width = Double.parseDouble(widthString);
                        double squareMeter = large * width;
                        squareMeterEditText.setText(String.valueOf(squareMeter));
                    } else {
                        squareMeterEditText.setText("");
                    }
                }
            }
        });

    }



    public void clearDecoraciones() {
        decoracionList.clear();
        totalSum = 0;
        totalSumTextView.setText("total");
        nombreEditText.setText("");
        direccionEditText.setText("");
        mailEditText.setText("");
        telEditText.setText("");
        productNameEditText.setText("");
        colorEditText.setText("");
        modeloEditText.setText("");
        largeEditText.setText("");
        widthEditText.setText("");
        pricePerSquareMeterEditText.setText("");
        squareMeterEditText.setText("");
        discountEditText.setText("");
        TableLayout tableLayout = findViewById(R.id.tableLayout);

        int rowCount = tableLayout.getChildCount();
        for (int i = 1; i < rowCount; i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            int viewCount = row.getChildCount();
            for (int j = 0; j < viewCount; j++) {
                View view = row.getChildAt(j);
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    textView.setText("");
                }
            }
        }

        Toast.makeText(MainActivity.this, "Datos eliminados correctamente", Toast.LENGTH_SHORT).show();
    }

    public void aggregate() {
        // Obtener los datos de la interfaz de usuario
        String productName = productNameEditText.getText().toString();
        String color = colorEditText.getText().toString();
        String modelo = modeloEditText.getText().toString();
        double large = Double.parseDouble(largeEditText.getText().toString());
        double widt = Double.parseDouble(widthEditText.getText().toString());
        double squareMeter = large*widt;
        double pricePerSquareMeter = Double.parseDouble(pricePerSquareMeterEditText.getText().toString());
        double totalPrice = squareMeter * pricePerSquareMeter;
        double discount = 0.0;
        if (!discountEditText.getText().toString().isEmpty()) {
            discount = Double.parseDouble(discountEditText.getText().toString());
        }



        // Calcular el descuento
        double discountAmount = totalPrice * (discount / 100);

        // Calcular el precio total aplicando el descuento
        double totalWithDiscount = totalPrice - discountAmount;

        // Crear una nueva instancia de Decoracion y agregarla a la lista
        Decoracion decoracion = new Decoracion(productName, color, modelo, large, widt, squareMeter, pricePerSquareMeter, totalWithDiscount);
        decoracionList.add(decoracion);

        // Actualizar la suma total
        totalSum += totalWithDiscount;

        // Mostrar la suma total en el TextView
        totalSumTextView.setText("SumaTotal: " + String.valueOf(totalSum));

        // Opcional: Mostrar un mensaje de confirmación
        Toast.makeText(MainActivity.this, "Datos agregados correctamente", Toast.LENGTH_SHORT).show();
    }




    private boolean checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 200);
    }

    private void requestStoragePermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityResultLauncher<String[]> launcher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    if (result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Permiso concedido, puedes continuar con la operación de creación de PDF.
                        printResult();
                    } else {
                        // Permiso denegado, muestra un mensaje al usuario o realiza alguna acción adecuada.
                        Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        launcher.launch(permissions);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 200) {
            if (grantResults.length > 0) {
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permiso concedido", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }
    public void printResult() {
        TableLayout tableLayout = findViewById(R.id.tableLayout);

        // Obtener los datos de la instancia de Decoracion
        String productName = productNameEditText.getText().toString();
        String color = colorEditText.getText().toString();
        String modelo = modeloEditText.getText().toString();
        double large = Double.parseDouble(largeEditText.getText().toString());
        double widt = Double.parseDouble(widthEditText.getText().toString());
        double squareMeter = large*widt;
        double pricePerSquareMeter = Double.parseDouble(pricePerSquareMeterEditText.getText().toString());
        double totalPrice = squareMeter * pricePerSquareMeter;

        // Crear una nueva fila para los datos
        TableRow row = new TableRow(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        row.setLayoutParams(layoutParams);

        // Crear y configurar los TextView para cada celda de la fila
        TextView productNameTextView = new TextView(this);
        productNameTextView.setText(productName);
        row.addView(productNameTextView);

        TextView colorTextView = new TextView(this);
        colorTextView.setText(color);
        row.addView(colorTextView);

        TextView modeloTextView = new TextView(this);
        modeloTextView.setText(modelo);
        row.addView(modeloTextView);

        TextView largeTextView = new TextView(this);
        largeTextView.setText(String.valueOf(large));
        row.addView(largeTextView);

        TextView widtTextView = new TextView(this);
        widtTextView.setText(String.valueOf(widt));
        row.addView(widtTextView);

        TextView squareMeterTextView = new TextView(this);
        squareMeterTextView.setText(String.valueOf(squareMeter));
        row.addView(squareMeterTextView);

        TextView pricePerSquareMeterTextView = new TextView(this);
        pricePerSquareMeterTextView.setText(String.valueOf(pricePerSquareMeter));
        row.addView(pricePerSquareMeterTextView);

        TextView totalPriceTextView = new TextView(this);
        totalPriceTextView.setText(String.valueOf(totalPrice));
        row.addView(totalPriceTextView);

        // Agregar la fila a la tabla
        tableLayout.addView(row);
        clearFields();
    }
    public void clearFields() {
        productNameEditText.setText("");
        colorEditText.setText("");
        modeloEditText.setText("");
        largeEditText.setText("");
        widthEditText.setText("");
        pricePerSquareMeterEditText.setText("");
        squareMeterEditText.setText("");
    }
    public void imprintPdf() {
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
        String leftText = "NOMBRE: " + nombreEditText.getText().toString() + "\n"
                + "DIRECCIÓN: " + direccionEditText.getText().toString() + "\n"
                + "MAIL: " + mailEditText.getText().toString() + "\n"
                + "TEL: " + telEditText.getText().toString();
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
        List<Decoracion> decoraciones = getDecoraciones(); // Obtén la lista de decoraciones desde donde corresponda

        // Dibujar los datos de la tabla
        for (int i = 0; i < decoraciones.size(); i++) {
            Decoracion decoracion = decoraciones.get(i);
            String productName = decoracion.getProductName();
            String modelo = decoracion.getModelo();
            String color = decoracion.getColor();
            double large = decoracion.getLarge();
            double widt = decoracion.getWidt();
            double squareMeter = decoracion.getSquareMeter();
            double pricePerSquareMeter = decoracion.getPricePerSquareMeter();
            double totalPrice = squareMeter*pricePerSquareMeter;

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
        int subtotalTableY = tableY + (decoraciones.size() + 1) * rowHeight + 20;
        int subtotalRowY = subtotalTableY + rowHeight;

// Calcular el subtotal
        double subtotal = 0;
        for (Decoracion decoracion : decoraciones) {
            subtotal += decoracion.getSquareMeter() * decoracion.getPricePerSquareMeter();
        }

// Obtener el descuento ingresado (si lo hay)
        double descuento = 0;
        String descuentoStr = discountEditText.getText().toString();
        if (!descuentoStr.isEmpty()) {
            descuento = Double.parseDouble(descuentoStr);
        }

// Calcular el total
        double total = subtotal - ((descuento/100)*subtotal);

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
                +"FIRMA Y NOMBRE\n"
                + "https://www.decoracionesjuco.com";

// Dibujar el tercer texto adicional sin negrita
        drawMultilineTextCentered(canvas, signatureText, signatureTextX, signatureTextY,boldPaint, pageWidth - 2 * signatureTextX);

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
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear el PDF", Toast.LENGTH_LONG).show();
        }

        document.close();
    }
    private List<Decoracion> getDecoraciones() {
        return decoracionList;
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
    public class Decoracion {
        private String productName;
        private String color;
        private String modelo;
        private double large;
        private double widt;
        private double squareMeter;
        private double pricePerSquareMeter;
        private double totalPrice;

        public Decoracion(String productName, String color, String modelo,double large,double widt, double squareMeter, double pricePerSquareMeter, double totalPrice) {
            this.productName = productName;
            this.color = color;
            this.modelo = modelo;
            this.large = large;
            this.widt = widt;
            this.squareMeter = squareMeter;
            this.pricePerSquareMeter = pricePerSquareMeter;
            this.totalPrice = totalPrice;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getModelo() {
            return modelo;
        }

        public void setModelo(String modelo) {
            this.modelo = modelo;
        }

        public double getLarge() {
            return large;
        }

        public void setLarge(double large) {
            this.large = large;
        }

        public double getWidt() {
            return widt;
        }

        public void setWidt(double widt) {
            this.widt = widt;
        }

        public double getSquareMeter() {
            return squareMeter;
        }

        public void setSquareMeter(double squareMeter) {
            this.squareMeter = squareMeter;
        }

        public double getPricePerSquareMeter() {
            return pricePerSquareMeter;
        }

        public void setPricePerSquareMeter(double pricePerSquareMeter) {
            this.pricePerSquareMeter = pricePerSquareMeter;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }

    }
    public String getCurrentDate() {
        // Obtener la fecha actual
        Date currentDate = new Date();

        // Crear un formato de fecha
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Formatear la fecha actual como una cadena de texto
        return dateFormat.format(currentDate);
    }


}