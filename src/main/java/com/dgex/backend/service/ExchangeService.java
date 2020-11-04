package com.dgex.backend.service;

import com.dgex.backend.config.JwtTokenProvider;
import com.dgex.backend.entity.Exchange;
import com.dgex.backend.entity.ExchangeStore;
import com.dgex.backend.entity.User;
import com.dgex.backend.entity.UserExchangeImage;
import com.dgex.backend.repository.ExchangeRepository;
import com.dgex.backend.repository.ExchangeStoreRepository;
import com.dgex.backend.repository.UserExchangeImageRepository;
import com.dgex.backend.repository.UserRepository;
import com.dgex.backend.service.common.FileManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SignatureException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;
    private final UserRepository userRepository;
    private final FileManageService fileManageService;
    private final UserExchangeImageRepository userExchangeImageRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ExchangeStoreRepository exchangeStoreRepository;

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
    public Object getDepositList(Integer page, Integer searchKey,  String searchWord) {
        Map<String, Object> result = new HashMap<>();
        List<Exchange> list;
        Integer totalPages;

        if(searchKey == null){
            searchKey = 0;
        }

        Sort sort = Sort.by(Sort.Direction.DESC,"createDatetime");
        if(searchKey == 1 && searchWord!=null){   //이메일 검색
            Page<Exchange> pageList = exchangeRepository.findByEmailIdAndDeleteDatetimeIsNullAndTradeType("%"+searchWord+"%","OUT", PageRequest.of(page-1,10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();

        }else if(searchKey == 2 && searchWord!=null){  //이름 검색
            Page<Exchange> pageList = exchangeRepository.findByNameAndDeleteDatetimeIsNullAndTradeType("%"+searchWord+"%","OUT", PageRequest.of(page-1,10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();
        }else if(searchKey == 3 && searchWord!=null){ //신청번호 검색
            Page<Exchange> pageList = exchangeRepository.findByReqNumberAndDeleteDatetimeIsNullAndTradeType("%"+searchWord+"%", "OUT",PageRequest.of(page-1,10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();
        }else if(searchKey == 4 && searchWord!=null){   //진행상황 검색
            Page<Exchange> pageList = exchangeRepository.findByStatusAndDeleteDatetimeIsNullAndTradeType("%"+searchWord+"%", "OUT", PageRequest.of(page-1,10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();
        }else{
            Page<Exchange> pageList = exchangeRepository.findByDeleteDatetimeIsNullAndTradeType("OUT", PageRequest.of(page-1, 10, sort));
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
    public void depositUpdate(Integer exchangeId, String status, String txid) {
        Exchange exchange = exchangeRepository.findById(exchangeId).get();
        exchange.setTxId(txid);
        exchange.setStatus(status);
        exchange.setUpdateDatetime(new Date());
        exchangeRepository.save(exchange);
    }

    @Transactional
    public Object findByExchangeInfo(Integer exchangeId) {
        Exchange exchange = exchangeRepository.findById(exchangeId).get();
        List<Exchange> exchangeList = exchangeRepository.findByDeleteDatetimeIsNullAndUserOrderByCreateDatetimeDesc(exchange.getUser());
        UserExchangeImage userExchangeImages = userExchangeImageRepository.findByDeleteDatetimeIsNullAndExchange(exchange);
        List<Exchange> depositList = exchangeRepository.findByDeleteDatetimeIsNullDeposit("OUT");

        Map<String, Object> result = new HashMap<>();

        result.put("exchangeInfo", exchange);
        result.put("exchangeList", exchangeList);
        result.put("depositList", depositList);
        result.put("userExchangeImages", userExchangeImages);
        return result;
    }

    @Transactional
    public Object findByType(Integer userId, String type) {
        String tradeType = null;
        List<Exchange> exchangeList;
        User user = userRepository.findById(userId).get();

        if("전체내역".equals(type)){
            exchangeList = exchangeRepository.findByDeleteDatetimeIsNullAndUserOrderByCreateDatetimeDesc(user);
        }else{
            if("입금".equals(type)){
                tradeType = "IN";
            }else if("출금".equals(type)){
                tradeType = "OUT";
            }else{
                tradeType = "EXCHANGE";
            }

            exchangeList = exchangeRepository.findByDeleteDatetimeIsNullAndUserAndTradeTypeOrderByCreateDatetimeDesc(user, tradeType);
        }

        Map<String, Object> result = new HashMap<>();

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
    public void insertWithdraw(Integer userId, String walletAddr, Double sendTg) {

        User user = userRepository.findById(userId).get();
        user.setTotalTg(user.getTotalTg() - sendTg);
        userRepository.save(user);

        Exchange exchange = new Exchange();
        exchange.setCreateDatetime(new Date());
        exchange.setTradeType("OUT");
        exchange.setWalletAddr(walletAddr);
        exchange.setAmount(sendTg);
        exchange.setStatus("신청");
        exchange.setUser(user);
        exchangeRepository.save(exchange);
    }

    @Transactional
    public void insertExchange(Integer userId, String walletAddr , String exchangeMethod, Double reqAmount, MultipartFile identifyCard,MultipartFile profileImage, Integer exchangeStoreId) {

        User user = userRepository.findById(userId).get();
        ExchangeStore exchangeStore = exchangeStoreRepository.findById(exchangeStoreId).get();

        user.setTotalTg(user.getTotalTg() - reqAmount);
        userRepository.save(user);

        Exchange exchange = new Exchange();
        exchange.setCreateDatetime(new Date());
        exchange.setTradeType("EXCHANGE");
        exchange.setWalletAddr(exchangeStore.getStoreName());
        exchange.setAmount(reqAmount);
        exchange.setStatus("신청");
        exchange.setUser(user);
        exchange.setExchangeMethod(exchangeMethod);
        exchange.setExchangeStore(exchangeStore);
        Exchange newEx = exchangeRepository.save(exchange);

        if(profileImage != null && identifyCard != null){
            UserExchangeImage userExchangeImage = new UserExchangeImage();
            userExchangeImage.setProfileImagePath(fileManageService.storeFile(profileImage));
            userExchangeImage.setIdentifyCardPath(fileManageService.storeFile(identifyCard));
            userExchangeImage.setCreateDatetime(new Date());
            userExchangeImage.setUser(user);
            userExchangeImage.setExchange(newEx);
            userExchangeImageRepository.save(userExchangeImage);
        }
    }



    @Transactional
    public Map<String,Object> insertBook(String identifyNumber, String txId,Double amount,String txidTime, String token ) throws SignatureException
        {
        Map<String,Object> result = new HashMap<>();

        Exchange oldExchange = exchangeRepository.findByDeleteDatetimeIsNullAndTxId(txId);

        User user = userRepository.findByDeleteDatetimeIsNullAndIdentifyNumber(identifyNumber);

        try{
            if(token != null && jwtTokenProvider.validateToken(token)){

                if(oldExchange == null){
                    if(user == null){
                        result.put("code", "0001");
                        result.put("result", false);
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
                        result.put("result", true);
                        result.put("msg", "정상적으로 등록되었습니다.");
                    }
                }else{
                    result.put("code", "0001");
                    result.put("result", false);
                    result.put("msg", "TXID가 이미 존재합니다.");
                }



            }else{
                result.put("result", false);
                result.put("msg", "토큰이 유효하지 않습니다.");
            }

        }catch (Exception e){
            result.put("result", false);
            result.put("code","0001");
            result.put("msg","token invalid");
        }


        return result;



    }

    @Transactional
    public Object checkBook(String txId, String token) throws SignatureException{
        Exchange exchange = exchangeRepository.findByDeleteDatetimeIsNullAndTxId(txId);
        Map<String, Object> result = new HashMap<>();

        try{
            if(token != null && jwtTokenProvider.validateToken(token)){
                if(exchange != null){
                    result.put("result", true);
                    result.put("msg", "내역이 존재합니다.");
                }else{
                    result.put("result", false);
                    result.put("msg", "내역이 존재하지 않습니다.");
                }
            }else{
                result.put("result", false);
                result.put("msg", "토큰이 유효하지 않습니다.");
            }

        }catch (Exception e){
            result.put("result", false);
            result.put("code","0001");
            result.put("msg","token invalid");
        }


        return result;
    }
}
