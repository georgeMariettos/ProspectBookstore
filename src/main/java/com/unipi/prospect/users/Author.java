package com.unipi.prospect.users;

public class Author extends User{
    public Author(String username, String password, String name, String surname) {
        super(username, password, name, surname);
    }

    public Author(String username, String password, String name, String surname, boolean active){
        super(username, password, name, surname, active);
    }

    public void ViewWrittenBooks(){
        // Implementation for viewing written books
    }
    public void ViewWrittenBookStats(){

    }
}
