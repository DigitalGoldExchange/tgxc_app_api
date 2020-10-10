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

    @ApiOperation(value = "거래 수정", notes = "거래 정보를 수정한다.")
    @PostMapping(value = "/update")
    public CommonResult update(
            @RequestParam(value = "exchangeId", required = false) Integer exchangeId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "note", required = false) String note
    ) {
        exchangeService.update(exchangeId, status, note);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "거래 단건 조회", notes = "거래 pk (exchangeId)를 받아 해당 회원의 정보를 조회한다.")
    @GetMapping(value = "/getOne")
    public SingleResult<Object> getOne(
            @RequestParam(value = "exchangeId", required = false) Integer exchangeId
    ) {
        return responseService.getSingleResult(exchangeService.findByExchangeInfo(exchangeId));
    }

    @ApiOperation(value = "교환 등록")
    @PostMapping(value = "/insert")
    public CommonResult insert(Exchange exchange) throws Exception {
        exchangeService.insert(exchange);
        return responseService.getSuccessResult();
    }
}
