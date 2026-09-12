package com.land.measurement.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.land.measurement.model.LandRecord;

import java.util.ArrayList;
import java.util.List;

public class LandDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pak_land_measurements.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_RECORDS = "land_records";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_SQ_FEET = "total_sq_feet";
    public static final String COLUMN_MARLAS = "total_marlas";
    public static final String COLUMN_KANALS = "total_kanals";
    public static final String COLUMN_BREAKDOWN = "breakdown_summary";
    public static final String COLUMN_MODE = "calculation_mode";
    public static final String COLUMN_STANDARD = "marla_standard";
    public static final String COLUMN_UNIT = "input_unit";
    public static final String COLUMN_SIDE_A = "side_a";
    public static final String COLUMN_SIDE_B = "side_b";
    public static final String COLUMN_SIDE_C = "side_c";
    public static final String COLUMN_SIDE_D = "side_d";
    public static final String COLUMN_DIAGONAL = "diagonal";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private static LandDatabaseHelper instance;

    public static synchronized LandDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new LandDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public LandDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_RECORDS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_LOCATION + " TEXT, "
                + COLUMN_SQ_FEET + " REAL, "
                + COLUMN_MARLAS + " REAL, "
                + COLUMN_KANALS + " REAL, "
                + COLUMN_BREAKDOWN + " TEXT, "
                + COLUMN_MODE + " TEXT, "
                + COLUMN_STANDARD + " TEXT, "
                + COLUMN_UNIT + " TEXT, "
                + COLUMN_SIDE_A + " REAL, "
                + COLUMN_SIDE_B + " REAL, "
                + COLUMN_SIDE_C + " REAL, "
                + COLUMN_SIDE_D + " REAL, "
                + COLUMN_DIAGONAL + " REAL, "
                + COLUMN_TIMESTAMP + " INTEGER"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        onCreate(db);
    }

    public long insertRecord(LandRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, record.getTitle());
        values.put(COLUMN_LOCATION, record.getLocation());
        values.put(COLUMN_SQ_FEET, record.getTotalSqFeet());
        values.put(COLUMN_MARLAS, record.getTotalMarlas());
        values.put(COLUMN_KANALS, record.getTotalKanals());
        values.put(COLUMN_BREAKDOWN, record.getBreakdownSummary());
        values.put(COLUMN_MODE, record.getCalculationMode());
        values.put(COLUMN_STANDARD, record.getMarlaStandard());
        values.put(COLUMN_UNIT, record.getInputUnit());
        values.put(COLUMN_SIDE_A, record.getSideA());
        values.put(COLUMN_SIDE_B, record.getSideB());
        values.put(COLUMN_SIDE_C, record.getSideC());
        values.put(COLUMN_SIDE_D, record.getSideD());
        values.put(COLUMN_DIAGONAL, record.getDiagonal());
        values.put(COLUMN_TIMESTAMP, record.getTimestamp() == 0 ? System.currentTimeMillis() : record.getTimestamp());

        return db.insert(TABLE_RECORDS, null, values);
    }

    public List<LandRecord> getAllRecords() {
        return searchRecords(null);
    }

    public List<LandRecord> searchRecords(String query) {
        List<LandRecord> recordList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery;
        String[] selectionArgs = null;

        if (query == null || query.trim().isEmpty()) {
            selectQuery = "SELECT * FROM " + TABLE_RECORDS + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";
        } else {
            selectQuery = "SELECT * FROM " + TABLE_RECORDS + " WHERE "
                    + COLUMN_TITLE + " LIKE ? OR "
                    + COLUMN_LOCATION + " LIKE ? "
                    + "ORDER BY " + COLUMN_TIMESTAMP + " DESC";
            String wild = "%" + query.trim() + "%";
            selectionArgs = new String[]{wild, wild};
        }

        Cursor cursor = db.rawQuery(selectQuery, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                LandRecord record = cursorToRecord(cursor);
                recordList.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recordList;
    }

    public boolean deleteRecord(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_RECORDS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public void clearAllRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECORDS, null, null);
    }

    private LandRecord cursorToRecord(Cursor cursor) {
        LandRecord record = new LandRecord();
        record.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
        record.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
        record.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)));
        record.setTotalSqFeet(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SQ_FEET)));
        record.setTotalMarlas(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_MARLAS)));
        record.setTotalKanals(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_KANALS)));
        record.setBreakdownSummary(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BREAKDOWN)));
        record.setCalculationMode(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODE)));
        record.setMarlaStandard(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STANDARD)));
        record.setInputUnit(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIT)));
        record.setSideA(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SIDE_A)));
        record.setSideB(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SIDE_B)));
        record.setSideC(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SIDE_C)));
        record.setSideD(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SIDE_D)));
        record.setDiagonal(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_DIAGONAL)));
        record.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
        return record;
    }
}
