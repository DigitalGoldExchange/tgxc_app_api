package com.dgex.backend.controller;

import com.dgex.backend.entity.User;
import com.dgex.backend.repository.NoticeRepository;
import com.dgex.backend.response.CommonResult;
import com.dgex.backend.response.SingleResult;
import com.dgex.backend.service.NoticeService;
import com.dgex.backend.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = {"Notice : 공지사항(Notice)"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/notice")
public class NoticeController {

    private final NoticeService noticeService;
    private final ResponseService responseService;
    private final NoticeRepository noticeRepository;

    @ApiOperation(value = "공지사항 등록", notes = "입력한 공지사항 정보를 등록한다.")
    @PostMapping(value = "/insert")
    public CommonResult insert(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "contents", required = false) String contents,
            @RequestParam(value = "koreanYn", required = false) String koreanYn
    ) {
        noticeService.insert(title, contents,koreanYn);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "공지사항 리스트")
    @GetMapping(value = "/getList")
    public SingleResult<Object> getList(
            @RequestParam(value = "searchWord", required = false) String searchWord,
            @RequestParam(defaultValue = "1", required = false) Integer page,
            @RequestParam(value = "koreanYn", required = false) String koreanYn
    ) {
        return responseService.getSingleResult(noticeService.getList(page, searchWord, koreanYn));
    }

    @ApiOperation(value = "공지사항 수정", notes = "공지사항 정보를 수정한다.")
    @PostMapping(value = "/update")
    public CommonResult update(
            @RequestParam(value = "noticeId", required = false) Integer noticeId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "koreanYn", required = false) String koreanYn
    ) {
        noticeService.update(noticeId, status, koreanYn);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "공지사항 삭제", notes = "공지사항 정보를 삭제한다.")
    @PostMapping(value = "/delete")
    public CommonResult delete(
            @RequestParam(value = "noticeId", required = false) Integer noticeId,
            @RequestParam(value = "koreanYn", required = false) String koreanYn
    ) {
        noticeService.delete(noticeId, koreanYn);
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "공지사항 단건 조회", notes = "공지사항 pk (noticeId)를 받아 해당 회원의 정보를 조회한다.")
    @GetMapping(value = "/getOne")
    public SingleResult<Object> getOne(
            @RequestParam(value = "noticeId", required = false) Integer noticeId
    ) {
        return responseService.getSingleResult(noticeRepository.findById(noticeId));
    }

    @ApiOperation(value = "공지사항 내용수정", notes = "공지사항 pk (noticeId)를 받아 해당 회원의 정보를 조회한다.")
    @PostMapping(value = "/updateContent")
    public CommonResult updateContent(
            @RequestParam(value = "noticeId", required = false) Integer noticeId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "contents", required = false) String contents,
            @RequestParam(value = "koreanYn", required = false) String koreanYn
    ) {
        noticeService.updateContent(noticeId, title, contents, koreanYn);
        return responseService.getSuccessResult();
    }


}
