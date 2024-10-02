package com.invest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.invest.model.Stock;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {

    // Find stock by ticker symbol
    Optional<Stock> findByTickerSymbol(String tickerSymbol);
}
