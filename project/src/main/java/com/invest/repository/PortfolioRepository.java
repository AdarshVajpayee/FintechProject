package com.invest.repository;

import com.invest.model.Portfolio;
import com.invest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    Portfolio findByInvestor(User investor);

    @Query("SELECT SUM(pb.quantity * b.currentPrice) FROM PortfolioBasket pb JOIN pb.basket b WHERE pb.portfolio.investor = :investor")
    double findCurrentValueByInvestor(User investor);
}
