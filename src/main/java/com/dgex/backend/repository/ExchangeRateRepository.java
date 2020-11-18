package com.dgex.backend.repository;

import com.dgex.backend.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Integer> {
    List<ExchangeRate> findByDeleteDatetimeIsNull();
}
