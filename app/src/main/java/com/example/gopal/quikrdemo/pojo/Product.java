package com.example.gopal.quikrdemo.pojo;

import java.util.List;


public class Product {
    private String productId;
    private String productName;
    private String productPrice;
    private String productDescription;
    private String productCategory;
    private List<String> productImageList;
    private String sellerName;

    public Product() {

    }

    public Product(String productId, String productName, String productPrice, String productDescription, String productCategory, List<String> productImageList, String sellerName) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productDescription = productDescription;
        this.productCategory = productCategory;
        this.productImageList = productImageList;
        this.sellerName = sellerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public List<String> getProductImageList() {
        return productImageList;
    }

    public void setProductImageList(List<String> productImageList) {
        this.productImageList = productImageList;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }
}
