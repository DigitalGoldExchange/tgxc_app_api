package com.dgex.backend.service;

import com.dgex.backend.entity.Exchange;
import com.dgex.backend.entity.User;
import com.dgex.backend.repository.ExchangeRepository;
import com.dgex.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ExchangeRepository exchangeRepository;

    @Transactional
    public Map<String,Object> insert(User user) {
        Map<String,Object> result = new HashMap<>();
        if(user.getEmailId()=="" || user.getPassword()=="" || user.getName()=="" || user.getPhoneNumber()==""){
            result.put("code", "0000");
            result.put("msg", "필수값 누락으로 회원가입 실패");
            return result;
        }else if(userRepository.findByDeleteDatetimeIsNullAndEmailId(user.getEmailId())!=null){
            result.put("code", "0000");
            result.put("msg", "이미 가입된 아이디입니다");
            return result;
        }else{
            BCryptPasswordEncoder pe = new BCryptPasswordEncoder();

            user.setCreateDatetime(new Date());
            user.setPassword(pe.encode(user.getPassword()));
            user.setLevel("MANAGE");
            user.setTotalTg(0.0);
            userRepository.save(user);
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
    public Object findByUserInfo(Integer userId) {
        User user = userRepository.findById(userId).get();
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


}
