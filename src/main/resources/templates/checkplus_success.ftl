<!doctype html>
<html lang="ko">
<head>
    <meta charset="utf-8">
</head>
<body onload="setSubmitMessage()">
<#--<div>${sMobileCo!}|${sMobileNo!}</div>-->
<script type="text/javascript">

    function setSubmitMessage() {
        window.postMessage('<#if result.result??>${result.result}</#if>|<#if result.name??>${result.name}</#if>|<#if result.utf8name??>${result.utf8name}</#if>|<#if result.birthDate??>${result.birthDate}</#if>|<#if result.mobileNo??>${result.mobileNo}</#if>','*');
    }

</script>
</body>
</html>