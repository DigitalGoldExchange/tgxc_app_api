package com.dgex.backend.service;

import com.dgex.backend.entity.ExchangeMethod;
import com.dgex.backend.entity.ExchangeRate;
import com.dgex.backend.repository.ExchangeMethodRepository;
import com.dgex.backend.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeMethodRepository exchangeMethodRepository;

    @Transactional
    public void insert(Double exchangeRate){

        ExchangeRate exrate = exchangeRateRepository.findByDeleteDatetimeIsNull();

        if(exrate != null){
            exrate.setDeleteDatetime(new Date());
            exrate.setUpdateDatetime(new Date());
            exchangeRateRepository.save(exrate);
        }

        ExchangeRate rate = new ExchangeRate();
        rate.setCreateDatetime(new Date());
        rate.setExchangeRate(exchangeRate);
        exchangeRateRepository.save(rate);
    }

    @Transactional
    public Object getList(){
        Map<String, Object> result = new HashMap<>();

        ExchangeRate exchangeRate = exchangeRateRepository.findByDeleteDatetimeIsNull();
        ExchangeMethod exchangeMethod = exchangeMethodRepository.findByDeleteDatetimeIsNull();

        result.put("exchangeRate", exchangeRate);
        result.put("exchangeMethod", exchangeMethod.getName());
        return result;
    }
}
