package com.invest.repository;

import com.invest.model.Transaction;
import com.invest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Fetch all transactions for a given investor
    List<Transaction> findByInvestor(User investor);

    // Calculate the total amount invested by the investor for 'BUY' transactions
    @Query("SELECT SUM(t.totalAmount) FROM Transaction t WHERE t.investor = :investor AND t.transactionType = 'BUY'")
    double findTotalInvestedByInvestor(User investor);

    // Fetch all 'BUY' transactions for a given investor
    @Query("SELECT t FROM Transaction t WHERE t.investor = :investor AND t.transactionType = 'BUY'")
    List<Transaction> findBuyTransactionsByInvestor(User investor);

    // Calculate the total quantity of baskets purchased by the investor for 'BUY' transactions
    @Query("SELECT SUM(t.quantity) FROM Transaction t WHERE t.investor = :investor AND t.transactionType = 'BUY'")
    int findTotalQuantityBoughtByInvestor(User investor);
    
    // New method: Find transactions by investor and transaction type
    List<Transaction> findByInvestorAndTransactionType(User investor, String transactionType);
}

