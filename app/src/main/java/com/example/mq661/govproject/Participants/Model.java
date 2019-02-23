package com.example.mq661.govproject.Participants;

/**
 * Created by zhaiydong on 2017/1/9.
 */
public class Model {
    private boolean ischeck;
    private String EmployeeNumber1, Name, Ministry;

    public String getEmployeeNumber1() {
        return EmployeeNumber1;
    }

    public void setEmployeeNumber1(String employeeNumber1) {
        EmployeeNumber1 = employeeNumber1;
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

    public boolean ischeck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

}
