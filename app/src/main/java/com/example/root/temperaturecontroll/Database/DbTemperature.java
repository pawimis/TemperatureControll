package com.example.root.temperaturecontroll.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.ArrayList;

public class DbTemperature extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "TemperatureDb";
    private static final String TABLE_NAME = "TableTemperature";
    private static final String KEY_ID = "ID";
    private static final String KEY_DATABASE_ID = "DATABASE_ID";
    private static final String KEY_ROOM = "ROOM";
    private static final String KEY_DATE = "DATE";
    private static final String KEY_TIME = "TIME";
    private static final String KEY_TEMP = "TEMP";
    private static final String KEY_CONTROLLER = "CONTROLLER";


    public DbTemperature(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    private void createDatabase(SQLiteDatabase db){
        if(db == null){
            db = this.getReadableDatabase();
        }
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_DATABASE_ID + " INTEGER, "
                + KEY_ROOM + " TEXT, "
                + KEY_DATE + " DATE, "
                + KEY_TIME + " TIME, "
                + KEY_TEMP + " TEXT, "
                + KEY_CONTROLLER + " TEXT);";
        db.execSQL(CREATE_TABLE);

    }
    public void insertRecord(String id , String room, String date,String time, String temperature, String controller){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+KEY_ID+ " FROM "+TABLE_NAME +" WHERE "+KEY_DATABASE_ID+" = " + id +" ",null);
        if(cursor.getCount() > 0)
            return;
        Log.i("DBMusic","insert  " + room + " temperature " + temperature);
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_DATABASE_ID, id);
        contentValues.put(KEY_ROOM, room);
        contentValues.put(KEY_DATE, date);
        contentValues.put(KEY_TIME,time);
        contentValues.put(KEY_TEMP, temperature);
        contentValues.put(KEY_CONTROLLER, controller);
        db.insert(TABLE_NAME, null, contentValues);
        cursor.close();
    }
    public Cursor getData(String id ){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE ID="+id+"",null);
        return cursor;
    }
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }
    public Integer deleteContact(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,"id = ?",new String[]{Integer.toString(id)});
    }

    public ArrayList<Temperature> getAllTemperatures(){
        ArrayList<Temperature> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME +" ORDER BY "+KEY_DATE+" DESC, "+KEY_TIME+" DESC",null);
        Log.i("DBTEMPERATURE","size of records: " + cursor.getCount());
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            arrayList.add(new Temperature(cursor.getString(cursor.getColumnIndex(KEY_DATABASE_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_ROOM)),
                    cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                    cursor.getString(cursor.getColumnIndex(KEY_TIME)),
                    cursor.getString(cursor.getColumnIndex(KEY_TEMP)),
                    cursor.getString(cursor.getColumnIndex(KEY_CONTROLLER))));
            Log.i("TEMPERATURE","Date:"+cursor.getString(cursor.getColumnIndex(KEY_DATE)));
            Log.i("TEMPERATURE","TEMP:"+cursor.getString(cursor.getColumnIndex(KEY_TEMP)));
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;
    }
    public String getMaxDate(){
        Log.i(TABLE_NAME,"getMaxDate");
        SQLiteDatabase db = this.getReadableDatabase();
        String date = "null";
        String time = "null";
        Cursor cursor = db.rawQuery("SELECT " +KEY_DATE +","+KEY_TIME+" FROM "+TABLE_NAME +" ORDER BY "+KEY_DATE+" DESC, "+KEY_TIME+" DESC LIMIT 1",null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
            time = cursor.getString(cursor.getColumnIndex(KEY_TIME));
        }
        cursor.close();
        Log.i("maxDate",date + " " + time);
        return date + " "+ time;
    }
    public String getMinDate(){
        Log.i(TABLE_NAME,"getMinDate");
        SQLiteDatabase db = this.getReadableDatabase();
        String date = "null";
        String time = "null";
        Cursor cursor = db.rawQuery("SELECT " +KEY_DATE +","+KEY_TIME+" FROM "+TABLE_NAME +" ORDER BY "+KEY_DATE+" ASC, "+KEY_TIME+" ASC LIMIT 1",null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
            time = cursor.getString(cursor.getColumnIndex(KEY_TIME));
        }
        cursor.close();
        Log.i("minDate",date + " " + time);
        return date + " "+ time;
    }
    public void getAllDates(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT "+KEY_DATE+" FROM "+TABLE_NAME +"",null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Log.i("DATE dist", cursor.getString(cursor.getColumnIndex(KEY_DATE)));
            cursor.moveToNext();
        }
    }
    public ArrayList<Temperature> getTemperatureOnDay(String date){
        ArrayList<Temperature> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME +" WHERE "+KEY_DATE+" =  date('"+date+"') ",null);
        Log.i("QUERY","SELECT * FROM "+TABLE_NAME +" WHERE "+KEY_DATE+" = "+date+" ORDER BY "+KEY_TIME+" DESC ");
        Log.i("DBTEMPERATURE on date","size of records: " + cursor.getCount());
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            arrayList.add(new Temperature(cursor.getString(cursor.getColumnIndex(KEY_DATABASE_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_ROOM)),
                    cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                    cursor.getString(cursor.getColumnIndex(KEY_TIME)),
                    cursor.getString(cursor.getColumnIndex(KEY_TEMP)),
                    cursor.getString(cursor.getColumnIndex(KEY_CONTROLLER))));
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;
    }
    public ArrayList<Temperature> getTemperatureInRoom(String room){
        ArrayList<Temperature> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME +" WHERE "+KEY_ROOM+" = "+ room+" ORDER BY "+KEY_DATE+" DESC, "+KEY_TIME+" DESC LIMIT 1",null);
        Log.i("DBTEMPERATURE","size of records: " + cursor.getCount());
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            arrayList.add(new Temperature(cursor.getString(cursor.getColumnIndex(KEY_DATABASE_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_ROOM)),
                    cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                    cursor.getString(cursor.getColumnIndex(KEY_TIME)),
                    cursor.getString(cursor.getColumnIndex(KEY_TEMP)),
                    cursor.getString(cursor.getColumnIndex(KEY_CONTROLLER))));
            cursor.moveToNext();
        }
        cursor.close();
        return arrayList;
    }
    public void removeAll(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_NAME,null,null);
    }

}
