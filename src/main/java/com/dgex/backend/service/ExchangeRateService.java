package com.dgex.backend.service;

import com.dgex.backend.entity.DepositAccount;
import com.dgex.backend.entity.ExchangeMethod;
import com.dgex.backend.entity.ExchangeRate;
import com.dgex.backend.entity.ExchangeStore;
import com.dgex.backend.repository.DepositAccountRepository;
import com.dgex.backend.repository.ExchangeMethodRepository;
import com.dgex.backend.repository.ExchangeRateRepository;
import com.dgex.backend.repository.ExchangeStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeMethodRepository exchangeMethodRepository;
    private final ExchangeStoreRepository exchangeStoreRepository;
    private final DepositAccountRepository depositAccountRepository;

    @Transactional
    public void insert(String exchangeRate, String exchangeGram){

        ExchangeRate rate = new ExchangeRate();
        rate.setCreateDatetime(new Date());
        rate.setExchangeRate(exchangeRate);
        rate.setExchangeGram(exchangeGram);
        exchangeRateRepository.save(rate);
    }

    @Transactional
    public Object getList(){
        Map<String, Object> result = new HashMap<>();

//        List<ExchangeRate> exchangeRate = exchangeRateRepository.findByDeleteDatetimeIsNull();
        ExchangeMethod exchangeMethod = exchangeMethodRepository.findByDeleteDatetimeIsNull();
        List<ExchangeStore> inactiveStoreList = exchangeStoreRepository.findByDeleteDatetimeIsNullAndDispYn("N");
        List<ExchangeStore> activeStoreList = exchangeStoreRepository.findByDeleteDatetimeIsNullAndDispYn("Y");
        DepositAccount depositAccount = depositAccountRepository.findByDeleteDatetimeIsNull();

        result.put("activeStoreList", activeStoreList);
        result.put("inactiveStoreList", inactiveStoreList);
        result.put("exchangeRate", "1.15");
        result.put("exchangeMethod", exchangeMethod.getName());
        result.put("depositAccount", depositAccount);
        return result;
    }

    @Transactional
    public Object getList1(){
        Map<String, Object> result = new HashMap<>();

        List<ExchangeRate> exchangeRate = exchangeRateRepository.findByDeleteDatetimeIsNull();
        ExchangeMethod exchangeMethod = exchangeMethodRepository.findByDeleteDatetimeIsNull();
        List<ExchangeStore> inactiveStoreList = exchangeStoreRepository.findByDeleteDatetimeIsNullAndDispYn("N");
        List<ExchangeStore> activeStoreList = exchangeStoreRepository.findByDeleteDatetimeIsNullAndDispYn("Y");
        DepositAccount depositAccount = depositAccountRepository.findByDeleteDatetimeIsNull();

        result.put("activeStoreList", activeStoreList);
        result.put("inactiveStoreList", inactiveStoreList);
        result.put("exchangeRate", exchangeRate);
        result.put("exchangeMethod", exchangeMethod.getName());
        result.put("depositAccount", depositAccount);
        return result;
    }

    @Transactional
    public Object getOne(Integer exchangeRateId){
        Map<String, Object> result = new HashMap<>();

        ExchangeRate exchangeRate = exchangeRateRepository.findById(exchangeRateId).get();

        result.put("exchangeRate", exchangeRate);
        result.put("result", true);
        return result;
    }

    @Transactional
    public void update(Integer exchangeRateId,String exchangeRate, String exchangeGram){

        ExchangeRate rate = exchangeRateRepository.findById(exchangeRateId).get();

        rate.setExchangeRate(exchangeRate);
        rate.setExchangeGram(exchangeGram);
        rate.setUpdateDatetime(new Date());
        exchangeRateRepository.save(rate);
    }

    @Transactional
    public void delete(String exchangeRateId) {
        String[] storeArray = exchangeRateId.split(",");
        for(int i = 0; i < storeArray.length; i++) {
            ExchangeRate store = exchangeRateRepository.findById(Integer.parseInt(storeArray[i])).get();
            store.setDeleteDatetime(new Date());
            exchangeRateRepository.save(store);
        }
    }
}
