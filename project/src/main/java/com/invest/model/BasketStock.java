package com.invest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
public class BasketStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stock_id")  // Foreign key to the Stock table
    private Stock stock;

    private int quantity;  // Quantity of the stock
    //@JsonIgnore
    private double unitPrice;  // Price of one stock unit when it was added


    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Get the latest price of the stock from the Stock table (instead of using a fixed unit price)
    public double getLatestStockPrice() {
        return this.stock.getClose();  // Retrieves the latest closing price from the Stock table
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }


}
