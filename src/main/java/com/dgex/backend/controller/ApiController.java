package com.dgex.backend.controller;

import com.dgex.backend.entity.Exchange;
import com.dgex.backend.response.CommonResult;
import com.dgex.backend.response.SingleResult;
import com.dgex.backend.service.ExchangeService;
import com.dgex.backend.service.ResponseService;
import com.dgex.backend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

@Api(tags = {"Api"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class ApiController {

    private final UserService userService;
    private final ExchangeService exchangeService;
    private final ResponseService responseService;



    @ApiOperation(value = "회원고유번호확인")
    @GetMapping(value = "/userInfo")
    public CommonResult userInfo(
            @RequestParam(value = "userId") String identifyNumber,
            @RequestHeader(value = "token") String token,
            HttpServletRequest request
    ) throws SignatureException {

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        String[] realIp = ip.split(",");

        System.out.println(realIp[0]);

        if("211.62.106.9".equals(realIp[0].trim())){
            Map<String, Object> result = userService.userInfo(identifyNumber,token);
            if(result.get("code")=="0000"){
                return responseService.getSuccessResult();
            }else{
                return responseService.getFailResult(0000, result.get("msg").toString());
            }
        }else{
            return responseService.getFailResult(0004, "허용된 IP가 아닙니다.");
        }


    }

    @ApiOperation(value = "장부등록")
    @GetMapping(value = "/book")
    public CommonResult insertBook(
            @RequestParam(value = "userId") String identifyNumber,
            @RequestParam(value = "txid") String txId,
            @RequestParam(value = "amount") String amount,
            @RequestParam(value = "txidTime") String txidTime,
            @RequestHeader(value = "token") String token,
            HttpServletRequest request
    ) throws SignatureException {

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        String[] realIp = ip.split(",");

        System.out.println(realIp[0]);

        if("211.62.106.9".equals(realIp[0].trim())){
            Map<String, Object> result = exchangeService.insertBook(identifyNumber,txId, amount, txidTime, token);
            if(result.get("code")=="0000"){
                return responseService.getSuccessResult();
            }else{
                return responseService.getFailResult(0001, result.get("msg").toString());
            }
        }else{
            return responseService.getFailResult(0004, "허용된 IP가 아닙니다.");
        }


    }

    @ApiOperation(value = "장부확인")
    @GetMapping(value = "/checkBook")
    public SingleResult<Object> checkBook(
            @RequestParam(value = "txid") String txId,
            @RequestHeader(value = "token") String token,
            HttpServletRequest request
    ) throws SignatureException {

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        String[] realIp = ip.split(",");

        System.out.println(realIp[0]);

        if("211.62.106.9".equals(realIp[0].trim())){
            return responseService.getSingleResult(exchangeService.checkBook(txId, token));
        }else{
            Map<String, Object> result = new HashMap<>();
            result.put("result", false);
            result.put("msg", "허용된 IP가 아닙니다.");
            return responseService.getSingleResult(result);
        }


    }



}
