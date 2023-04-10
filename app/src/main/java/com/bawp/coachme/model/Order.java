/**
 * Class: Order.java
 *
 * Class associated with the orders made by the users that are going to be paid.
 * This orders could be appointments and self-workout plans
 *
 * Fields:
 * - orderId: id of the order (could be the appointmentId or workoutplanId)
 * - productTitle: title that will be displayed on the screen
 * - productType: type of product (1 -> appointment, 2 -> self-workout)
 * - productDescription: description that will be displayed on the screen
 * - totalPrice: totalPrice of that item (appointment / self-workout)
 * - isSelected: to control the element selected in the order fragment
 *
 * @author Luis Miguel Miranda
 * @version 1.0
 */
package com.bawp.coachme.model;

public class Order {

    private String orderId;
    private String productTitle;
    private int productType; //1: appointment, 2: self-workout
    private String productDescription;
    private double totalPrice;
    private boolean isSelected;

    public Order(){

    }

    public Order(String orderId,String productTitle, int productType, String productDescription, double totalPrice,
                 boolean isSelected){
        this.orderId = orderId;
        this.productTitle = productTitle;
        this.productType = productType;
        this.productDescription = productDescription;
        this.totalPrice = totalPrice;
        this.isSelected = isSelected;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
