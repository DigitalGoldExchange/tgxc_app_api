package com.dgex.backend.service;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.dgex.backend.config.JwtTokenProvider;
import com.dgex.backend.entity.*;
import com.dgex.backend.repository.*;
import com.dgex.backend.service.common.FileManageService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ExchangeRepository exchangeRepository;
    private final FileManageService fileManageService;
    private final UserPassportImageRepository userPassportImageRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PushInfoRepository pushInfoRepository;
    private final DepositAccountRepository depositAccountRepository;

    @Autowired
    private JavaMailSender mailSender;

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static final String CD = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static SecureRandom rnd = new SecureRandom();

    String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    String randomString1(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(CD.charAt(rnd.nextInt(CD.length())));
        return sb.toString();
    }

    String randomIdentifyNumber(String key){
        StringBuilder sb = new StringBuilder(16);
        for(int i = 0; i < 16; i++){
            sb.append(key.charAt(rnd.nextInt(16)));
        }
        return sb.toString();
    }

    @Async
    public Object sendSignKey(String emailId, String signKey){
        Map<String, Object> result = new HashMap<>();

        MimeMessage msg = mailSender.createMimeMessage();
        try{
            msg.setSubject("회원가입 이메일 인증 메일입니다.");
            msg.setText(new StringBuffer().append("<h1>[이메일 인증]</h1>")
                    .append("<p>아래 링크를 클릭하시면 이메일 인증이 완료됩니다.</p>")
                    .append("<a href='http://117.52.98.39:8093/user/signUpConfirm?email=")
                    .append(emailId)
                    .append("&authKey=")
                    .append(signKey)
                    .append("'>이메일 인증 확인</a>")
                    .toString(),"utf-8","html");
            msg.setRecipients(MimeMessage.RecipientType.TO, emailId);
        }catch (MessagingException e){
            result.put("result",false);
        }
        try{
            mailSender.send(msg);
            result.put("result",true);
        }catch (MailException e){
            e.printStackTrace();
            result.put("result",false);
        }
        return result;

    }

    @Transactional
    public boolean signUpConfirm(String emailId, String signKey){
        Map<String, Object> result = new HashMap<>();

        User user = userRepository.findByDeleteDatetimeIsNullAndEmailIdAndSignKey(emailId, signKey);

        if(user != null){
            if(user.getKoreanYn().equals("Y")){
                user.setStatus(2);
            }else{
                user.setStatus(4);
            }
            user.setUpdateDatetime(new Date());
            userRepository.save(user);
            return true;
        }else{
            result.put("msg","회원 정보를 찾을 수 없습니다.");
            result.put("result",false);
            return false;
        }

    }

    @Transactional
    public Map<String,Object> insert(User user, MultipartFile profileImage) {
        Map<String,Object> result = new HashMap<>();
        if(user.getEmailId()=="" || user.getPassword()=="" /*|| user.getName()=="" || user.getPhoneNumber()=="" */){
            result.put("code", "0000");
            result.put("msg", "필수값 누락으로 회원가입 실패");
            return result;
        }else if(userRepository.findByDeleteDatetimeIsNullAndEmailId(user.getEmailId())!=null){
            result.put("code", "0000");
            result.put("msg", "이미 가입된 아이디입니다");
            return result;
        }else{
            BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
            String newPw = randomString(8);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");

            Date time = new Date();
            String timeString = dateFormat.format(time);

            String key = timeString + randomString1(4);

            user.setIdentifyNumber(randomIdentifyNumber(key));
            user.setEmailId(user.getEmailId());
            user.setAddress(user.getAddress());
            user.setAddressDetail(user.getAddressDetail());
            user.setZipCode(user.getZipCode());
            user.setName(user.getName());
            user.setPhoneNumber(user.getPhoneNumber());
            user.setCreateDatetime(new Date());
            user.setPassword(pe.encode(user.getPassword()));
            user.setLevel("USER");
            user.setTotalTg(0.0);
            user.setStatus(0);
            user.setDeviceToken(user.getDeviceToken());
            user.setSignKey(newPw);
            User newUser = userRepository.save(user);

            if(profileImage != null){
                UserPassportImage up = new UserPassportImage();
                up.setProfileImagePath(fileManageService.storeFile(profileImage));
                up.setCreateDatetime(new Date());
                up.setUser(newUser);
                userPassportImageRepository.save(up);
            }

            result.put("user", newUser);
            result.put("code","0001");

            return result;
        }
    }
//16개 영문숫자 섞어서
    @Transactional
    public Map<String,Object> insertAdmin(String name,String emailId,String password,String menuLevel) {
        Map<String,Object> result = new HashMap<>();
        if(name=="" || emailId=="" || password=="" || menuLevel==""){
            result.put("code", "0000");
            result.put("msg", "필수값 누락으로 회원가입 실패");
            return result;
        }else if(userRepository.findByDeleteDatetimeIsNullAndEmailId(emailId)!=null){
            result.put("code", "0000");
            result.put("msg", "이미 가입된 이메일입니다");
            return result;
        }else{
            BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
            User user = new User();
            user.setName(name);
            user.setEmailId(emailId);
            user.setCreateDatetime(new Date());
            user.setPassword(pe.encode(password));
            user.setLevel("MANAGE");
            user.setMenuLevel(menuLevel);
            user.setTotalTg(0.0);
            userRepository.save(user);
            result.put("code","0001");
            return result;
        }
    }

    @Transactional
    public Object getList(Integer page, Integer searchKey,  String searchWord) {
        Map<String, Object> result = new HashMap<>();
        List<User> list;
        Integer totalPages;

        if(searchKey == null){
            searchKey = 0;
        }

        Sort sort = Sort.by(Sort.Direction.DESC,"createDatetime");

        if(searchKey == 1 && searchWord!=null){   //이메일 검색
//            Page<User> pageList = userRepository.findByEmailIdAndDeleteDatetimeIsNull("%"+searchWord+"%", PageRequest.of(page-1,10, sort));
            Page<User> pageList = userRepository.findByEmailId("%"+searchWord+"%", PageRequest.of(page-1,10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();

        }else if(searchKey == 2 && searchWord!=null){  //이름 검색
//            Page<User> pageList = userRepository.findByNameAndDeleteDatetimeIsNull("%"+searchWord+"%", PageRequest.of(page-1,10, sort));
            Page<User> pageList = userRepository.findByName("%"+searchWord+"%", PageRequest.of(page-1,10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();
        }else if(searchKey == 3 && searchWord!=null){
//            Page<User> pageList = userRepository.findByPhoneNumberAndDeleteDatetimeIsNull("%"+searchWord+"%", PageRequest.of(page-1,10, sort));
            Page<User> pageList = userRepository.findByPhoneNumber("%"+searchWord+"%", PageRequest.of(page-1,10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();
        }else{
//            Page<User> pageList = userRepository.findByDeleteDatetimeIsNullAndLevel("USER", PageRequest.of(page-1, 10, sort));
            Page<User> pageList = userRepository.findByLevel("USER", PageRequest.of(page-1, 10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();
        }
        result.put("list",list);
        result.put("totalPages",totalPages);

        return result;
    }

    @Transactional
    public Object getManageList(Integer page) {
        Map<String, Object> result = new HashMap<>();
        List<User> list;
        Integer totalPages;

        Sort sort = Sort.by(Sort.Direction.DESC,"createDatetime");

        Page<User> pageList = userRepository.findByDeleteDatetimeIsNullAndLevel("MANAGE",PageRequest.of(page-1, 10, sort));
        list = pageList.toList();
        totalPages = pageList.getTotalPages();

        result.put("list",list);
        result.put("totalPages",totalPages);

        return result;
    }

    @Transactional
    public void update(Integer userId, Integer status) {
        User user = userRepository.findById(userId).get();
        user.setStatus(status);
        if(status == 3){
            user.setDeleteDatetime(new Date());
        }
        user.setUpdateDatetime(new Date());
        userRepository.save(user);
    }

    @Transactional
    public void userOtpInit(Integer userId) {
        User user = userRepository.findById(userId).get();
        user.setOtpKey(null);
        user.setUpdateDatetime(new Date());
        userRepository.save(user);
    }



    @Transactional
    public void updateUser(Integer userId,String address,String addressDetail,String phoneNumber, String password, String zipCode) {
        User user = userRepository.findById(userId).get();
        if(address != null){
            user.setAddress(address);
        }
        if(addressDetail != null){
            user.setAddressDetail(addressDetail);
        }
        if(phoneNumber != null){
            user.setPhoneNumber(phoneNumber);
        }
        if(zipCode != null){
            user.setZipCode(zipCode);
        }
        if(password != null) {
            BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
            user.setPassword(pe.encode(password));
        }

        user.setUpdateDatetime(new Date());
        userRepository.save(user);
    }

    @Transactional
    public void delete(Integer userId) {
        User user = userRepository.findById(userId).get();
        user.setDeleteDatetime(new Date());
        userRepository.save(user);
    }

    @Transactional
    public Object findByUserInfo(Integer userId) {
        User user = userRepository.findById(userId).get();
        List<Exchange> exchangeList = exchangeRepository.findByDeleteDatetimeIsNullAndUserOrderByCreateDatetimeDesc(user);
        UserPassportImage userPassportImage = userPassportImageRepository.findByDeleteDatetimeIsNullAndUser(user);
        Integer unreadPushCount = pushInfoRepository.countByDeleteDatetimeIsNullAndUserAndReadYn(user, "N");
        Map<String, Object> result = new HashMap<>();

        result.put("exchangeList", exchangeList);
        result.put("userPassportImage", userPassportImage);
        result.put("user",user);
        if(unreadPushCount > 0 ){
            result.put("unreadPushCount", true);
        }else{
            result.put("unreadPushCount", false);
        }


        return result;
    }

    @Transactional
    public Object depositInfo(Integer userId) {
        User user = userRepository.findById(userId).get();
        Integer unreadPushCount = pushInfoRepository.countByDeleteDatetimeIsNullAndUserAndReadYn(user, "N");
        DepositAccount depositAccount = depositAccountRepository.findByDeleteDatetimeIsNull();
        Map<String, Object> result = new HashMap<>();

        result.put("user",user);
        result.put("depositAccount",depositAccount);
        if(unreadPushCount > 0 ){
            result.put("unreadPushCount", true);
        }else{
            result.put("unreadPushCount", false);
        }


        return result;
    }


    @Transactional
    public Map<String, Object>  userInfo(String identifyNumber, String token) throws SignatureException {
        User user = userRepository.findByDeleteDatetimeIsNullAndIdentifyNumber(identifyNumber);
        Map<String, Object> result = new HashMap<>();
        try{
            if (token != null && jwtTokenProvider.validateToken(token)) {
//            Authentication auth = jwtTokenProvider.getAuthentication(token);
//            SecurityContextHolder.getContext().setAuthentication(auth);
                if(user != null){
                    result.put("result", true);
                    result.put("code","0000");
                    result.put("msg","정상 처리");
                }else{
                    result.put("result", false);
                    result.put("code","0001");
                    result.put("msg","등록된 회원이 없습니다.");
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
    public Map<String, Object> refreshToken(String token) throws SignatureException {
        Map<String, Object> result = new HashMap<>();

        try{
            if (token != null && jwtTokenProvider.validateToken(token)) {
//            Authentication auth = jwtTokenProvider.getAuthentication(token);
//            SecurityContextHolder.getContext().setAuthentication(auth);
                result.put("result", true);
                result.put("msg", "토큰이 유효합니다.");
            }else{
                User user = jwtTokenProvider.getAuthUserInfo(token);
                result.put("refreshToken",jwtTokenProvider.createRefreshToken(String.valueOf(user.getUserId())));
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
    public Object findByEmailId(String emailId) {
        User user = userRepository.findByDeleteDatetimeIsNullAndEmailId(emailId);
//        List<Exchange> exchangeList = exchangeRepository.findByDeleteDatetimeIsNullAndUser(user);

        Map<String, Object> result = new HashMap<>();

//        result.put("exchangeList", exchangeList);
        if(user == null){
            result.put("result", false);
        }else{
            result.put("result", true);
            result.put("user",user);
        }


        return result;
    }

    @Transactional
    public Object loginCheck(String emailId, String password, String deviceToken, String deviceType, String role) {
        Map<String, Object> result = new HashMap<>();
        User user = null;
        if("admin".equals(role)){
            user = userRepository.findByDeleteDatetimeIsNullAndEmailIdAndLevelIsNot(emailId, "USER");
        }else{
            user = userRepository.findByDeleteDatetimeIsNullAndEmailId(emailId);
        }

        if(user == null){
            result.put("result", false);
        }else{
            user.setDeviceToken(deviceToken);
            user.setDeviceType(deviceType);
            user.setUpdateDatetime(new Date());
            userRepository.save(user);

            List<Exchange> exchangeList = exchangeRepository.findByDeleteDatetimeIsNullAndUserOrderByCreateDatetimeDesc(user);

            BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
            if(pe.matches(password, user.getPassword())){
                result.put("result", true);
                result.put("user", user);
                result.put("token", jwtTokenProvider.createToken(String.valueOf(user.getUserId())));
                if(exchangeList != null){
                    result.put("exchangeList", exchangeList);
                }else{
                    result.put("exchangeList", false);
                }

            }else{
                result.put("result", false);
            }
        }

        return result;
    }

    @Transactional
    public Object passwordCheck(Integer userId, String password) {
        Map<String, Object> result = new HashMap<>();
        User user = userRepository.findById(userId).get();

        if(user == null){
            result.put("result", false);
        }else{
            BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
            if(pe.matches(password, user.getPassword())){
                result.put("result", true);
                result.put("user", user);
            }else{
                result.put("result", false);
            }
        }

        return result;
    }

    @Transactional
    public Object updateAdmin(Integer userId, String name, String emailId, String password) {
        Map<String, Object> result = new HashMap<>();
        User user = userRepository.findById(userId).get();

        if(user == null){
            result.put("result", false);
        }else{
            BCryptPasswordEncoder pe = new BCryptPasswordEncoder();

            user.setName(name);
            user.setEmailId(emailId);
            user.setPassword(pe.encode(password));
            user.setUpdateDatetime(new Date());
            userRepository.save(user);
            result.put("result", true);
        }

        return result;
    }

    @Transactional
    public Object resetPassword(Integer userId, String password) {
        Map<String, Object> result = new HashMap<>();
        User user = userRepository.findById(userId).get();

        if(user == null){
            result.put("result", false);
        }else{
            BCryptPasswordEncoder pe = new BCryptPasswordEncoder();

            user.setPassword(pe.encode(password));
            user.setUpdateDatetime(new Date());
            userRepository.save(user);
            result.put("result", true);
        }

        return result;
    }

    @Transactional
    public Object updateLevel(String level) {
        Map<String, Object> result = new HashMap<>();

        String[] levelArray = level.split(";");
        for(int i = 0; i < levelArray.length; i++){
            String[] userLevel = levelArray[i].split("\\|");
            User user = userRepository.findById(Integer.parseInt(userLevel[0])).get();
            if(userLevel.length > 1){
                user.setMenuLevel(userLevel[1]);
            }else{
                user.setMenuLevel("");
            }
            userRepository.save(user);
            result.put("result", true);
        }
//        User user = userRepository.findById(userId).get();

        return result;
    }

    @Async
    public Object sendEmail(String name, String emailId) {
        Map<String, Object> result = new HashMap<>();

        User user = userRepository.findByDeleteDatetimeIsNullAndNameAndEmailId(name, emailId);

        if(user != null){

            String newPw = randomString(8);
            BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
            user.setPassword(pe.encode(newPw));
            user.setUpdateDatetime(new Date());
            userRepository.save(user);

            MimeMessage msg = mailSender.createMimeMessage();
            try{
                msg.setSubject(user.getName()+"님 비밀번호 찾기 메일입니다.");
                msg.setText("비밀번호는 "+newPw+" 입니다.");
                msg.setRecipients(MimeMessage.RecipientType.TO, user.getEmailId());
            }catch (MessagingException e){
                result.put("result",false);
                result.put("msg","메일 전송에 실패했습니다.");
                e.printStackTrace();
            }
            try{
                mailSender.send(msg);
                result.put("result",true);
            }catch (MailException e){
                e.printStackTrace();
                result.put("result",false);
                result.put("msg","메일 전송에 실패했습니다.");
            }

        }else{
            result.put("msg","회원 정보를 찾을 수 없습니다.");
            result.put("result",false);
        }

        return result;
    }


    @Transactional
    public Object getOtpCode() {
        Map<String, Object> result = new HashMap<>();

        byte[] buffer = new byte[5 + 5 * 5];
        new Random().nextBytes(buffer);
        Base32 codec = new Base32();
        byte[] secretKey = Arrays.copyOf(buffer, 10);
        byte[] bEncodeKey = codec.encode(secretKey);

        String encodedKey = new String(bEncodeKey);
        result.put("encodedKey", encodedKey);

        return result;
    }

    @Transactional
    public Object checkCode(String userCode, String otpKey) throws NoSuchAlgorithmException, InvalidKeyException {

        long otpNum = Integer.parseInt(userCode);
        long wave = new Date().getTime()/30000;

        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(otpKey);
        int window = 3;
//        boolean result = false;
        //사용자와 서버의 시간 차이가 발생할 수 있어 플러스 마이너스 1분 30초 여유를 둔다.
//        for(int i = -window; i <= window; i++){
//            long hash = verify_code(decodedKey, wave + i);
            long hash = verify_code(decodedKey, wave );
            if(hash == otpNum){
//                result = true;
                return true;
            }
//        }

        return false;
    }

    private static int verify_code(byte[] key, long t) throws NoSuchAlgorithmException, InvalidKeyException{
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }

        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);

        int offset = hash[20 - 1] & 0xF;

        // We're using a long because Java hasn't got unsigned int.
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            // We are dealing with signed bytes:
            // we just keep the first byte.
            truncatedHash |= (hash[offset + i] & 0xFF);
        }

        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;

        return (int) truncatedHash;
    }

    @Transactional
    public void updateOtpKey(Integer userId, String otpKey) {
        User user = userRepository.findById(userId).get();
        user.setOtpKey(otpKey);
        user.setUpdateDatetime(new Date());
        userRepository.save(user);
    }



    @Transactional
    public Object confirmOtp(String userCode, Integer userId) throws NoSuchAlgorithmException, InvalidKeyException {

        User user = userRepository.findById(userId).get();

        long otpNum = Integer.parseInt(userCode);
        long wave = new Date().getTime()/30000;

        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(user.getOtpKey());
        int window = 3;
//        boolean result = false;
        //사용자와 서버의 시간 차이가 발생할 수 있어 플러스 마이너스 1분 30초 여유를 둔다.
//        for(int i = -window; i <= window; i++){
//            long hash = verify_code(decodedKey, wave + i);
        long hash = verify_code(decodedKey, wave );
        if(hash == otpNum){
//                result = true;
            return true;
        }
//        }

        return false;
    }




}
