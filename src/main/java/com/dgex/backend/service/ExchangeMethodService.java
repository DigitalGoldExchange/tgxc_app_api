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
}
