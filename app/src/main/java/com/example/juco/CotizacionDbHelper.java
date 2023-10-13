package com.example.juco;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class CotizacionDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Cotizacion.db";

    public static class CotizacionEntry implements BaseColumns {
        public static final String TABLE_NAME = "cotizaciones";
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_COLOR = "color";
        public static final String COLUMN_MODELO = "modelo";
        public static final String COLUMN_LARGE = "large";
        public static final String COLUMN_WIDTH = "width";
        public static final String COLUMN_SQUARE_METER = "square_meter";
        public static final String COLUMN_PRICE_PER_SQUARE_METER = "price_per_square_meter";
        public static final String COLUMN_TOTAL_PRICE = "total_price";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CotizacionEntry.TABLE_NAME + " (" +
                    CotizacionEntry._ID + " INTEGER PRIMARY KEY," +
                    CotizacionEntry.COLUMN_PRODUCT_NAME + " TEXT," +
                    CotizacionEntry.COLUMN_COLOR + " TEXT," +
                    CotizacionEntry.COLUMN_MODELO + " TEXT," +
                    CotizacionEntry.COLUMN_LARGE + " REAL," +
                    CotizacionEntry.COLUMN_WIDTH + " REAL," +
                    CotizacionEntry.COLUMN_SQUARE_METER + " REAL," +
                    CotizacionEntry.COLUMN_PRICE_PER_SQUARE_METER + " REAL," +
                    CotizacionEntry.COLUMN_TOTAL_PRICE + " REAL)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CotizacionEntry.TABLE_NAME;

    public CotizacionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
