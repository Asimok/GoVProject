package com.example.mq661.govproject.SearchRoom;

import android.widget.TextView;

public class roomAdapterInfo {
    private String  BuildingNumber,RoomNumber,Time,Size,Function,IsMeeting;

    @Override
    public String toString() {
        return "roomAdapterInfo{" +
                "BuildingNumber='" + BuildingNumber + '\'' +
                ", RoomNumber='" + RoomNumber + '\'' +
                ", Time='" + Time + '\'' +
                ", Size='" + Size + '\'' +
                ", Function='" + Function + '\'' +
                ", IsMeeting='" + IsMeeting + '\'' +
                '}';
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

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public String getFunction() {
        return Function;
    }

    public void setFunction(String function) {
        Function = function;
    }

    public String getIsMeeting() {
        return IsMeeting;
    }

    public void setIsMeeting(String isMeeting) {
        IsMeeting = isMeeting;
    }
}
