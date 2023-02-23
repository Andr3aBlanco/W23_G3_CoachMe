/**
 * Class: UserSingleton.java
 *
 * Class that will hold the information of the current user, so we can get the userId
 * between fragments/activities.
 *
 * Fields:
 * - instance: instance of the Class
 * - userId: user id of the customer/trainer that comes from Firebase Database
 *
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */

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
