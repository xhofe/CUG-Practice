var index=1;
var mailList=[1];
var type="SOAP";
$(function () {
    $('#addMail').click(function () {
        index=index+1;
        var flag=index;
        mailList.push(flag);
        $('#addMail').before("<div id=\"mail"+flag+"\">\n" +
            "                <div class=\"input-group mb-3\">\n" +
            "                    <input type=\"text\" class=\"form-control\" placeholder=\"Email\" id=\"name"+flag+"\" name=\"email\">\n" +
            "                    <div class=\"input-group-append\">\n" +
            "                        <span class=\"input-group-text\">@</span>\n" +
            "                    </div>\n" +
            "                    <input type=\"text\" class=\"form-control\" placeholder=\"example.com\" id=\"domain"+flag+"\" name=\"email\">\n" +
            "                    <button type=\"button\" class=\"btn btn-warning deleteMail\" id=\"delete"+flag+"\">❎</button>" +
            "                </div>\n" +
            "            </div>");
        $('#delete'+flag.toString()).click(function () {
            mailList.remove(flag);
            $('#mail'+flag.toString()).remove();
        });
    });
    $('#delete1').click(function () {
        mailList.remove(1);
        $('#mail1').remove();
    });
    $(".choose").click(function () {
        type=$(this).attr('id');
    });
    $("#ok1").click(function () {
        var data={
            _url:$("#name").val()+'@'+$("#domain").val(),
            _payload:$("#text").val()
        };
        if (data._payload===""){
            alert("内容为空");
            console.log(data);
            return;
        }
        var res;
        if (type==="SOAP"){
            res=sendMail_SOAP(data);
            if (res==="Y"){
                $("#br1").after("  " +
                    "<div class=\"alert alert-success alert-dismissible fade show\">\n" +
                    "    <button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>\n" +
                    "    <strong>成功!</strong> SOAP:邮件发送成功。\n" +
                    "  </div>");
            }
            else if (res==="N"){
                $("#br1").after("  " +
                    "<div class=\"alert alert-danger alert-dismissible fade show\">\n" +
                    "    <button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>\n" +
                    "    <strong>失败!</strong> SOAP:邮件发送失败\n" +
                    "  </div>");
            }
        }else if (type==="Rest"){
            res=sendMail_Rest(data);
            if (res==="Y"){
                $("#br1").after("  " +
                    "<div class=\"alert alert-success alert-dismissible fade show\">\n" +
                    "    <button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>\n" +
                    "    <strong>成功!</strong> Rest:邮件发送成功。\n" +
                    "  </div>");
            }
            else if (res==="N"){
                $("#br1").after("  " +
                    "<div class=\"alert alert-danger alert-dismissible fade show\">\n" +
                    "    <button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>\n" +
                    "    <strong>失败!</strong> Rest:邮件发送失败\n" +
                    "  </div>");
            }
        }
    });
    $("#ok2").click(function () {
        var mails=[];
        mailList.forEach(function (value) {
            mails.push($("#name"+value).val()+'@'+$("#domain"+value).val())
        });
        var data={
            _url:mails,
            _payload:$("#text1").val()
        };
        if (data._payload===""){
            alert("内容为空");
            console.log(data);
            return;
        }
        var res;
        if (type==="SOAP"){
            res=sendMailBatch_SOAP(data);
            if (res==="Y"){
                $("#br2").after("  " +
                    "<div class=\"alert alert-success alert-dismissible fade show\">\n" +
                    "    <button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>\n" +
                    "    <strong>成功!</strong> SOAP:邮件批量发送成功。\n" +
                    "  </div>");
            }
            else if (res==="N"){
                $("#br2").after("  " +
                    "<div class=\"alert alert-danger alert-dismissible fade show\">\n" +
                    "    <button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>\n" +
                    "    <strong>失败!</strong> SOAP:邮件批量发送失败\n" +
                    "  </div>");
            }
        }else if (type==="Rest"){
            res=sendMailBatch_Rest(data);
            if (res==="Y"){
                $("#br2").after("  " +
                    "<div class=\"alert alert-success alert-dismissible fade show\">\n" +
                    "    <button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>\n" +
                    "    <strong>成功!</strong> Rest:邮件批量发送成功。\n" +
                    "  </div>");
            }
            else if (res==="N"){
                $("#br2").after("  " +
                    "<div class=\"alert alert-danger alert-dismissible fade show\">\n" +
                    "    <button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>\n" +
                    "    <strong>失败!</strong> Rest:邮件批量发送失败\n" +
                    "  </div>");
            }
        }
    });
    $("#ok3").click(function () {
        var data={
            _url:$("#email").val()
        };
        var res;
        if (type==="SOAP"){
            res=validateEmailAddress_SOAP(data);
            if (res==="Y"){
                $("#br3").after("  " +
                    "<div class=\"alert alert-success alert-dismissible fade show\">\n" +
                    "    <button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>\n" +
                    "    <strong>成功!</strong> SOAP:此邮箱合法\n" +
                    "  </div>");
            }
            else if (res==="N"){
                $("#br3").after("  " +
                    "<div class=\"alert alert-danger alert-dismissible fade show\">\n" +
                    "    <button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>\n" +
                    "    <strong>失败!</strong> SOAP:邮箱不合法\n" +
                    "  </div>");
            }
        }else if (type==="Rest"){
            res=validateEmailAddress_Rest(data);
            if (res==="Y"){
                $("#br3").after("  " +
                    "<div class=\"alert alert-success alert-dismissible fade show\">\n" +
                    "    <button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>\n" +
                    "    <strong>成功!</strong> Rest:此邮箱合法\n" +
                    "  </div>");
            }
            else if (res==="N"){
                $("#br3").after("  " +
                    "<div class=\"alert alert-danger alert-dismissible fade show\">\n" +
                    "    <button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>\n" +
                    "    <strong>失败!</strong> Rest:邮箱不合法\n" +
                    "  </div>");
            }
        }
    });
});

Array.prototype.indexOf = function(val) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] === val) return i;
    }
    return -1;
};
Array.prototype.remove = function(val) {
    var index = this.indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
};