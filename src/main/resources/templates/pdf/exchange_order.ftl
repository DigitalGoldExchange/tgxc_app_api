<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0">
    <title>exchange_form</title>
</head>
<body style="font-family: NanumGothic;">
<div class="page">
    <div style="width: 100%">
        <div>
            <div style="text-align: center; margin-bottom:50px">TGXC 금 교환 신청서</div>
            <div style="margin-bottom:10px">신청번호 : ${exchange.reqNumber!}</div>
            <div style="margin-bottom:10px">신청일 : ${exchange.createDatetime!}</div>
            <div style="margin-bottom:10px">신청인 : ${exchange.user.name!}</div>
            <div style="margin-bottom:10px">이메일 : ${exchange.user.emailId!}</div>
            <div style="margin-bottom:15px">신청정보 : ${exchange.reqType!}, ${exchange.reqQty!}개, ${exchange.amount!}TG</div>

            <div style="margin-bottom:15px"><img src="${uploadDir!}/uploads/${userExchangeImage.identifyCardPath!}" style="width: 300px; height: 250px;" alt=""></div>
            <div style="margin-bottom:15px"><img src="${uploadDir!}/uploads/${userExchangeImage.profileImagePath!}" style="width: 300px; height: 250px;" alt=""></div>
            <div style="margin-bottom:15px">상기 신청인의 신분 및 신청된 수량, 신청번호를 확인하시어 교환을 진행하여 주시기 바랍니다.</div>
            <div style="text-align: right; margin-bottom:10px">승인 : ${tradeTime!}</div>
            <div style="text-align: right; margin-bottom:10px">작성 : ${nowTime!}</div>
            <div style="text-align: right; margin-bottom:10px">TGXC</div>
        </div>

        </div>
    </div>
</div>
</body>
</html>