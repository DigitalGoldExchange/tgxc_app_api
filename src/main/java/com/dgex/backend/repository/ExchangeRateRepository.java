package com.dgex.backend.repository;

import com.dgex.backend.entity.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Integer> {
    ExchangeRate findByDeleteDatetimeIsNull();
}
