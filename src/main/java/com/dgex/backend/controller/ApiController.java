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

import java.security.InvalidKeyException;
import java.security.SignatureException;
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
            @RequestHeader(value = "token") String token
    ) throws SignatureException {
        Map<String, Object> result = userService.userInfo(identifyNumber,token);
        if(result.get("code")=="0000"){
            return responseService.getSuccessResult();
        }else{
            return responseService.getFailResult(0000, result.get("msg").toString());
        }
//        return responseService.getSingleResult();
    }

    @ApiOperation(value = "장부등록")
    @GetMapping(value = "/book")
    public CommonResult insertBook(
            @RequestParam(value = "userId") String identifyNumber,
            @RequestParam(value = "txid") String txId,
            @RequestParam(value = "amount") Double amount,
            @RequestParam(value = "txidTime") String txidTime,
            @RequestHeader(value = "token") String token
    ) throws SignatureException {
        Map<String, Object> result = exchangeService.insertBook(identifyNumber,txId, amount, txidTime, token);
        if(result.get("code")=="0000"){
            return responseService.getSuccessResult();
        }else{
            return responseService.getFailResult(0001, result.get("msg").toString());
        }
    }

    @ApiOperation(value = "장부확인")
    @GetMapping(value = "/checkBook")
    public SingleResult<Object> checkBook(
            @RequestParam(value = "txid") String txId,
            @RequestHeader(value = "token") String token
    ) throws SignatureException {
        return responseService.getSingleResult(exchangeService.checkBook(txId, token));
    }



}
