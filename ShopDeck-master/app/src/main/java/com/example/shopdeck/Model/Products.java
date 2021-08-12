package com.example.shopdeck.Model;

public class Products {
    private String product_name,description,category,date,image,price,product_id,time;

    public Products(){

    }

    public Products(String product_name, String description, String category, String date, String image, String price, String product_id, String time) {
        this.product_name = product_name;
        this.description = description;
        this.category = category;
        this.date = date;
        this.image = image;
        this.price = price;
        this.product_id = product_id;
        this.time = time;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
