package com.dgex.backend.controller;

import com.dgex.backend.response.CommonResult;
import com.dgex.backend.response.SingleResult;
import com.dgex.backend.service.DepositAccountService;
import com.dgex.backend.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"DepositAccount : 지갑주소(DepositAccount)"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/deposit")
public class DepositAccountController {

    private final ResponseService responseService;
    private final DepositAccountService depositAccountService;


    @ApiOperation(value = "지갑주소 등록", notes = "입력한 지갑주소 정보를 등록한다.")
    @PostMapping(value = "/insert")
    public CommonResult insert(
            @RequestParam(value = "walletAddress", required = false) String walletAddress
    ) {
        depositAccountService.insert(walletAddress);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "지갑주소 리스트")
    @GetMapping(value = "/getList")
    public SingleResult<Object> getList(
    ) {
        return responseService.getSingleResult(depositAccountService.getList());
    }
}
