package com.bawp.coachme.model;

public class Trainer {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private double latitudeCoord;
    private double longitudeCoord;
    private double radius;
    private double flatPrice;
    private String phoneNumber;
    private String address;
    private String trainerProfileImage;
    private double rating;

    public Trainer(){

    }
    public Trainer(String id, String firstName, String lastName, String email, double latitudeCoord, double longitudeCoord, double radius, double flatPrice, String phoneNumber, String address, String trainerProfileImage) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.latitudeCoord = latitudeCoord;
        this.longitudeCoord = longitudeCoord;
        this.radius = radius;
        this.flatPrice = flatPrice;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.trainerProfileImage = trainerProfileImage;
    }

    public Trainer(String id, String firstName, String lastName, String email, double latitudeCoord, double longitudeCoord, double radius, double flatPrice, String phoneNumber, String address, String trainerProfileImage, double rating) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.latitudeCoord = latitudeCoord;
        this.longitudeCoord = longitudeCoord;
        this.radius = radius;
        this.flatPrice = flatPrice;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.trainerProfileImage = trainerProfileImage;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTrainerProfileImage() {
        return trainerProfileImage;
    }

    public void setTrainerProfileImage(String trainerProfileImage) {
        this.trainerProfileImage = trainerProfileImage;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
