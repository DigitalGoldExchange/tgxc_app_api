<!doctype html>
<html lang="ko">
<head>
    <meta charset="utf-8">
    <meta http-Equiv="Cache-Control" Content="no-cache" />
    <meta http-Equiv="Pragma" Content="no-cache" />
    <meta http-Equiv="Expires" Content="0" />
</head>
<body onload="goNice()">
<form name="form_chk" method="post">
    <input type="hidden" name="m" value="checkplusService">
    <input type="hidden" name="EncodeData" value="${sEncData!}">
</form>
<script>
    function goNice(){
        document.form_chk.action = "http://nice.checkplus.co.kr/CheckPlusSafeModel/checkplus.cb";
        document.form_chk.submit();
    }
</script>
</body>
</html>