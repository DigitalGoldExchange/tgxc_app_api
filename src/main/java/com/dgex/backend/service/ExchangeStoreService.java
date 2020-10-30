package com.dgex.backend.service;

import com.dgex.backend.entity.Exchange;
import com.dgex.backend.entity.ExchangeMethod;
import com.dgex.backend.entity.ExchangeRate;
import com.dgex.backend.entity.ExchangeStore;
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
public class ExchangeStoreService {

    private final ExchangeStoreRepository exchangeStoreRepository;

    @Transactional
    public void insert(String name,String address,String phoneNumber) {
        ExchangeStore store = new ExchangeStore();
        store.setCreateDatetime(new Date());
        store.setStoreName(name);
        store.setStoreAddr(address);
        store.setStorePhoneNumber(phoneNumber);
        store.setDispYn("N");
        exchangeStoreRepository.save(store);
    }

    @Transactional
    public Object getInactiveStoreList(){
        Map<String, Object> result = new HashMap<>();

        List<ExchangeStore> inactiveStoreList = exchangeStoreRepository.findByDeleteDatetimeIsNullAndDispYn("N");

        result.put("inactiveStoreList", inactiveStoreList);
        return result;
    }

    @Transactional
    public Object getActiveStoreList(){
        Map<String, Object> result = new HashMap<>();

        List<ExchangeStore> activeStoreList = exchangeStoreRepository.findByDeleteDatetimeIsNullAndDispYn("Y");

        result.put("activeStoreList", activeStoreList);
        return result;
    }

    @Transactional
    public void delete(String storeId) {
        String[] storeArray = storeId.split(",");
        for(int i = 0; i < storeArray.length; i++) {
            ExchangeStore store = exchangeStoreRepository.findById(Integer.parseInt(storeArray[i])).get();
            store.setDeleteDatetime(new Date());
            exchangeStoreRepository.save(store);
        }
    }

    @Transactional
    public void inactiveUpdate(String storeId) {
        String[] storeArray = storeId.split(",");
        for(int i = 0; i < storeArray.length; i++) {
            ExchangeStore store = exchangeStoreRepository.findById(Integer.parseInt(storeArray[i])).get();
            store.setDispYn("N");
            store.setUpdateDatetime(new Date());
            exchangeStoreRepository.save(store);
        }
    }

    @Transactional
    public void activeUpdate(String storeId) {

        String[] storeArray = storeId.split(",");
        for(int i = 0; i < storeArray.length; i++) {
            ExchangeStore store = exchangeStoreRepository.findById(Integer.parseInt(storeArray[i])).get();
            store.setDispYn("Y");
            store.setUpdateDatetime(new Date());
            exchangeStoreRepository.save(store);
        }
    }
}
