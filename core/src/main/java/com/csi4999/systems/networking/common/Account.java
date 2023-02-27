package com.csi4999.systems.networking.common;

public class Account {

    public String username;
    public String password;

    public Account() {} // empty constructor for Kryo

    public Account(String username, String password){
        this.username = username;
        this.password = password;
    }
}
