package com.example.root.temperaturecontroll.Database;


public class Temperature {

    private String databaseId;
    private String room;
    private String date;
    private String time;
    private String temp;
    private float tempFloat;
    private String Controller;

    Temperature(String databaseId, String room, String date, String time, String temp, String controller) {
        this.databaseId = databaseId;
        this.room = room;
        this.date = date;
        this.temp = temp;
        this.time = time;
        this.tempFloat = Float.parseFloat(temp);
        Controller = controller;
    }

    public String getTime() {return time;}
    public String getTemp() {
        return temp;
    }

}
