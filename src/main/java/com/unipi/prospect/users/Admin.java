package com.unipi.prospect.users;

public class Admin extends User{
    public Admin(String username, String password, String name, String surname) {
        super(username, password, name, surname);
    }

    public Admin(String username, String password, String name, String surname, boolean active){
        super(username, password, name, surname, active);
    }
}
