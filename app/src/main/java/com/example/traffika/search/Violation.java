package com.example.traffika.search;

public class Violation {
    String vehicle_plate, date_time, violation_type;
    String violation_id;

    public Violation(){

    }

    public String getId(){
        String id=violation_id;
        return id;
    }
    public String getVehiclePlate(){
        String plate=vehicle_plate;
        return plate;
    }
    public String getDateTime(){
        String time=date_time;
        return time;
    }
    public String getViolationType(){
        String type=violation_type;
        return type;
    }

    public void setId(String vId){
        violation_id=vId;
    }
    public void setVehicle_plate(String vPlate){
        vehicle_plate=vPlate;
    }
    public void setDateTime(String date){
        date_time=date;
    }
    public void setViolationType(String vType){
        violation_type=vType;
    }
}
