package com.dgex.backend.repository;

import com.dgex.backend.entity.Exchange;
import com.dgex.backend.entity.UserExchangeImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserExchangeImageRepository extends JpaRepository<UserExchangeImage, Integer> {
    UserExchangeImage findByDeleteDatetimeIsNullAndExchange(Exchange exchange);
}
