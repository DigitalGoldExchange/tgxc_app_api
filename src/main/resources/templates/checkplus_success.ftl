<!doctype html>
<html lang="ko">
<head>
    <meta charset="utf-8">
</head>
<body onload="setSubmitMessage()">
<#--<div>${sMobileCo!}|${sMobileNo!}</div>-->
<script type="text/javascript">

    function setSubmitMessage() {
        window.postMessage('${result.result}|${result.name!}|${result.utf8name!}|${result.birthDate!}|${result.mobileNo!}','*');
    }

</script>
</body>
</html>