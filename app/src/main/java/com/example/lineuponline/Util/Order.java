package com.example.lineuponline.Util;

public class Order {
    private int id;
    private int position;
    private String hospital;
    private String section;
    private String doctor;
    private String userTel;
    private String reserve_date;

    public Order(){
        super();
    }

    public Order(int id, String doctor, String userTel, String reserve_date) {
        super();
        this.id = id;
        this.doctor = doctor;
        this.userTel = userTel;
        this.reserve_date = reserve_date;
    }

    public Order(int id, int position,String hospital,String section, String doctor, String userTel, String reserve_date) {
        this.id = id;
        this.position = position;
        this.hospital = hospital;
        this.section = section;
        this.doctor = doctor;
        this.userTel = userTel;
        this.reserve_date = reserve_date;
    }

    public String getHospital() {
        return hospital;
    }

    public int getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public String getReserve_date() {
        return reserve_date;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setReserve_date(String reserve_date) {
        this.reserve_date = reserve_date;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }
}
