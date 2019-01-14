package com.example.mq661.govproject.BookRoom;

public class people {
    private String EmployeeNumber,Name,Ministry;

    public String getEmployeeNumber() {
        return EmployeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        EmployeeNumber = employeeNumber;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMinistry() {
        return Ministry;
    }

    public void setMinistry(String ministry) {
        Ministry = ministry;
    }

    @Override
    public String toString() {
        return "people{" +
                "EmployeeNumber='" + EmployeeNumber + '\'' +
                ", Name='" + Name + '\'' +
                ", Ministry='" + Ministry + '\'' +
                '}';
    }
}
