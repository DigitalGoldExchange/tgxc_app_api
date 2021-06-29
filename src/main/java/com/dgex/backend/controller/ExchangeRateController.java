package com.dgex.backend.controller;

import com.dgex.backend.repository.ExchangeRateRepository;
import com.dgex.backend.response.CommonResult;
import com.dgex.backend.response.SingleResult;
import com.dgex.backend.service.ExchangeRateService;
import com.dgex.backend.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"ExchangeRate : 교환비율(ExchangeRate)"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/exchangeRate")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;
    private final ResponseService responseService;
    private final ExchangeRateRepository exchangeRateRepository;

    @ApiOperation(value = "교환비율 등록", notes = "입력한 교환비율 정보를 등록한다.")
    @PostMapping(value = "/insert")
    public CommonResult insert(
            @RequestParam(value = "exchangeRate", required = false) String exchangeRate,
            @RequestParam(value = "exchangeGram", required = false) String exchangeGram
    ) {
        exchangeRateService.insert(exchangeRate, exchangeGram);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "교환비율 리스트")
    @GetMapping(value = "/getList")
    public SingleResult<Object> getList(
    ) {
        return responseService.getSingleResult(exchangeRateService.getList());
    }

    @ApiOperation(value = "교환비율 리스트")
    @GetMapping(value = "/getList1")
    public SingleResult<Object> getList1(
    ) {
        return responseService.getSingleResult(exchangeRateService.getList1());
    }

    @ApiOperation(value = "교환비율 리스트")
    @GetMapping(value = "/getOne")
    public SingleResult<Object> getOne(
            @RequestParam(value = "exchangeRateId", required = false) Integer exchangeRateId
    ) {
        return responseService.getSingleResult(exchangeRateService.getOne(exchangeRateId));
    }

    @ApiOperation(value = "교환비율 등록", notes = "입력한 교환비율 정보를 등록한다.")
    @PostMapping(value = "/update")
    public CommonResult update(
            @RequestParam(value = "exchangeRate", required = false) String exchangeRate,
            @RequestParam(value = "exchangeGram", required = false) String exchangeGram,
            @RequestParam(value = "exchangeRateId", required = false) Integer exchangeRateId

    ) {
        exchangeRateService.update(exchangeRateId, exchangeRate, exchangeGram);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "교환비율 삭제")
    @PostMapping(value = "/delete")
    public CommonResult delete(
            @RequestParam(value = "exchangeRateId", required = false) String exchangeRateId
    ) {
        exchangeRateService.delete(exchangeRateId);
        return responseService.getSuccessResult();
    }

}
