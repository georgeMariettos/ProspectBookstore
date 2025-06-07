package com.unipi.prospect.users;

public abstract class User {
    private String username;
    private String password;
    private String name;
    private String surname;
    private boolean active;

    public User(String username, String password, String name, String surname){
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.active = true;
    }

    public  User(String username, String password, String name, String surname, boolean active){
        this(username, password, name, surname);
        this.active = active;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public String getSurname() {
        return surname;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void ChangeAccountDetails(){

    }
    public void DeactiveAccount(){

    }
    public void LeaveComment(){

    }
    public void Logout(){

    }
}
