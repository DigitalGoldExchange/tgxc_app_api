package com.dgex.backend.controller;

import com.dgex.backend.entity.User;
import com.dgex.backend.repository.UserRepository;
import com.dgex.backend.response.CommonResult;
import com.dgex.backend.response.SingleResult;
import com.dgex.backend.service.ResponseService;
import com.dgex.backend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Base64;
import java.util.Map;

@Api(tags = {"User : 회원(User)"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final ResponseService responseService;
    private final UserService userService;
    private final UserRepository userRepository;

    @ApiOperation(value = "회원 등록", notes = "입력한 회원 정보를 등록한다.")
    @PostMapping(value = "/insert")
    public SingleResult<Object> insert(User user,@RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {
//        Map<String, Object> result = userService.insert(user, profileImage);
//        if(result.get("code")=="0001"){
//            return responseService.getSuccessResult();
//        }else{
//            return responseService.getFailResult(0000, result.get("msg").toString());
//        }
        return responseService.getSingleResult(userService.insert(user, profileImage));
    }

    @ApiOperation(value = "이메일 인증")
    @GetMapping(value = "/signUpConfirm")
    public void signUpConfirm(
            @RequestParam(value = "email", required = false) String emailId,
            @RequestParam(value = "authKey", required = false) String signKey,
            HttpServletResponse response
    ) throws IOException {
//        return responseService.getSingleResult(userService.signUpConfirm(emailId, signKey));
        boolean result = userService.signUpConfirm(emailId, signKey);
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        if(result){
            out.println("<script>alert('인증되었습니다.'); window.open('','_self').close();</script>");
        }else{
            out.println("<script>alert('이미 인증되었습니다.'); window.open('','_self').close();</script>");
        }

    }

    @ApiOperation(value = "관리자 등록", notes = "입력한 회원 정보를 등록한다.")
    @PostMapping(value = "/insertAdmin")
    public CommonResult insertAdmin(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "emailId", required = false) String emailId,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "menuLevel", required = false) String menuLevel
    ) {
        Map<String, Object> result = userService.insertAdmin(name, emailId, password, menuLevel);
        if(result.get("code")=="0001"){
            return responseService.getSuccessResult();
        }else{
            return responseService.getFailResult(0000, result.get("msg").toString());
        }
    }

    @ApiOperation(value = "관리자 등록", notes = "입력한 회원 정보를 등록한다.")
    @PostMapping(value = "/insertMember")
    public CommonResult insertMember(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "emailId", required = false) String emailId,
            @RequestParam(value = "password", required = false) String password
    ) {
        Map<String, Object> result = userService.insertMember(name, emailId, password);
        if(result.get("code")=="0001"){
            return responseService.getSuccessResult();
        }else{
            return responseService.getFailResult(0000, result.get("msg").toString());
        }
    }

    @ApiOperation(value = "회원 리스트", notes = "회원 pk (userId)를 역방향(최신순)으로 정렬한 리스트를 조회한다.")
    @GetMapping(value = "/getList")
    public SingleResult<Object> getList(
                                        @RequestParam(value = "searchKey", required = false) Integer searchKey,
                                        @RequestParam(value = "searchWord", required = false) String searchWord,
                                        @RequestParam(defaultValue = "1", required = false) Integer page
    ) {
        return responseService.getSingleResult(userService.getList(page, searchKey, searchWord));
    }

    @ApiOperation(value = "회원 리스트", notes = "회원 pk (userId)를 역방향(최신순)으로 정렬한 리스트를 조회한다.")
    @GetMapping(value = "/refreshToken")
    public SingleResult<Object> refreshToken(
            @RequestParam(value = "token") String token
    ) throws SignatureException {
        return responseService.getSingleResult(userService.refreshToken(token));
    }

    @ApiOperation(value = "관리자 리스트", notes = "회원 pk (userId)를 역방향(최신순)으로 정렬한 리스트를 조회한다.")
    @GetMapping(value = "/getManageList")
    public SingleResult<Object> getManageList(
            @RequestParam(defaultValue = "1", required = false) Integer page
    ) {
        return responseService.getSingleResult(userService.getManageList(page));
    }

    @ApiOperation(value = "회원 수정", notes = "회원 정보를 수정한다.")
    @PostMapping(value = "/update")
    public CommonResult update(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "status", required = false) Integer status
    ) {
        userService.update(userId, status);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "OTP 초기화")
    @PostMapping(value = "/userOtpInit")
    public CommonResult userOtpInit(
            @RequestParam(value = "userId", required = false) Integer userId
    ) {
        userService.userOtpInit(userId);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "관리자 수정", notes = "회원 정보를 수정한다.")
    @PostMapping(value = "/updateAdmin")
    public CommonResult updateAdmin(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "emailId", required = false) String emailId,
            @RequestParam(value = "updatePassword", required = false) String password

    ) {
        userService.updateAdmin(userId, name, emailId, password);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "비밀번호 초기화", notes = "회원 정보를 수정한다.")
    @PostMapping(value = "/resetPassword")
    public CommonResult resetPassword(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "password", required = false) String password

    ) {
        userService.resetPassword(userId, password);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "관리자 권한수정", notes = "회원 정보를 수정한다.")
    @PostMapping(value = "/updateLevel")
    public CommonResult updateLevel(
            @RequestParam(value = "level", required = false) String level

    ) {
        userService.updateLevel(level);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "회원 단건 조회", notes = "회원 pk (userId)를 받아 해당 회원의 정보를 조회한다.")
    @GetMapping(value = "/getOne")
    public SingleResult<Object> getOne(
            @RequestParam(value = "userId", required = false) Integer userId
    ) {
        return responseService.getSingleResult(userService.findByUserInfo(userId));
    }

    @ApiOperation(value = "회원 단건 조회", notes = "회원 pk (userId)를 받아 해당 회원의 정보를 조회한다.")
    @GetMapping(value = "/depositInfo")
    public SingleResult<Object> depositInfo(
            @RequestParam(value = "userId", required = false) Integer userId
    ) {
        return responseService.getSingleResult(userService.depositInfo(userId));
    }

    @ApiOperation(value = "회원 비밀번호 체크", notes = "회원 pk (userId)를 받아 해당 회원의 정보를 조회한다.")
    @GetMapping(value = "/findPassword")
    public SingleResult<Object> passwordCheck(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "pw", required = false) String password
    ) {
        return responseService.getSingleResult(userService.passwordCheck(userId, password));
    }

    @ApiOperation(value = "로그인", notes = "회원 로그인 정보를 받아 일치 여부를 조회한다.")
    @PostMapping(value = "/login")
    public SingleResult<Object> login(
            @RequestParam(value = "emailId", required = false) String emailId,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "deviceToken", required = false) String deviceToken,
            @RequestParam(value = "deviceType", required = false) String deviceType,
            @RequestParam(value = "role", required = false) String role
    ) {
        return responseService.getSingleResult(userService.loginCheck(emailId, password, deviceToken, deviceType,role));
    }



    @ApiOperation(value = "회원 삭제", notes = "입력한 회원 정보를 삭제한다.")
    @PostMapping(value = "/delete")
    public CommonResult delete(
            @RequestParam(value = "userId", required = false) Integer userId
    ) {
        userService.delete(userId);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "비밀번호 찾기")
    @PostMapping(value = "/sendEmail")
    public SingleResult<Object> sendEmail(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "emailId", required = false) String emailId
    ) {
        return responseService.getSingleResult(userService.sendEmail(name, emailId));
    }

    @ApiOperation(value = "비밀번호 찾기")
    @PostMapping(value = "/findUserPassword")
    public SingleResult<Object> findUserPassword(
            @RequestParam(value = "emailId", required = false) String emailId,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber
    ) {
        return responseService.getSingleResult(userService.findUserPassword(emailId, phoneNumber));
    }

    @ApiOperation(value = "회원 단건 조회", notes = "회원 pk (userId)를 받아 해당 회원의 정보를 조회한다.")
    @GetMapping(value = "/findByEmailId")
    public SingleResult<Object> findByEmailId(
            @RequestParam(value = "emailId", required = false) String emailId
    ) {
        return responseService.getSingleResult(userService.findByEmailId(emailId));
    }

    @ApiOperation(value = "회원 단건 조회", notes = "회원 pk (userId)를 받아 해당 회원의 정보를 조회한다.")
    @GetMapping(value = "/findByPhoneNumber")
    public SingleResult<Object> findByPhone(
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber
    ) {
        return responseService.getSingleResult(userService.findByPhoneNumber(phoneNumber));
    }

    @ApiOperation(value = "OTP코드")
    @GetMapping(value = "/getOtpCode")
    public SingleResult<Object> getOtpCode() {
        return responseService.getSingleResult(userService.getOtpCode());
    }

    @ApiOperation(value = "OTP 인증")
    @PostMapping(value = "/checkCode")
    public SingleResult<Object> checkCode(
            @RequestParam(value = "userCode", required = true) String userCode,
            @RequestParam(value = "otpKey", required = true) String otpKey
    ) throws NoSuchAlgorithmException, InvalidKeyException {
        return responseService.getSingleResult(userService.checkCode(userCode, otpKey));
    }

    @ApiOperation(value = "OTP 인증")
    @PostMapping(value = "/confirmOtp")
    public SingleResult<Object> confirmOtp(
            @RequestParam(value = "userCode", required = true) String userCode,
            @RequestParam(value = "userId", required = true) Integer userId
    ) throws NoSuchAlgorithmException, InvalidKeyException {
        return responseService.getSingleResult(userService.confirmOtp(userCode, userId));
    }



    @ApiOperation(value = "confirmEmail")
    @PostMapping(value = "/sendSignKey")
    public SingleResult<Object> sendSignKey(
            @RequestParam(value = "emailId", required = true) String emailId,
            @RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "isKorea", required = true) String isKorea,
            @RequestParam(value = "signKey", required = true) String signKey
    ) {
        return responseService.getSingleResult(userService.sendSignKey(emailId, name,isKorea, signKey));
    }


    @ApiOperation(value = "OTP 수정")
    @PostMapping(value = "/updateOtpKey")
    public CommonResult updateOtpKey(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "otpKey", required = false) String otpKey
    ) {
        userService.updateOtpKey(userId, otpKey);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "개인정보 수정")
    @PostMapping(value = "/updateUser")
    public CommonResult updateUser(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "addressDetail", required = false) String addressDetail,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "zipCode", required = false) String zipCode
    ) {
        userService.updateUser(userId, address, addressDetail, phoneNumber, password, zipCode);
        return responseService.getSuccessResult();
    }


    @ApiOperation(value = "푸시타입 수정")
    @PostMapping(value = "/updatePushType")
    public CommonResult updatePushType(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "pushType", required = false) String pushType
    ) {
        userService.updatePushType(userId, pushType);
        return responseService.getSuccessResult();
    }







}
