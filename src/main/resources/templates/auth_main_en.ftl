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
                        <img src="/assets/images/553x.png" style="width: 160px; height: 128.4px;"/>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td style="text-align:center; height: 20px; padding-bottom: 30px; padding-top: 30px">Welcome, ${name!}!</td>
    </tr>
    <tr>
        <td style="text-align:center; padding-bottom: 5px">Thank you for using TGXC.</td>
    </tr>
    <tr>
        <td style="text-align:center; padding-bottom: 5px">Your user ID is ${emailId!}.</td>
    </tr>
    <tr>
        <td style="text-align:center; padding-bottom: 10px">Please click the  <span style="color:#d5ad42">button</span> below to verify your email.</td>
    </tr>
    <tr>
        <td style="text-align:center"><input type="button" style="background-color:#d5ad42;width:118px;height:30px;color:#fff" value="Confirm" onclick="document.location.href='${url!}'" target="_blank"></td>
    </tr>
</table>

</body>
</html>