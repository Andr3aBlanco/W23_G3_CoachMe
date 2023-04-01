package com.bawp.coachme.utils;

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

import com.bawp.coachme.model.User;

public class UserSingleton {

    private static UserSingleton instance;
    private String userId;
    private String userDeviceToken;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;

    private UserSingleton(){

    }

    public static UserSingleton getInstance(){
        if (instance == null){
            instance = new UserSingleton();
        }
        return instance;
    }

    public UserSingleton(String userId, String userDeviceToken, String firstName, String lastName, String email, String phoneNumber, String address) {
        this.userId = userId;
        this.userDeviceToken = userDeviceToken;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public static void setInstance(UserSingleton instance) {
        UserSingleton.instance = instance;
    }

    public String getUserDeviceToken() {
        return userDeviceToken;
    }

    public void setUserDeviceToken(String userDeviceToken) {
        this.userDeviceToken = userDeviceToken;
    }
}
