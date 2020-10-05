package com.dgex.backend.service;

import com.dgex.backend.entity.ExchangeRate;
import com.dgex.backend.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;


    @Transactional
    public void insert(Double exchangeRate){

        ExchangeRate rate = new ExchangeRate();
        rate.setCreateDatetime(new Date());
        rate.setExchangeRate(exchangeRate);
        exchangeRateRepository.save(rate);
    }
}
