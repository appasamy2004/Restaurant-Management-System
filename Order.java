package com.restaurant.model;

import java.util.Date;
import java.util.List;

public class Order {
    private int id;
    private String customerName;
    private double totalAmount;
    private Date orderDate;
    private List<OrderItem> items;

    // Constructors, getters, and setters
    public Order() {}

    public Order(int id, String customerName, double totalAmount, Date orderDate) {
        this.id = id;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
    }

    // Getters and setters for all fields
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}