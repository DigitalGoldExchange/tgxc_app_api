package com.dgex.backend.controller;

import com.dgex.backend.response.CommonResult;
import com.dgex.backend.response.SingleResult;
import com.dgex.backend.service.ExcelDownService;
import com.dgex.backend.service.ResponseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api(tags = {"ExcelDown : 엑셀(ExcelDown)"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/excel")
public class ExcelDownController {

    private final ResponseService responseService;
    private final ExcelDownService excelDownService;


    @ApiOperation(value = "엑셀다운로드")
    @PostMapping(value = "/excelDown")
    public CommonResult excelDown(
            @RequestParam(value = "exchangeId", required = false) Integer exchangeId
    ) throws Exception {
        excelDownService.excelDown(exchangeId);
        return responseService.getSuccessResult();
    }

}
