package com.dgex.backend.service;

import com.dgex.backend.entity.Exchange;
import com.dgex.backend.entity.User;
import com.dgex.backend.repository.ExchangeRepository;
import com.dgex.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;
    private final UserRepository userRepository;

    @Transactional
    public Object getList(Integer page, Integer searchKey,  String searchWord) {
        Map<String, Object> result = new HashMap<>();
        List<Exchange> list;
        Integer totalPages;

        if(searchKey == null){
            searchKey = 0;
        }

        Sort sort = Sort.by(Sort.Direction.DESC,"createDatetime");
        if(searchKey == 1 && searchWord!=null){   //이메일 검색
            Page<Exchange> pageList = exchangeRepository.findByReqNumberAndDeleteDatetimeIsNull("%"+searchWord+"%", PageRequest.of(page-1,10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();

        }else if(searchKey == 2 && searchWord!=null){  //이름 검색
            Page<Exchange> pageList = exchangeRepository.findByEmailIdAndDeleteDatetimeIsNull("%"+searchWord+"%", PageRequest.of(page-1,10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();
        }else if(searchKey == 3 && searchWord!=null){ //신청번호 검색
            Page<Exchange> pageList = exchangeRepository.findByNameAndDeleteDatetimeIsNull("%"+searchWord+"%", PageRequest.of(page-1,10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();
        }else if(searchKey == 4 && searchWord!=null){   //진행상황 검색
            Page<Exchange> pageList = exchangeRepository.findByStatusAndDeleteDatetimeIsNull("%"+searchWord+"%", PageRequest.of(page-1,10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();
        }else{
            Page<Exchange> pageList = exchangeRepository.findByDeleteDatetimeIsNull(PageRequest.of(page-1, 10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();
        }

        result.put("list",list);
        result.put("totalPages",totalPages);

        return result;
    }

    @Transactional
    public void update(Integer exchangeId, String status, String note) {
        Exchange exchange = exchangeRepository.findById(exchangeId).get();
        if(note != null){
            exchange.setNote(note);
        }else{
            exchange.setNote("");
        }
        exchange.setStatus(status);
        exchange.setUpdateDatetime(new Date());
        exchangeRepository.save(exchange);
    }

    @Transactional
    public Object findByExchangeInfo(Integer exchangeId) {
        Exchange exchange = exchangeRepository.findById(exchangeId).get();

        List<Exchange> exchangeList = exchangeRepository.findByDeleteDatetimeIsNullAndUser(exchange.getUser());


        Map<String, Object> result = new HashMap<>();

        result.put("exchangeInfo", exchange);
        result.put("exchangeList", exchangeList);
        return result;
    }

    @Transactional
    public void insert(Exchange exchange) {
        exchange.setCreateDatetime(new Date());
        exchange.setStatus("신청");
        exchangeRepository.save(exchange);
    }

    @Transactional
    public Map<String,Object> insertBook(String identifyNumber, String txId,Double amount,String txidTime ){
        Map<String,Object> result = new HashMap<>();
        User user = userRepository.findByDeleteDatetimeIsNullAndIdentifyNumber(identifyNumber);

        if(user == null){
            result.put("code", "0001");
            result.put("msg", "등록된 회원이 없거나 탈퇴한 회원입니다.");
        }else{
            Exchange exchange = new Exchange();
            exchange.setUser(user);
            exchange.setAmount(amount);
            exchange.setCreateDatetime(new Date());
            exchange.setTxId(txId);
            exchange.setTxIdDatetime(txidTime);
            exchange.setTradeType("IN");

            exchangeRepository.save(exchange);

            result.put("code", "0000");
            result.put("msg", "정상적으로 등록되었습니다.");
        }

        return result;



    }

    @Transactional
    public Object checkBook(String txId, Double amount) {
        Exchange exchange = exchangeRepository.findByDeleteDatetimeIsNullAndTxIdAndAmount(txId, amount);

        Map<String, Object> result = new HashMap<>();

        if(exchange != null){
            result.put("result", true);
        }else{
            result.put("result", false);
        }

        return result;
    }
}
