package com.example.lineuponline.Util;

public class Record {
    private String name;
    private String gender;
    private int age;
    private String hospital;
    private String section;
    private String doctor;
    private String date;
    private String status;

    public Record(){
        super();
    }

    public Record(String name, String gender, int age, String hospital, String section, String doctor, String date, String status) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.hospital = hospital;
        this.section = section;
        this.doctor = doctor;
        this.date = date;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }

    public String getHospital() {
        return hospital;
    }

    public String getSection() {
        return section;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
