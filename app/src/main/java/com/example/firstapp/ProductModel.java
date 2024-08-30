package com.example.firstapp;

import java.util.Objects;

public class ProductModel {
    private String name;
    private String price;
    private String imageRes;
    private String description;
    private String id;
    private int quantity ;

    public ProductModel() {
    }

    public ProductModel(String id, String name, String price,String description, String imageRes, int quantity) {
        this.name = name;
        this.id=id;
        this.price = price;
        this.imageRes = imageRes;
        this.description = description;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getImageRes() {
        return imageRes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImageRes(String imageRes) {
        this.imageRes = imageRes;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ProductModel that = (ProductModel) obj;
        return Objects.equals(id, that.id); // or another unique identifier
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // or another unique identifier
    }

}