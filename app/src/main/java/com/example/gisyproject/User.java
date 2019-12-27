package com.example.gisyproject;

public class User {
    public String Username,Email,Password,Gender,Degree,College,Address,ProfileImageuRL;

    public User(){

    }

    public User(String username, String email, String password, String gender, String degree, String college, String address, String profileImageuRL) {
        Username = username;
        Email = email;
        Password = password;
        Gender = gender;
        Degree = degree;
        College = college;
        Address = address;
        ProfileImageuRL = profileImageuRL;
    }
}
