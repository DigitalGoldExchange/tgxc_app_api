package com.dgex.backend.controller;

import com.dgex.backend.entity.Exchange;
import com.dgex.backend.response.CommonResult;
import com.dgex.backend.response.SingleResult;
import com.dgex.backend.service.ExchangeService;
import com.dgex.backend.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = {"Exchange : 거래내역(Exchange)"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/exchange")
public class ExchangeController {

    private final ResponseService responseService;
    private final ExchangeService exchangeService;

    @ApiOperation(value = "거래내역 리스트")
    @GetMapping(value = "/getList")
    public SingleResult<Object> getList(
            @RequestParam(value = "searchKey", required = false) Integer searchKey,
            @RequestParam(value = "searchWord", required = false) String searchWord,
            @RequestParam(defaultValue = "1", required = false) Integer page
    ) {
        return responseService.getSingleResult(exchangeService.getList(page, searchKey, searchWord));
    }

    @ApiOperation(value = "거래내역 리스트")
    @GetMapping(value = "/getMemberConfirmList")
    public SingleResult<Object> getMemberConfirmList(
            @RequestParam(value = "reqNumber", required = false) String reqNumber
    ) {
        return responseService.getSingleResult(exchangeService.getMemberConfirmList(reqNumber));
    }

    @ApiOperation(value = "거래내역 리스트")
    @GetMapping(value = "/getDepositList")
    public SingleResult<Object> getDepositList(
            @RequestParam(value = "searchKey", required = false) Integer searchKey,
            @RequestParam(value = "searchWord", required = false) String searchWord,
            @RequestParam(defaultValue = "1", required = false) Integer page
    ) {
        return responseService.getSingleResult(exchangeService.getDepositList(page, searchKey, searchWord));
    }

//    @ApiOperation(value = "거래 수정", notes = "거래 정보를 수정한다.")
//    @PostMapping(value = "/update")
//    public CommonResult update(
//            @RequestParam(value = "exchangeId", required = false) Integer exchangeId,
//            @RequestParam(value = "status", required = false) String status,
//            @RequestParam(value = "note", required = false) String note
//    ) {
//        exchangeService.update(exchangeId, status, note);
//        return responseService.getSuccessResult();
//    }
    @ApiOperation(value = "거래 수정", notes = "거래 정보를 수정한다.")
    @PostMapping(value = "/update")
    public SingleResult<Object> update(
            @RequestParam(value = "exchangeId", required = false) Integer exchangeId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "note", required = false) String note
    ) {
        return responseService.getSingleResult(exchangeService.update(exchangeId, status, note));
    }

    @ApiOperation(value = "거래 수정", notes = "거래 정보를 수정한다.")
    @PostMapping(value = "/userCancel")
    public SingleResult<Object> userCancel(
            @RequestParam(value = "exchangeId", required = false) Integer exchangeId,
            @RequestParam(value = "status", required = false) String status
    ) {
        return responseService.getSingleResult(exchangeService.userCancel(exchangeId, status));
    }


    @ApiOperation(value = "거래 수정", notes = "거래 정보를 수정한다.")
    @PostMapping(value = "/depositUpdate")
    public CommonResult depositUpdate(
            @RequestParam(value = "exchangeId", required = false) Integer exchangeId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "txid", required = false) String txid
    ) {
        exchangeService.depositUpdate(exchangeId, status, txid);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "거래 단건 조회", notes = "거래 pk (exchangeId)를 받아 해당 회원의 정보를 조회한다.")
    @GetMapping(value = "/getOne")
    public SingleResult<Object> getOne(
            @RequestParam(value = "exchangeId", required = false) Integer exchangeId
    ) {
        return responseService.getSingleResult(exchangeService.findByExchangeInfo(exchangeId));
    }

    @ApiOperation(value = "거래 단건 조회", notes = "거래 pk (exchangeId)를 받아 해당 회원의 정보를 조회한다.")
    @GetMapping(value = "/findByType")
    public SingleResult<Object> findByType(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "type", required = false) String type
    ) {
        return responseService.getSingleResult(exchangeService.findByType(userId, type));
    }



    @ApiOperation(value = "교환 등록")
    @PostMapping(value = "/insert")
    public CommonResult insert(Exchange exchange) throws Exception {
        exchangeService.insert(exchange);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "교환 등록")
    @PostMapping(value = "/insertWithdraw")
    public CommonResult insertWithdraw(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "walletAddr", required = false) String walletAddr,
            @RequestParam(value = "sendTg", required = false) String sendTg
    ) {
        exchangeService.insertWithdraw(userId, walletAddr, sendTg);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "교환 등록")
    @PostMapping(value = "/insertExchange")
    public CommonResult insertExchange(
            @RequestParam(value = "userId") Integer userId,
            @RequestParam(value = "walletAddr", required = false) String walletAddr,
            @RequestParam(value = "exchangeMethod") String exchangeMethod,
            @RequestParam(value = "reqAmount") String reqAmount,
            @RequestParam(value = "identifyCard") MultipartFile identifyCard,
            @RequestParam(value = "profileImage") MultipartFile profileImage,
            @RequestParam(value = "exchangeStoreId") Integer exchangeStoreId,
            @RequestParam(value = "reqType", required = false) String reqType,
            @RequestParam(value = "reqQty", required = false) String reqQty

    ) {
        exchangeService.insertExchange(userId, walletAddr,exchangeMethod, reqAmount, identifyCard, profileImage, exchangeStoreId, reqType, reqQty);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "교환 등록")
    @PostMapping(value = "/insertExchangeAnd")
    public CommonResult insertExchangeAnd(
            @RequestParam(value = "userId") Integer userId,
            @RequestParam(value = "walletAddr", required = false) String walletAddr,
            @RequestParam(value = "exchangeMethod") String exchangeMethod,
            @RequestParam(value = "reqAmount") String reqAmount,
            @RequestParam(value = "identifyCard") MultipartFile identifyCard,
            @RequestParam(value = "profileImage") MultipartFile profileImage,
            @RequestParam(value = "exchangeStoreId") Integer exchangeStoreId,
            @RequestParam(value = "reqType") String reqType,
            @RequestParam(value = "reqQty") String reqQty

    ) {
        exchangeService.insertExchange(userId, walletAddr,exchangeMethod, reqAmount, identifyCard, profileImage, exchangeStoreId, reqType, reqQty);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "텔레그램 테스트")
    @PostMapping(value = "/sendTelegram")
    public CommonResult sendTelegram1(){
        exchangeService.sendTelegram1();
        return responseService.getSuccessResult();
    }
}
