package com.dgex.backend.controller;

import com.dgex.backend.entity.User;
import com.dgex.backend.repository.UserRepository;
import com.dgex.backend.response.CommonResult;
import com.dgex.backend.response.SingleResult;
import com.dgex.backend.service.ResponseService;
import com.dgex.backend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = {"User : 회원(User)"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final ResponseService responseService;
    private final UserService userService;
    private final UserRepository userRepository;

    @ApiOperation(value = "회원 등록", notes = "입력한 회원 정보를 등록한다.")
    @PostMapping(value = "/insert")
    public CommonResult insert(User user) {
        Map<String, Object> result = userService.insert(user);
        if(result.get("code")=="0001"){
            return responseService.getSuccessResult();
        }else{
            return responseService.getFailResult(0000, result.get("msg").toString());
        }
    }

    @ApiOperation(value = "회원 리스트", notes = "회원 pk (userId)를 역방향(최신순)으로 정렬한 리스트를 조회한다.")
    @GetMapping(value = "/getList")
    public SingleResult<Object> getList(
                                        @RequestParam(value = "searchKey", required = false) Integer searchKey,
                                        @RequestParam(value = "searchWord", required = false) String searchWord,
                                        @RequestParam(defaultValue = "1", required = false) Integer page
    ) {
        return responseService.getSingleResult(userService.getList(page, searchKey, searchWord));
    }

    @ApiOperation(value = "회원 수정", notes = "회원 정보를 수정한다.")
    @PostMapping(value = "/update")
    public CommonResult update(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "status", required = false) Integer status
    ) {
        userService.update(userId, status);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "회원 단건 조회", notes = "회원 pk (userId)를 받아 해당 회원의 정보를 조회한다.")
    @GetMapping(value = "/getOne")
    public SingleResult<Object> getOne(
            @RequestParam(value = "userId", required = false) Integer userId
    ) {
        return responseService.getSingleResult(userService.findByUserInfo(userId));
    }

    @ApiOperation(value = "로그인", notes = "회원 로그인 정보를 받아 일치 여부를 조회한다.")
    @PostMapping(value = "/login")
    public SingleResult<Object> login(
            @RequestParam(value = "emailId", required = false) String emailId,
            @RequestParam(value = "password", required = false) String password
    ) {
        return responseService.getSingleResult(userService.loginCheck(emailId, password));
    }



}
