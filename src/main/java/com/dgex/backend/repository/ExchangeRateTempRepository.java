package com.dgex.backend.repository;

import com.dgex.backend.entity.ExchangeRateTemp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRateTempRepository extends JpaRepository<ExchangeRateTemp, Integer> {
    ExchangeRateTemp findByDeleteDatetimeIsNull();
}
