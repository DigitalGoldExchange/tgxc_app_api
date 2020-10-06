package com.dgex.backend.service;

import com.dgex.backend.entity.ExchangeMethod;
import com.dgex.backend.repository.ExchangeMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ExchangeMethodService {

    private final ExchangeMethodRepository exchangeMethodRepository;

    @Transactional
    public void update(ExchangeMethod exchangeMethod) {

        ExchangeMethod method = exchangeMethodRepository.findById(exchangeMethod.getExchangeMethodId()).get();
        method.setDispYn(exchangeMethod.getDispYn());
        method.setUpdateDatetime(new Date());
        exchangeMethodRepository.save(method);
    }

    @Transactional
    public void insert(String exchangeMethod) {
        ExchangeMethod method = exchangeMethodRepository.findByDeleteDatetimeIsNull();

        if(method == null){
            ExchangeMethod exchangeMethod1 = new ExchangeMethod();
            exchangeMethod1.setName(exchangeMethod);
            exchangeMethod1.setCreateDatetime(new Date());
            exchangeMethodRepository.save(exchangeMethod1);
        }else {
            method.setName(exchangeMethod);
            method.setUpdateDatetime(new Date());
            exchangeMethodRepository.save(method);
        }
    }
}
