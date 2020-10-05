package com.dgex.backend.repository;

import com.dgex.backend.entity.ExchangeMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeMethodRepository extends JpaRepository<ExchangeMethod, Integer> {
    List<ExchangeMethod> findByDeleteDatetimeIsNull();
}
