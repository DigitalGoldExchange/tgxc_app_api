package com.dgex.backend.controller;

import com.dgex.backend.repository.ServerManageRepository;
import com.dgex.backend.response.CommonResult;
import com.dgex.backend.response.SingleResult;
import com.dgex.backend.service.ResponseService;
import com.dgex.backend.service.ServerManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"ServerManage : 서버(ServerManage)"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/serverManage")
public class ServerManageController {

    private final ServerManageService serverManageService;
    private final ResponseService responseService;
    private final ServerManageRepository serverManageRepository;

    @ApiOperation(value = "회원 수정", notes = "회원 정보를 수정한다.")
    @PostMapping(value = "/update")
    public CommonResult update(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "status", required = false) String status
    ) {
        serverManageService.update(userId, status);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "단건 조회")
    @GetMapping(value = "/getOne")
    public SingleResult<Object> getOne(
    ) {
        return responseService.getSingleResult(serverManageRepository.findByDeleteDatetimeIsNull());
    }
}
