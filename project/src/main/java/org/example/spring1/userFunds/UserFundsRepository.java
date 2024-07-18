package org.example.spring1.userFunds;

import org.example.spring1.currency.model.CurrencyCode;
import org.example.spring1.userFunds.model.UserFunds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFundsRepository extends JpaRepository<UserFunds, Long> {
    List<UserFunds> findByUserId(Long userId);
    Optional<UserFunds> findByUserIdAndCurrency(Long userId, CurrencyCode currency);
}