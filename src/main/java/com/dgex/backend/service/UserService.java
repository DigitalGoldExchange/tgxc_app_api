package com.dgex.backend.service;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.dgex.backend.entity.Exchange;
import com.dgex.backend.entity.User;
import com.dgex.backend.entity.UserPassportImage;
import com.dgex.backend.repository.ExchangeRepository;
import com.dgex.backend.repository.UserPassportImageRepository;
import com.dgex.backend.repository.UserRepository;
import com.dgex.backend.service.common.FileManageService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ExchangeRepository exchangeRepository;
    private final FileManageService fileManageService;
    private final UserPassportImageRepository userPassportImageRepository;

    @Autowired
    private JavaMailSender mailSender;

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
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
            Page<User> pageList = userRepository.findByEmailIdAndDeleteDatetimeIsNull("%"+searchWord+"%", PageRequest.of(page-1,10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();

        }else if(searchKey == 2 && searchWord!=null){  //이름 검색
            Page<User> pageList = userRepository.findByNameAndDeleteDatetimeIsNull("%"+searchWord+"%", PageRequest.of(page-1,10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();
        }else if(searchKey == 3 && searchWord!=null){
            Page<User> pageList = userRepository.findByPhoneNumberAndDeleteDatetimeIsNull("%"+searchWord+"%", PageRequest.of(page-1,10, sort));
            list = pageList.toList();
            totalPages = pageList.getTotalPages();
        }else{
            Page<User> pageList = userRepository.findByDeleteDatetimeIsNullAndLevel("USER", PageRequest.of(page-1, 10, sort));
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
    public void delete(Integer userId) {
        User user = userRepository.findById(userId).get();
        user.setDeleteDatetime(new Date());
        userRepository.save(user);
    }

    @Transactional
    public Object findByUserInfo(Integer userId) {
        User user = userRepository.findById(userId).get();
        List<Exchange> exchangeList = exchangeRepository.findByDeleteDatetimeIsNullAndUser(user);

        Map<String, Object> result = new HashMap<>();

        result.put("exchangeList", exchangeList);
        result.put("user",user);

        return result;
    }

    @Transactional
    public Object findByEmailId(String emailId) {
        User user = userRepository.findByDeleteDatetimeIsNullAndEmailId(emailId);
        List<Exchange> exchangeList = exchangeRepository.findByDeleteDatetimeIsNullAndUser(user);

        Map<String, Object> result = new HashMap<>();

        result.put("exchangeList", exchangeList);
        result.put("user",user);

        return result;
    }

    @Transactional
    public Object loginCheck(String emailId, String password) {
        Map<String, Object> result = new HashMap<>();
        User user = userRepository.findByDeleteDatetimeIsNullAndEmailId(emailId);

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


}