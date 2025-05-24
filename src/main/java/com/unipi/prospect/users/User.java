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

    public void ChangeAccountDetails(){

    }
    public void DeactiveAccount(){

    }
    public void LeaveComment(){

    }
    public void Logout(){

    }
}
