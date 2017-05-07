package com.example.root.temperaturecontroll.Database;

/**
 * Created by root on 10.12.16.
 */

public class Temperature {
    public Temperature(String databaseId, String room, String date,String time, String temp, String controller) {
        this.databaseId = databaseId;
        this.room = room;
        this.date = date;
        this.temp = temp;
        this.time = time;
        Controller = controller;
    }
    String databaseId;
    String room;
    String date;
    String time;
    String temp;
    String Controller;

    public String getDatabaseId() {
        return databaseId;
    }
    public String getRoom() {
        return room;
    }
    public String getDate() {
        return date;
    }
    public String getTime() {return time;}
    public String getTemp() {
        return temp;
    }
    public String getController() {
        return Controller;
    }
}
