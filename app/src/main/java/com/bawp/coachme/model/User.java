/**
 * Class: User.java
 *
 * Class associated with the information of the users (customers/trainers)
 *
 * Fields:
 * - firstName
 * - lastName
 * - email
 * - latitudeCoord: latitude coordinate of the trainer (where normally works)
 * - longitudeCoord: longitude coordinate of the trainer (where normally works)
 * - radius: circled area where the trainer could have training sessions (in km)
 * - flatPrice: flat price per hour of session (trainer)
 * - phoneNumber
 * - address
 * - role: Role object associated to the role of the user (customer/trainer)
 * - serviceTypes: List<String> of types of services a trainer offers (CrossFit, Cycling)
 * - specialConditions: List<String> of special conditions of a customer (specific types of body pain, etc)
 * - stripeCustomerId: For customers, this is the customer Id from Stripe Database
 *
 * @author Luis Miguel Miranda
 * @version 1.0.1
 */
package com.bawp.coachme.model;

import android.app.Service;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

@IgnoreExtraProperties
public class User implements Serializable {

    private String firstName;
    private String lastName;
    private String email;
    private double latitudeCoord;
    private double longitudeCoord;
    private double radius;
    private double flatPrice;
    private String phoneNumber;
    private String address;
    private Role role;

    private List<String> serviceTypes;
    private List<String> specialConditions;
    private String stripeCustomerId;

    public User(){

    }

    public User(String firstName, String lastName, String email, double latitudeCoord, double longitudeCoord,
                double radius, double flatPrice, String phoneNumber, String address, Role role,
                List<String> serviceTypes) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.latitudeCoord = latitudeCoord;
        this.longitudeCoord = longitudeCoord;
        this.radius = radius;
        this.flatPrice = flatPrice;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
        this.serviceTypes = serviceTypes;
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

    public double getLatitudeCoord() {
        return latitudeCoord;
    }

    public void setLatitudeCoord(double latitudeCoord) {
        this.latitudeCoord = latitudeCoord;
    }

    public double getLongitudeCoord() {
        return longitudeCoord;
    }

    public void setLongitudeCoord(double longitudeCoord) {
        this.longitudeCoord = longitudeCoord;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getFlatPrice() {
        return flatPrice;
    }

    public void setFlatPrice(double flatPrice) {
        this.flatPrice = flatPrice;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<String> getServiceTypes() {
        return serviceTypes;
    }

    public void setServiceTypes(List<String> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    public List<String> getSpecialConditions() {
        return specialConditions;
    }

    public void setSpecialConditions(List<String> specialConditions) {
        this.specialConditions = specialConditions;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    @Override
    public String toString(){
        return this.firstName+" "+this.lastName;
    }
}
