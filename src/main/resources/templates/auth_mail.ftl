<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0">
    <title>mailform_password</title>
</head>
<body style="height:100%;margin:0;background-color:#f8f8f8;">
<table width="100%" cellspacing="0" cellpadding="0">
    <tr>
        <td>
            <table width="100%" cellpadding="0" cellspacing="0" style="background-color:#f8f7f5;margin:0 auto;width:100%;border-bottom:1px solid #5f4f36;height:130px">
                <tr>
                    <td style="text-align:center;padding-top:60px">
                        <img src="/logo.png">
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td style="text-align:center">환영합니다! ${name!}</td>
    </tr>
    <tr>
        <td style="text-align:center">TGXC 가입을 축하드립니다!</td>
    </tr>
    <tr>
        <td style="text-align:center">가압하신 ID는 ${emailId!}입니다.</td>
    </tr>
    <tr>
        <td style="text-align:center">아래 <span style="color:#d5ad42">버튼</span>을 클릭하여 가입을 완료해 주세요.</td>
    </tr>
    <tr>
        <td style="text-align:center"><input type="button" style="background-color:#d5ad42;width:118px;height:30px;color:#fff" value="가입 완료" onclick="document.location.href='${url!}'" target="_blank"></td>
    </tr>
</table>

</body>
</html>