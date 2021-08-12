package com.example.shopdeck.Model;

public class OrderM {
    public String product_id,product_name,price,date,quantity;

    public OrderM() {
    }

    public OrderM(String product_id, String product_name, String price, String date, String quantity) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.price = price;
        this.date = date;
        this.quantity = quantity;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
