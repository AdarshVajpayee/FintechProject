package com.invest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Portfolio {

    // Initialize portfolioBaskets in the constructor
    public Portfolio() {
        this.portfolioBaskets = new ArrayList<>();  // Ensure the list is initialized
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "investor_id")
    @JsonIgnore  // Avoid serialization of investor to break the circular reference
    private User investor;  // The investor who owns this portfolio

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioBasket> portfolioBaskets;

    private Double totalInvestment = 0.0;  // The total amount of money invested in all baskets

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getInvestor() {
        return investor;
    }

    public void setInvestor(User investor) {
        this.investor = investor;
    }

    public List<PortfolioBasket> getPortfolioBaskets() {
        return portfolioBaskets;
    }

    public void setPortfolioBaskets(List<PortfolioBasket> portfolioBaskets) {
        this.portfolioBaskets = portfolioBaskets;
    }

    public Double getTotalInvestment() {
        return totalInvestment;
    }

    public void setTotalInvestment(Double totalInvestment) {
        this.totalInvestment = totalInvestment;
    }

    // Other potential helper methods for managing portfolioBaskets

    public void addPortfolioBasket(PortfolioBasket portfolioBasket) {
        this.portfolioBaskets.add(portfolioBasket);
    }

    public void removePortfolioBasket(PortfolioBasket portfolioBasket) {
        this.portfolioBaskets.remove(portfolioBasket);
    }
}
