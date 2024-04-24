package com.example.burgernezz.Models;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Cart {

    public String getCartID() {
        return cartID;
    }

    public void setCartID(String cartID) {
        this.cartID = cartID;
    }

    @Exclude
    private String cartID;

    private String product;
    private Integer quantity;
    private Integer price;



    public Cart(){

    }

    public Cart(String product, Integer quantity, Integer price){
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }


    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

}
