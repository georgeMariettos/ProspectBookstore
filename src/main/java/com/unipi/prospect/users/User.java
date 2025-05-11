package com.unipi.prospect.users;

public abstract class User {
    private String username;
    private String password;
    private String name;
    private boolean active;

    public User(String username, String password, String name){
        this.username = username;
        this.password = password;
        this.name = name;
        this.active = true;
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
