package com.example.lineuponline.Util;

public class User {
    private int id;
    private String name;
    private String pwd;
    private String gender;
    private int age;
    private String tel;

    public User(){
        super();
    }

    public User(String name,String pwd,String tel){
        super();
        this.name = name;
        this.pwd = pwd;
        this.tel = tel;
    }

    public User(String name, String pwd, String gender, int age, String tel) {
        super();
        this.name = name;
        this.pwd = pwd;
        this.gender = gender;
        this.age = age;
        this.tel = tel;
    }

    public User(int id, String name, String pwd, String gender, int age, String tel) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.gender = gender;
        this.age = age;
        this.tel = tel;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPwd() {
        return pwd;
    }

    public String getTel() {
        return tel;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
