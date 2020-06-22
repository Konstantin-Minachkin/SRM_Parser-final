package com.example.warhammer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TabHost;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SAVES";
    private static final String TABLE_SAVE = "save";
    private static final String KEY_NAME = "name";
    private static final String KEY_ALB = "album";
    private static final String KEY_ID = "id";

    private static final String DB_CREATE = "create table " + TABLE_SAVE + "(" + KEY_ID  + " integer primary key autoincrement NOT NULL,"
            + KEY_NAME + " text," + KEY_ALB + " text" + ")";
    Context myContext;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = context;
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_SAVE);
    }

    //Описываем структуру данных
    private ContentValues createContentValues(String name, String album) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_ALB, album);
        return values;
    }

    public void add(String name, String alb) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = createContentValues(name, alb);
        Cursor table = db.query(TABLE_SAVE, null, KEY_NAME + " == '" + name + "'", null, null, null, null);
        if (table != null && table.getCount() <= 0) {
            db.insert(TABLE_SAVE, null, values);
            Toast toast = Toast.makeText(myContext, "Успешно добавили в закладки", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            Toast toast = Toast.makeText(myContext, "Не добавили в закладки", Toast.LENGTH_SHORT);
            toast.show();
        }
        db.close();
    }

    public void delete(int rowId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SAVE, KEY_ID + "=" + rowId, null);
        db.close();
    }

    //Получение всей таблицы
    public Cursor getSavesTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_SAVE, new String[] { KEY_ID, KEY_NAME, KEY_ALB}, null, null, null,
                null, null);
    }

}
