package com.dgex.backend.service;

import com.dgex.backend.entity.DepositAccount;
import com.dgex.backend.entity.ExchangeMethod;
import com.dgex.backend.entity.ExchangeRate;
import com.dgex.backend.entity.ExchangeStore;
import com.dgex.backend.repository.DepositAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DepositAccountService {

    private final DepositAccountRepository depositAccountRepository;

    @Transactional
    public void insert(String walletAddress){

        DepositAccount depositAccount = depositAccountRepository.findByDeleteDatetimeIsNull();

        if(depositAccount != null){
            depositAccount.setDeleteDatetime(new Date());
            depositAccount.setUpdateDatetime(new Date());
            depositAccountRepository.save(depositAccount);
        }

        DepositAccount account = new DepositAccount();
        account.setCreateDatetime(new Date());
        account.setAccount(walletAddress);
        depositAccountRepository.save(account);
    }

    @Transactional
    public Object getList(){
        Map<String, Object> result = new HashMap<>();

        DepositAccount depositAccount = depositAccountRepository.findByDeleteDatetimeIsNull();

        result.put("depositAccount", depositAccount);
        return result;
    }
}
