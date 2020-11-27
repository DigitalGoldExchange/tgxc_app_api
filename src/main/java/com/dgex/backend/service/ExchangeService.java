package com.dgex.backend.service;

import com.dgex.backend.config.JwtTokenProvider;
import com.dgex.backend.entity.*;
import com.dgex.backend.repository.*;
import com.dgex.backend.service.common.FileManageService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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
    private final PushInfoService pushInfoService;
    private final ExchangeRateRepository exchangeRateRepository;

    static SecureRandom rnd = new SecureRandom();

    String randomReqNumber(String key){
        StringBuilder sb = new StringBuilder(5);
        for(int i = 0; i < 5; i++){
            sb.append(key.charAt(rnd.nextInt(5)));
        }
        return sb.toString();
    }

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
            Page<Exchange> pageList = exchangeRepository.findByEmailIdAndDeleteDatetimeIsNullAndTradeType("%"+searchWord+"%","EXCHANGE", PageRequest.of(page-1,10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();

        }else if(searchKey == 2 && searchWord!=null){  //이름 검색
            Page<Exchange> pageList = exchangeRepository.findByNameAndDeleteDatetimeIsNullAndTradeType("%"+searchWord+"%","EXCHANGE", PageRequest.of(page-1,10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();
        }else if(searchKey == 3 && searchWord!=null){ //신청번호 검색
            Page<Exchange> pageList = exchangeRepository.findByReqNumberAndDeleteDatetimeIsNullAndTradeType("%"+searchWord+"%","EXCHANGE", PageRequest.of(page-1,10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();
        }else if(searchKey == 4 && searchWord!=null){   //진행상황 검색
            Page<Exchange> pageList = exchangeRepository.findByStatusAndDeleteDatetimeIsNullAndTradeType("%"+searchWord+"%", "EXCHANGE", PageRequest.of(page-1,10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();
        }else{
            Page<Exchange> pageList = exchangeRepository.findByDeleteDatetimeIsNullAndTradeType("EXCHANGE",PageRequest.of(page-1, 10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();
        }

        result.put("list",list);
        result.put("totalPages",totalPages);

        return result;
    }

    @Transactional
    public Object getMemberConfirmList(String reqNumber) {

        Exchange exchange = exchangeRepository.findByDeleteDatetimeIsNullAndReqNumber(reqNumber);

        UserExchangeImage userExchangeImages = userExchangeImageRepository.findByDeleteDatetimeIsNullAndExchange(exchange);

        Map<String, Object> result = new HashMap<>();

        result.put("exchangeInfo", exchange);
        result.put("userExchangeImages", userExchangeImages);
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
    public Object update(Integer exchangeId, String status, String note) {
        Map<String, Object> result = new HashMap<>();
        Exchange exchange = exchangeRepository.findById(exchangeId).get();


        User user = exchange.getUser();
        if(note != null){
            exchange.setNote(note);
        }else{
            exchange.setNote("");
        }

        if("취소".equals(status) || "반려".equals(status)){

            if(exchange.getStatus().equals("취소") || exchange.getStatus().equals("반려")){
                result.put("result",false);
                result.put("msg","이미 취소되었습니다.");
                return result;
            }else{

                BigDecimal totalTg = new BigDecimal(user.getTotalTg());
                BigDecimal tg = new BigDecimal(exchange.getAmount());
                user.setTotalTg(totalTg.add(tg).stripTrailingZeros().toPlainString());
                userRepository.save(user);


            }
        }

        //최초 신청 날짜
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String tradeTime = dateFormat.format(exchange.getCreateDatetime());
        String tradeType = null;
        String pushStatus = null;
        if(user.getKoreanYn().equals("Y")){
            if(exchange.getTradeType().equals("OUT")){
                tradeType = "출금";
            }else if(exchange.getTradeType().equals("EXCHANGE")){
                tradeType = "교환 신청";
            }
        }else{
            if(exchange.getTradeType().equals("OUT")){
                tradeType = "Withdraw";
            }else if(exchange.getTradeType().equals("EXCHANGE")){
                tradeType = "TG Exchange";
            }

            if(status.equals("완료")){
                pushStatus = "completed";
            }else if(status.equals("승인")){
                pushStatus = "approved";
            }else if(status.equals("반려")){
                pushStatus = "rejected";
            }else if(status.equals("취소")){
                pushStatus = "cancelled";
            }

        }

        if("A".equals(user.getPushType()) || "M".equals(user.getPushType()) ){
            String title = null;
            String content = null;

            if(user.getKoreanYn().equals("Y")){
                title = "[알림] "+tradeType+"이 "+status+" 되었습니다.";
                content = "안녕하세요."+user.getName()+"님, "+tradeTime+"에 신청하신 "+exchange.getAmount()+"TG "+tradeType+"이 "+status+"되었습니다.";
            }else{
                title = "[Alert] "+tradeType+" Request has been "+pushStatus;
                content = "Hi "+user.getName()+", You request for "+tradeType+"with "+exchange.getAmount()+ " on "+tradeTime+" has been "+pushStatus+".";
            }

            pushInfoService.sendPush(user.getUserId(), title, content);
        }

        exchange.setStatus(status);
        exchange.setUpdateDatetime(new Date());
        exchangeRepository.save(exchange);
        result.put("result",true);
        return result;
    }

    @Transactional
    public Object userCancel(Integer exchangeId, String status) {
        Map<String, Object> result = new HashMap<>();
        Exchange exchange = exchangeRepository.findById(exchangeId).get();


        User user = exchange.getUser();

        if("취소".equals(status) || "반려".equals(status)){

            if(exchange.getStatus().equals("취소")){
                result.put("result",false);
                result.put("msg","이미 취소된 내역입니다.");
                return result;
            }else if(exchange.getStatus().equals("반려")){
                result.put("result",false);
                result.put("msg","이미 반려된 내역입니다.");
                return result;
            }else if(exchange.getStatus().equals("완료")){
                result.put("result",false);
                result.put("msg","이미 완료된 내역입니다.");
                return result;
            }else if(exchange.getStatus().equals("승인")){
                result.put("result",false);
                result.put("msg","이미 승인된 내역입니다.");
                return result;
            }else{

                BigDecimal totalTg = new BigDecimal(user.getTotalTg());
                BigDecimal tg = new BigDecimal(exchange.getAmount());
                user.setTotalTg(totalTg.add(tg).stripTrailingZeros().toPlainString());
                userRepository.save(user);
            }
        }

        //최초 신청 날짜
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String tradeTime = dateFormat.format(exchange.getCreateDatetime());
        String tradeType = null;
        String pushStatus = null;
        if(user.getKoreanYn().equals("Y")){
            if(exchange.getTradeType().equals("OUT")){
                tradeType = "출금";
            }else if(exchange.getTradeType().equals("EXCHANGE")){
                tradeType = "교환 신청";
            }
        }else{
            if(exchange.getTradeType().equals("OUT")){
                tradeType = "Withdraw";
            }else if(exchange.getTradeType().equals("EXCHANGE")){
                tradeType = "TG Exchange";
            }

            if(status.equals("완료")){
                pushStatus = "completed";
            }else if(status.equals("승인")){
                pushStatus = "approved";
            }else if(status.equals("반려")){
                pushStatus = "rejected";
            }else if(status.equals("취소")){
                pushStatus = "cancelled";
            }

        }

        if("A".equals(user.getPushType()) || "M".equals(user.getPushType()) ){
            String title = null;
            String content = null;

            if(user.getKoreanYn().equals("Y")){
                title = "[알림] "+tradeType+"이 "+status+" 되었습니다.";
                content = "안녕하세요."+user.getName()+"님, "+tradeTime+"에 신청하신 "+exchange.getAmount()+"TG "+tradeType+"이 "+status+"되었습니다.";
            }else{
                title = "[Alert] "+tradeType+" Request has been "+pushStatus;
                content = "Hi "+user.getName()+", You request for "+tradeType+"with "+exchange.getAmount()+ " on "+tradeTime+" has been "+pushStatus+".";
            }

            pushInfoService.sendPush(user.getUserId(), title, content);
        }

        exchange.setStatus(status);
        exchange.setUpdateDatetime(new Date());
        exchangeRepository.save(exchange);
        result.put("result",true);
        return result;
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
    public void insertWithdraw(Integer userId, String walletAddr, String sendTg) {
        User user = userRepository.findById(userId).get();

        BigDecimal totalTg = new BigDecimal(user.getTotalTg());
        BigDecimal tg = new BigDecimal(sendTg);

        user.setTotalTg(totalTg.subtract(tg).stripTrailingZeros().toPlainString());
        userRepository.save(user);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");

        Date time = new Date();
        String timeString = dateFormat.format(time);

        String key = timeString + user.getIdentifyNumber();

        Exchange exchange = new Exchange();
        exchange.setCreateDatetime(new Date());
        exchange.setTradeType("OUT");
        exchange.setReqNumber("W"+timeString+randomReqNumber(key));
        exchange.setWalletAddr(walletAddr);
        exchange.setAmount(sendTg);
        exchange.setStatus("신청");
        exchange.setUser(user);
        Exchange newEx = exchangeRepository.save(exchange);

        sendTelegram("W", newEx);

    }

    @Transactional
    public void insertExchange(Integer userId, String walletAddr , String exchangeMethod, String reqAmount, MultipartFile identifyCard,MultipartFile profileImage, Integer exchangeStoreId, String reqType,String reqQty) {

        User user = userRepository.findById(userId).get();
        ExchangeStore exchangeStore = exchangeStoreRepository.findById(exchangeStoreId).get();

        BigDecimal totalTg = new BigDecimal(user.getTotalTg());
        BigDecimal tg = new BigDecimal(reqAmount);

        user.setTotalTg(totalTg.subtract(tg).stripTrailingZeros().toPlainString());
        userRepository.save(user);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");

        Date time = new Date();
        String timeString = dateFormat.format(time);

        String key = timeString + user.getIdentifyNumber();

        Exchange exchange = new Exchange();
        exchange.setCreateDatetime(new Date());
        exchange.setTradeType("EXCHANGE");
        exchange.setWalletAddr(exchangeStore.getStoreName());
        exchange.setAmount(reqAmount);
        exchange.setStatus("신청");
        exchange.setReqNumber("E"+timeString+randomReqNumber(key));
        exchange.setUser(user);
        exchange.setExchangeMethod(exchangeMethod);
        exchange.setExchangeStore(exchangeStore);
        if(reqQty != null){
            exchange.setReqQty(reqQty);
        }else{
            exchange.setReqQty("0");
        }
        if(reqType != null){
            exchange.setReqType(reqType);
        }else {
            exchange.setReqType("0");
        }


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

        sendTelegram("E", newEx);

    }

    @Transactional
    public Map<String,Object> insertBook(String identifyNumber, String txId,String amount,String txidTime, String token ) throws SignatureException
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

                        BigDecimal totalTg = new BigDecimal(user.getTotalTg());
                        BigDecimal tg = new BigDecimal(amount);

                        user.setTotalTg(totalTg.add(tg).toString());
                        userRepository.save(user);

                        result.put("code", "0000");
                        result.put("result", true);
                        result.put("msg", "정상적으로 등록되었습니다.");

                        if("A".equals(user.getPushType()) || "M".equals(user.getPushType()) ){
                            String title = null;
                            String content = null;

                            if(user.getKoreanYn().equals("Y")){
                                title = "[알림] "+amount+"TG 입금이 완료 되었습니다.";
                                content = "안녕하세요. "+user.getName()+"님, "+amount+"TG 입금이 완료 되었습니다.";
                            }else{
                                title = "[Alert] "+amount+"TG has been deposited";
                                content = "Hi "+user.getName()+", "+amount+"TG has been deposited on your account";
                            }

                            pushInfoService.sendPush(user.getUserId(), title, content);
                        }

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


    public void sendTelegram(String type, Exchange exchange){

        String title = null;
        if(type.equals("E")){
            title = "[TGXC 교환 신청]";
        }else{
            title = "[TGXC 출금 신청]";
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일");

        StringBuilder sb = new StringBuilder();
        sb.append(title).append(System.getProperty("line.separator"))
                .append("신청번호 : ").append(exchange.getReqNumber())
                .append(System.getProperty("line.separator"))
                .append("신청수량 : ").append(exchange.getAmount()+"TG")
                .append(System.getProperty("line.separator"))
                .append("신청일 : ").append(format.format(exchange.getCreateDatetime()))
                .append(System.getProperty("line.separator"))
                .append("<a href='https://service.tgxc.net'><b>관리자 바로가기</b></a>")
        ;


        TelegramBot bot = new TelegramBot("1450724655:AAG5z8zd4n4Eyury8dZWavnryVaEK-5NuH0");
        SendMessage request = new SendMessage(-478799146,sb.toString())
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .disableNotification(false);

        SendResponse sendResponse = bot.execute(request);
        boolean ok = sendResponse.isOk();
        Message message = sendResponse.message();


    }

    @Transactional
    public void sendTelegram1(){

        String title = "[TGXC 교환 신청]";


        StringBuilder sb = new StringBuilder();
        sb.append(title).append(System.getProperty("line.separator"))
                .append("신청번호 : ").append("TGXC테스트입니다.")
                .append(System.getProperty("line.separator"))
                .append("신청수량 : ").append("12TG")
                .append(System.getProperty("line.separator"))
                .append("신청일 : ").append("2020/11.27")
                .append(System.getProperty("line.separator"))
                .append("<a href='https://service.tgxc.net'><b>관리자 바로가기</b></a>")
        ;

        TelegramBot bot = new TelegramBot("1450724655:AAG5z8zd4n4Eyury8dZWavnryVaEK-5NuH0");
        SendMessage request = new SendMessage(-478799146,sb.toString())
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .disableNotification(false);

        SendResponse sendResponse = bot.execute(request);
        boolean ok = sendResponse.isOk();
        Message message = sendResponse.message();

    }
}
