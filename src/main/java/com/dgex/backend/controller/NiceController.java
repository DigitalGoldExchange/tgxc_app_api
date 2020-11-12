package com.dgex.backend.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Api(tags = {"Nice : 본인인증(Nice)"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/nice")
public class NiceController {

    @ApiOperation(value = "본인인증시작")
    @GetMapping(value = "/niceStart")
    public ModelAndView niceStart(
            ModelAndView mav
    ) {

        Map<String, Object> payReqMap = new HashMap<>();

        NiceID.Check.CPClient niceCheck = new  NiceID.Check.CPClient();

        String sSiteCode = "BT052";			// NICE로부터 부여받은 사이트 코드
        String sSitePassword = "I9fpP2tGg3Ep";		// NICE로부터 부여받은 사이트 패스워드

        Random rd = new Random();
        String ran = "";
        for(int i = 0; i < 5; i++){
            ran += rd.nextInt(9);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");

        Date time = new Date();
        String timeString = dateFormat.format(time);

        String sRequestNumber = timeString + ran;

//        String sRequestNumber = "REQ0000000001";        	// 요청 번호, 이는 성공/실패후에 같은 값으로 되돌려주게 되므로
// 업체에서 적절하게 변경하여 쓰거나, 아래와 같이 생성한다.
        sRequestNumber = niceCheck.getRequestNO(sSiteCode);
//        session.setAttribute("REQ_SEQ" , sRequestNumber);	// 해킹등의 방지를 위하여 세션을 쓴다면, 세션에 요청번호를 넣는다.

        String sAuthType = "M";      	// 없으면 기본 선택화면, M: 핸드폰, C: 신용카드, X: 공인인증서

        String popgubun 	= "N";		//Y : 취소버튼 있음 / N : 취소버튼 없음
        String customize 	= "Mobile";		//없으면 기본 웹페이지 / Mobile : 모바일페이지

        String sGender = ""; 			//없으면 기본 선택 값, 0 : 여자, 1 : 남자

// CheckPlus(본인인증) 처리 후, 결과 데이타를 리턴 받기위해 다음예제와 같이 http부터 입력합니다.
//리턴url은 인증 전 인증페이지를 호출하기 전 url과 동일해야 합니다. ex) 인증 전 url : http://www.~ 리턴 url : http://www.~
//        String sReturnUrl = "http://localhost:8093/nice/niceSuccess";      // 성공시 이동될 URL
//        String sErrorUrl = "http://localhost:8093/nice/niceFail";         // 실패시 이동될 URL
        String sReturnUrl = "http://117.52.98.39:8093/nice/niceSuccess";      // 성공시 이동될 URL
        String sErrorUrl = "http://117.52.98.39:8093/nice/niceFail";         // 실패시 이동될 URL


// 입력될 plain 데이타를 만든다.
        String sPlainData = "7:REQ_SEQ" + sRequestNumber.getBytes().length + ":" + sRequestNumber +
                "8:SITECODE" + sSiteCode.getBytes().length + ":" + sSiteCode +
                "9:AUTH_TYPE" + sAuthType.getBytes().length + ":" + sAuthType +
                "7:RTN_URL" + sReturnUrl.getBytes().length + ":" + sReturnUrl +
                "7:ERR_URL" + sErrorUrl.getBytes().length + ":" + sErrorUrl +
                "11:POPUP_GUBUN" + popgubun.getBytes().length + ":" + popgubun +
                "9:CUSTOMIZE" + customize.getBytes().length + ":" + customize +
                "6:GENDER" + sGender.getBytes().length + ":" + sGender;

        String sMessage = "";
        String sEncData = "";

        int iReturn = niceCheck.fnEncode(sSiteCode, sSitePassword, sPlainData);
        if( iReturn == 0 )
        {
            sEncData = niceCheck.getCipherData();
        }
        else if( iReturn == -1)
        {
            sMessage = "암호화 시스템 에러입니다.";
        }
        else if( iReturn == -2)
        {
            sMessage = "암호화 처리오류입니다.";
        }
        else if( iReturn == -3)
        {
            sMessage = "암호화 데이터 오류입니다.";
        }
        else if( iReturn == -9)
        {
            sMessage = "입력 데이터 오류입니다.";
        }
        else
        {
            sMessage = "알수 없는 에러 입니다. iReturn : " + iReturn;
        }
        mav.addObject("sEncData",sEncData);
        mav.addObject("sMessage",sMessage);
        mav.setViewName("checkplus");
        return mav;
    }

    @ApiOperation(value = "본인인증시작")
    @PostMapping(value = "/niceSuccess")
    public ModelAndView niceSuccess(
            @RequestParam(value="EncodeData") String EncodeData,
            ModelAndView mav

    )   {
        NiceID.Check.CPClient niceCheck = new  NiceID.Check.CPClient();
        Map<String, Object> result = new HashMap<>();

        String sEncodeData = requestReplace(EncodeData, "encodeData");

        String sSiteCode = "BT052";				// NICE로부터 부여받은 사이트 코드
        String sSitePassword = "I9fpP2tGg3Ep";			// NICE로부터 부여받은 사이트 패스워드

        String sCipherTime = "";			// 복호화한 시간
        String sRequestNumber = "";			// 요청 번호
        String sResponseNumber = "";		// 인증 고유번호
        String sAuthType = "";				// 인증 수단
        String sName = "";					// 성명
        String utf8Name = "";					// 성명
        String sDupInfo = "";				// 중복가입 확인값 (DI_64 byte)
        String sConnInfo = "";				// 연계정보 확인값 (CI_88 byte)
        String sBirthDate = "";				// 생년월일(YYYYMMDD)
        String sGender = "";				// 성별
        String sNationalInfo = "";			// 내/외국인정보 (개발가이드 참조)
        String sMobileNo = "";				// 휴대폰번호
        String sMobileCo = "";				// 통신사
        String sMessage = "";
        String sPlainData = "";

        int iReturn = niceCheck.fnDecode(sSiteCode, sSitePassword, sEncodeData);

        if( iReturn == 0 )
        {
            sPlainData = niceCheck.getPlainData();
            sCipherTime = niceCheck.getCipherDateTime();

// 데이타를 추출합니다.
            java.util.HashMap mapresult = niceCheck.fnParse(sPlainData);

            sRequestNumber  = (String)mapresult.get("REQ_SEQ");
            sResponseNumber = (String)mapresult.get("RES_SEQ");
            sAuthType		= (String)mapresult.get("AUTH_TYPE");
            sName			= (String)mapresult.get("NAME");
            utf8Name			= (String)mapresult.get("UTF8_NAME"); //charset utf8 사용시 주석 해제 후 사용
            sBirthDate		= (String)mapresult.get("BIRTHDATE");
            sGender			= (String)mapresult.get("GENDER");
            sNationalInfo  	= (String)mapresult.get("NATIONALINFO");
            sDupInfo		= (String)mapresult.get("DI");
            sConnInfo		= (String)mapresult.get("CI");
            sMobileNo		= (String)mapresult.get("MOBILE_NO");
            sMobileCo		= (String)mapresult.get("MOBILE_CO");

            mav.addObject("result","success");
            mav.addObject("sMobileNo",sMobileNo);
            mav.addObject("sName",sName);
            mav.addObject("sBirthDate",sBirthDate);
//            result.put("result","success");
//            result.put("sMobileNo","sMobileNo");
//            result.put("sName","sName");
//            result.put("sBirthDate","sBirthDate");

        }else{
            mav.addObject("result","fail");
            mav.addObject("sMobileNo","");
            mav.addObject("sName","");
            mav.addObject("sBirthDate","");
//            result.put("result","fail");
//            result.put("sMobileNo","");
//            result.put("sName","");
//            result.put("sBirthDate","");
        }
//        mav.addObject("result",result);
        mav.setViewName("checkplus_success");
        return mav;

    }

    public String requestReplace (String paramValue, String gubun) {

        String result = "";

        if (paramValue != null) {

            paramValue = paramValue.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

            paramValue = paramValue.replaceAll("\\*", "");
            paramValue = paramValue.replaceAll("\\?", "");
            paramValue = paramValue.replaceAll("\\[", "");
            paramValue = paramValue.replaceAll("\\{", "");
            paramValue = paramValue.replaceAll("\\(", "");
            paramValue = paramValue.replaceAll("\\)", "");
            paramValue = paramValue.replaceAll("\\^", "");
            paramValue = paramValue.replaceAll("\\$", "");
            paramValue = paramValue.replaceAll("'", "");
            paramValue = paramValue.replaceAll("@", "");
            paramValue = paramValue.replaceAll("%", "");
            paramValue = paramValue.replaceAll(";", "");
            paramValue = paramValue.replaceAll(":", "");
            paramValue = paramValue.replaceAll("-", "");
            paramValue = paramValue.replaceAll("#", "");
            paramValue = paramValue.replaceAll("--", "");
            paramValue = paramValue.replaceAll("-", "");
            paramValue = paramValue.replaceAll(",", "");

            if(gubun != "encodeData"){
                paramValue = paramValue.replaceAll("\\+", "");
                paramValue = paramValue.replaceAll("/", "");
                paramValue = paramValue.replaceAll("=", "");
            }

            result = paramValue;

        }
        return result;
    }



}
