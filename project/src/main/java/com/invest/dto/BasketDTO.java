package com.invest.dto;


import java.util.List;

public class BasketDTO {

    private Long id;
    private String basketName;
    private Double currentPrice;
    private List<BasketStockDTO> stocks;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBasketName() {
        return basketName;
    }

    public void setBasketName(String basketName) {
        this.basketName = basketName;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public List<BasketStockDTO> getStocks() {
        return stocks;
    }

    public void setStocks(List<BasketStockDTO> stocks) {
        this.stocks = stocks;
    }
}
