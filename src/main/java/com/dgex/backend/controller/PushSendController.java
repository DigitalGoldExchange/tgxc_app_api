package com.dgex.backend.controller;

import com.dgex.backend.entity.User;
import com.dgex.backend.response.CommonResult;
import com.dgex.backend.response.SingleResult;
import com.dgex.backend.service.PushInfoService;
import com.dgex.backend.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = {"PushInfo : 푸시(PushInfo)"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/push")
public class PushSendController {

    private final ResponseService responseService;
    private final PushInfoService pushInfoService;

    @ApiOperation(value = "회원 등록", notes = "입력한 회원 정보를 등록한다.")
    @PostMapping(value = "/sendPush")
    public SingleResult<Object> sendPush(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content

    ) {

        return responseService.getSingleResult(pushInfoService.sendPush(userId, title, content));
    }
}
