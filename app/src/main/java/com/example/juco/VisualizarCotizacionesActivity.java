package com.example.juco;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class VisualizarCotizacionesActivity extends AppCompatActivity {
    private ListView cotizacionesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_cotizaciones);

        // Enlaza el ListView desde el diseño XML
        cotizacionesListView = findViewById(R.id.cotizacionesListView);

        // Inicializa y muestra las cotizaciones en el ListView
        mostrarCotizaciones();
    }

    private void mostrarCotizaciones() {
        CotizacionDbHelper dbHelper = new CotizacionDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                CotizacionDbHelper.CotizacionEntry.COLUMN_PRODUCT_NAME,
                CotizacionDbHelper.CotizacionEntry.COLUMN_COLOR,
                // Añade los otros campos aquí...
        };

        Cursor cursor = db.query(
                CotizacionDbHelper.CotizacionEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        // Crear un ArrayList para almacenar las cotizaciones
        ArrayList<String> cotizacionesList = new ArrayList<>();

        while (cursor.moveToNext()) {
            String productName = cursor.getString(cursor.getColumnIndexOrThrow(CotizacionDbHelper.CotizacionEntry.COLUMN_PRODUCT_NAME));
            String color = cursor.getString(cursor.getColumnIndexOrThrow(CotizacionDbHelper.CotizacionEntry.COLUMN_COLOR));
            // Añade los otros campos aquí...

            // Construye una cadena con los datos y agrégala a la lista
            String cotizacionInfo = "Producto: " + productName + ", Color: " + color;
            cotizacionesList.add(cotizacionInfo);
        }

        cursor.close();

        // Configura un ArrayAdapter para el ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cotizacionesList);
        cotizacionesListView.setAdapter(adapter);

        // Agregar un Listener al ListView para manejar la selección de elementos
        cotizacionesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Acción a realizar cuando se selecciona un elemento de la lista
                // Puedes abrir una vista de detalles o realizar otras acciones aquí
                String selectedCotizacion = cotizacionesList.get(position);
                Toast.makeText(VisualizarCotizacionesActivity.this, selectedCotizacion, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
