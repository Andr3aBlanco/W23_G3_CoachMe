package com.bawp.coachme.utils;

public class UserSingleton {

    private static UserSingleton instance;
    private String userId;

    private UserSingleton(){

    }

    public static UserSingleton getInstance(){
        if (instance == null){
            instance = new UserSingleton();
        }
        return instance;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

}
