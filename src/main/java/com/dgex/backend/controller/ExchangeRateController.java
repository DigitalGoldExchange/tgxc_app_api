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
            @RequestParam(value = "exchangeRate", required = false) Double exchangeRate
    ) {
        exchangeRateService.insert(exchangeRate);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "교환비율 리스트")
    @GetMapping(value = "/getList")
    public SingleResult<Object> getList(
    ) {
        return responseService.getSingleResult(exchangeRateService.getList());
    }
}
