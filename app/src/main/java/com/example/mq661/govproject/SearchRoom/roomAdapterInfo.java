package com.example.mq661.govproject.SearchRoom;

public class roomAdapterInfo {
    private String BuildingNumber;
    private String RoomNumber;
    private String Time;
    private String Size;
    private String Function;
    private String IsMeeting;

    private String Days;

    @Override
    public String toString() {
        return "roomAdapterInfo{" +
                "BuildingNumber='" + BuildingNumber + '\'' +
                ", RoomNumber='" + RoomNumber + '\'' +
                ", Time='" + Time + '\'' +
                ", Size='" + Size + '\'' +
                ", Function='" + Function + '\'' +
                ", IsMeeting='" + IsMeeting + '\'' +
                ", Days='" + Days + '\'' +
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

    public String getDays() {
        return Days;
    }

    public void setDays(String days) {
        Days = days;
    }


}
