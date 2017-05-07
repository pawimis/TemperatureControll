package com.example.root.temperaturecontroll.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;


public class DbPlan extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PlanDb";
    private static final String TABLE_NAME = "TablePlan";
    private static final String KEY_ID = "ID";
    private static final String KEY_HOUR = "HOUR";
    private static final String KEY_TEMPERATURE = "TEMPERATURE";
    private static final String KEY_SET_DATE = "SET_DATE";
    private static final String KEY_SET_HOUR = "SET_HOUR";
    private static final DateFormat timeDateFormat = new SimpleDateFormat("kk:mm");
    private static final DateFormat dateDateFormat = new SimpleDateFormat("yyyy-MM-dd");


    public DbPlan(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase(db);


    }
    private void createDatabase(SQLiteDatabase db) {
        if(db==null){
            db = this.getReadableDatabase();
        }
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME + " ("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_HOUR + " TIME, "
                + KEY_TEMPERATURE + " INTEGER, "
                + KEY_SET_DATE + " DATE, "
                + KEY_SET_HOUR + " TIME);";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public boolean insertRecord(String hour, String temperature){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+KEY_ID+ " FROM "+TABLE_NAME +" WHERE "+KEY_HOUR+" = '" + hour +"' ",null);
        if(cursor.getCount() > 0) {
            Log.i("Update","Update");
            updateRecord(hour,temperature);
            return true;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_HOUR, hour);
        contentValues.put(KEY_TEMPERATURE, temperature);
        contentValues.put(KEY_SET_HOUR,timeDateFormat.format(Calendar.getInstance().getTime()));
        contentValues.put(KEY_SET_DATE,dateDateFormat.format(Calendar.getInstance().getTime()));
        db.insert(TABLE_NAME, null, contentValues);
        cursor.close();
        return true;
    }
    public void updateRecord(String hour, String temperature ){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TEMPERATURE,temperature);
        contentValues.put(KEY_SET_HOUR,timeDateFormat.format(Calendar.getInstance().getTime()));
        contentValues.put(KEY_SET_DATE,dateDateFormat.format(Calendar.getInstance().getTime()));
        db.update(TABLE_NAME,contentValues,KEY_HOUR+"= '"+hour+"'",null);
        db.close();
        Log.i("Update",hour+"/"+temperature);

    }
    public String getRecord(String hour){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+KEY_TEMPERATURE+ " FROM "
                +TABLE_NAME+" WHERE "+KEY_HOUR+" = "+ hour+"",null);
        cursor.moveToFirst();
        String data = "";
        if(cursor.getCount() >0) {
            data = cursor.getString(cursor.getColumnIndex(KEY_TEMPERATURE));
        }
        cursor.close();
        return data;
    }
    public ArrayList<String>getAllRecords(){
        ArrayList<String> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " +TABLE_NAME+" ORDER BY "+KEY_HOUR+" ASC",null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            arrayList.add(cursor.getString(cursor.getColumnIndex(KEY_HOUR))+"/"+cursor.getString(cursor.getColumnIndex(KEY_TEMPERATURE)));
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;
    }
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }
}
