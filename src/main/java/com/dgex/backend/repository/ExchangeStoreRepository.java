package com.dgex.backend.repository;

import com.dgex.backend.entity.ExchangeStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeStoreRepository extends JpaRepository<ExchangeStore, Integer> {
    List<ExchangeStore> findByDeleteDatetimeIsNullAndDispYn(String dispYn);
}
