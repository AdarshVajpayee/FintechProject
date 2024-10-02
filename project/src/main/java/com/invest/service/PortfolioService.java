package com.invest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invest.model.Basket;
import com.invest.model.BasketStock;
import com.invest.model.Portfolio;
import com.invest.model.PortfolioBasket;
import com.invest.model.Stock;
import com.invest.model.Transaction;
import com.invest.model.User;
import com.invest.repository.BasketRepository;
import com.invest.repository.PortfolioRepository;
import com.invest.repository.StockRepository;
import com.invest.repository.TransactionRepository;
import com.invest.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // Investor invests in a basket
    public Portfolio investInBasket(String investorEmail, Long basketId, int quantity) {
        User investor = userRepository.findByEmail(investorEmail)
                .orElseThrow(() -> new RuntimeException("Investor not found"));

        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new RuntimeException("Basket not found"));

        double totalCost = basket.getTotalCost() * quantity;

        if (investor.getWalletBalance() < totalCost) {
            throw new RuntimeException("Insufficient funds in wallet");
        }

        Portfolio portfolio = portfolioRepository.findByInvestor(investor);
        if (portfolio == null) {
            portfolio = new Portfolio();
            portfolio.setInvestor(investor);
        }

        PortfolioBasket portfolioBasket = portfolio.getPortfolioBaskets().stream()
                .filter(pb -> pb.getBasket().getId().equals(basketId))
                .findFirst()
                .orElse(null);

        if (portfolioBasket == null) {
            portfolioBasket = new PortfolioBasket();
            portfolioBasket.setPortfolio(portfolio);
            portfolioBasket.setBasket(basket);
            portfolioBasket.setQuantity(quantity);
            portfolio.getPortfolioBaskets().add(portfolioBasket);
        } else {
            portfolioBasket.setQuantity(portfolioBasket.getQuantity() + quantity);
        }

        investor.setWalletBalance(investor.getWalletBalance() - totalCost);
        portfolio.setTotalInvestment(portfolio.getTotalInvestment() + totalCost);

        // Save the transaction
        Transaction transaction = new Transaction();
        transaction.setInvestor(investor);
        transaction.setBasket(basket);
        transaction.setQuantity(quantity);
        transaction.setTotalAmount(totalCost);
        transaction.setTransactionType("BUY");
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);

        userRepository.save(investor);
        portfolioRepository.save(portfolio);

        return portfolio;
    }

    // Investor sells a basket
    public Portfolio sellBasket(String investorEmail, Long basketId, int quantity) {
        User investor = userRepository.findByEmail(investorEmail)
                .orElseThrow(() -> new RuntimeException("Investor not found"));

        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new RuntimeException("Basket not found"));

        Portfolio portfolio = portfolioRepository.findByInvestor(investor);
        PortfolioBasket portfolioBasket = portfolio.getPortfolioBaskets().stream()
                .filter(pb -> pb.getBasket().getId().equals(basketId))
                .findFirst()
                .orElse(null);

        if (portfolioBasket == null || portfolioBasket.getQuantity() < quantity) {
            throw new RuntimeException("Not enough quantity to sell");
        }

        double updatedBasketPrice = calculateUpdatedBasketPrice(basket); // Use updated stock prices
        double totalValue = updatedBasketPrice * quantity;

        portfolioBasket.setQuantity(portfolioBasket.getQuantity() - quantity);
        portfolio.setTotalInvestment(portfolio.getTotalInvestment() - totalValue);

        if (portfolioBasket.getQuantity() == 0) {
            portfolio.getPortfolioBaskets().remove(portfolioBasket);
        }

        investor.setWalletBalance(investor.getWalletBalance() + totalValue);

        // Save the transaction
        Transaction transaction = new Transaction();
        transaction.setInvestor(investor);
        transaction.setBasket(basket);
        transaction.setQuantity(quantity);
        transaction.setTotalAmount(totalValue);
        transaction.setTransactionType("SELL");
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);

        userRepository.save(investor);
        portfolioRepository.save(portfolio);

        return portfolio;
    }

 // Calculate the updated basket price using current stock prices
    private double calculateUpdatedBasketPrice(Basket basket) {
        double updatedBasketPrice = 0.0;
        
        // Iterate through the list of BasketStock
        for (BasketStock basketStock : basket.getStocks()) {
            Stock stock = stockRepository.findByTickerSymbol(basketStock.getStock().getTickerSymbol())
                    .orElseThrow(() -> new RuntimeException("Stock not found: " + basketStock.getStock().getTickerSymbol()));
            
            // Calculate updated basket price based on the latest stock price and quantity
            updatedBasketPrice += stock.getClose() * basketStock.getQuantity();
        }
        
        return updatedBasketPrice;
    }


 // Get portfolio of investor
    public Portfolio getPortfolio(String investorEmail) {
        User investor = userRepository.findByEmail(investorEmail)
                .orElseThrow(() -> new RuntimeException("Investor not found"));

        Portfolio portfolio = portfolioRepository.findByInvestor(investor);
        
        if (portfolio == null || portfolio.getPortfolioBaskets().isEmpty()) {
            return new Portfolio(); // Return an empty portfolio instead of throwing an exception
        }
        
        return portfolio;
    }


    // Get investment summary of the investor
    public List<Transaction> getInvestmentSummary(String investorEmail) {
        User investor = userRepository.findByEmail(investorEmail)
                .orElseThrow(() -> new RuntimeException("Investor not found"));

        return transactionRepository.findByInvestor(investor);
    }

 // Calculate the portfolio return for a specific investor
    public double calculatePortfolioReturn(String investorEmail) {
        // Fetch the investor
        User investor = userRepository.findByEmail(investorEmail)
                .orElseThrow(() -> new RuntimeException("Investor not found"));

        // Fetch all BUY transactions for the investor
        List<Transaction> buyTransactions = transactionRepository.findByInvestorAndTransactionType(investor, "BUY");

        if (buyTransactions.isEmpty()) {
            throw new RuntimeException("No BUY transactions found for this investor");
        }

        double totalBasketValue = 0.0;
        int totalBasketQuantity = 0;

        // Calculate total value and total quantity of baskets
        for (Transaction transaction : buyTransactions) {
            double pricePerBasket = transaction.getTotalAmount() / transaction.getQuantity();
            totalBasketValue += pricePerBasket * transaction.getQuantity();
            totalBasketQuantity += transaction.getQuantity();
        }

        // Calculate the average purchase price per basket
        double averageBasketPrice = totalBasketValue / totalBasketQuantity;

        // Fetch the current price of the basket (assuming the same basket is bought multiple times)
        Long basketId = buyTransactions.get(0).getBasket().getId(); // Assuming basketId is the same for all transactions
        Basket basket = basketRepository.findById(basketId)
                .orElseThrow(() -> new RuntimeException("Basket not found"));

        double currentPrice = basket.getCurrentPrice();  // Get current price from the basket

        // Calculate the portfolio return
        double portfolioReturn = currentPrice - averageBasketPrice;

        return portfolioReturn;
    }
}

