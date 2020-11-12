<!doctype html>
<html lang="ko">
<head>
    <meta charset="utf-8">
</head>
<body onload="setSubmitMessage()">
<#--<div>${result!}|${sMobileNo!}|${sName!}|${sBirthDate!}</div>-->
<script type="text/javascript">

    function setSubmitMessage() {
         <#--window.postMessage('${result!}|${sMobileNo!}|${sName!}|${sBirthDate!}','*');-->
        window.ReactNativeWebView.postMessage('${result!}|${sMobileNo!}|${sName!}|${sBirthDate!}');
    }

</script>
</body>
</html>
