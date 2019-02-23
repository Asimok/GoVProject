package com.example.mq661.govproject.Participants;

public class ParticipantsInfo {

    String name;
    String BuildingNumber;
    String RoomNumber;
    String Days;
    String Time;
    String token;
    String num;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuildingNumber() {
        return BuildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        BuildingNumber = buildingNumber;
    }

    public String getRoomNumber() {
        return RoomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        RoomNumber = roomNumber;
    }

    public String getDays() {
        return Days;
    }

    public void setDays(String days) {
        Days = days;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
