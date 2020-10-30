package com.dgex.backend.controller;

import com.dgex.backend.response.CommonResult;
import com.dgex.backend.response.SingleResult;
import com.dgex.backend.service.ExchangeStoreService;
import com.dgex.backend.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"ExchangeStore : 교환매장(ExchangeStore)"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/exchangeStore")
public class ExchangeStoreController {

    private final ResponseService responseService;
    private final ExchangeStoreService exchangeStoreService;

    @ApiOperation(value = "교환매장 등록", notes = "입력한 교환비율 정보를 등록한다.")
    @PostMapping(value = "/insert")
    public CommonResult insert(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber
    ) {
        exchangeStoreService.insert(name, address,phoneNumber);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "교환매장 리스트")
    @GetMapping(value = "/getInactiveStoreList")
    public SingleResult<Object> getInactiveStoreList(
    ) {
        return responseService.getSingleResult(exchangeStoreService.getInactiveStoreList());
    }

    @ApiOperation(value = "교환매장 리스트")
    @GetMapping(value = "/getActiveStoreList")
    public SingleResult<Object> getActiveStoreList(
    ) {
        return responseService.getSingleResult(exchangeStoreService.getActiveStoreList());
    }

    @ApiOperation(value = "교환매장 삭제")
    @PostMapping(value = "/delete")
    public CommonResult delete(
            @RequestParam(value = "storeId", required = false) String storeId
    ) {
        exchangeStoreService.delete(storeId);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "교환매장 비활성화")
    @PostMapping(value = "/inactiveUpdate")
    public CommonResult inactiveUpdate(
            @RequestParam(value = "storeId", required = false) String storeId
    ) {
        exchangeStoreService.inactiveUpdate(storeId);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "교환매장 활성화")
    @PostMapping(value = "/activeUpdate")
    public CommonResult activeUpdate(
            @RequestParam(value = "storeId", required = false) String storeId
    ) {
        exchangeStoreService.activeUpdate(storeId);
        return responseService.getSuccessResult();
    }


}
