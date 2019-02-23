package com.example.mq661.govproject.Login_Register;

public class bookroomInfoAdapter {
    private String BuildingNumber;
    private String RoomNumber;
    private String Time;
    private String nowTime;
    private String Days;

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

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getNowTime() {
        return nowTime;
    }

    public void setNowTime(String nowTime) {
        this.nowTime = nowTime;
    }

    public String getDays() {
        return Days;
    }

    public void setDays(String days) {
        Days = days;
    }
}
