package com.dgex.backend.controller;

import com.dgex.backend.entity.ExchangeMethod;
import com.dgex.backend.repository.ExchangeMethodRepository;
import com.dgex.backend.response.CommonResult;
import com.dgex.backend.response.SingleResult;
import com.dgex.backend.service.ExchangeMethodService;
import com.dgex.backend.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"ExchangeMethod : 교환방법(ExchangeMethod)"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/exchangeMethod")
public class ExchangeMethodController {

    private final ResponseService responseService;
    private final ExchangeMethodRepository exchangeMethodRepository;
    private final ExchangeMethodService exchangeMethodService;

    @ApiOperation(value = "교환비율 리스트")
    @GetMapping(value = "/getList")
    public SingleResult<Object> getList(
    ) {
        return responseService.getSingleResult(exchangeMethodRepository.findByDeleteDatetimeIsNull());
    }

    @ApiOperation(value = "공지사항 수정", notes = "공지사항 정보를 수정한다.")
    @PostMapping(value = "/update")
    public CommonResult update(ExchangeMethod exchangeMethod) {
        exchangeMethodService.update(exchangeMethod);
        return responseService.getSuccessResult();
    }
}
