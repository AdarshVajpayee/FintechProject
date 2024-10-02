package com.invest.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invest.model.Basket;
import com.invest.model.User;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {
    List<Basket> findByAdvisorId(Long advisorId);
}
