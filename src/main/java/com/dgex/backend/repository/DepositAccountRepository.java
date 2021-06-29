package com.dgex.backend.repository;

import com.dgex.backend.entity.DepositAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositAccountRepository extends JpaRepository<DepositAccount, Integer> {

    DepositAccount findByDeleteDatetimeIsNull();
}
