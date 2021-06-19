sendMail_SOAP=function (_data) {
    console.log(_data);
    var response;
    var _payload = "" +
        "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
        "    <Body>\n" +
        "        <sendEmail xmlns=\"http://soap\">\n" +
        "            <_url>" + _data._url + "</_url>\n" +
        "            <_payload>" + _data._payload + "</_payload>\n" +
        "        </sendEmail>\n" +
        "    </Body>\n" +
        "</Envelope>";

    $.ajax({
        async: false,
        crossDomain: true,
        url: "http://localhost:8080/services/SOAPServices",
        method: "POST",
        headers: {
            "content-type": "text/xml",
            "soapaction": "\\\"\\\"",
            "cache-control": "no-cache"
        },
        data: _payload,
        type: "post",   //请求方式,
        beforeSend: function () {
            //请求前的处理
        },
        success: function (data) {
            //请求成功时处理
            response = data.getElementsByTagName("sendEmailReturn")[0].childNodes[0].textContent;
        },
        error: function () {
            //请求出错处理
            alert("Ajax Error")
            return null;
        }
    });
    return response;
};

sendMailBatch_SOAP=function (_data) {
    console.log(_data);
    console.log(_data);
    var response;
    var urls="";
    _data._url.forEach(function (value) {
        urls=urls+"<_url>"+value+"</_url>\n";
    });
    var _payload ="" +
        "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
        "    <Body>\n" +
        "        <sendEmailBatch xmlns=\"http://SOAP\">\n" +
        "            <_url>\n" +
        urls+
        "            </_url>\n" +
        "            <_payload>"+_data._payload+"</_payload>\n" +
        "        </sendEmailBatch>\n" +
        "    </Body>\n" +
        "</Envelope>";

    $.ajax({
        async: false,
        crossDomain: true,
        url: "http://localhost:8080/services/SOAPServices",
        method: "POST",
        headers: {
            "content-type": "text/xml",
            "soapaction": "\\\"\\\"",
            "cache-control": "no-cache"
        },
        data: _payload,
        type: "post",   //请求方式,
        beforeSend: function () {
            //请求前的处理
        },
        success: function (data) {
            //请求成功时处理
            response = data.getElementsByTagName("sendEmailBatchReturn")[0].childNodes[0].textContent;
        },
        error: function () {
            //请求出错处理
            alert("Ajax Error");
            return null;
        }
    });
    return response;
};

validateEmailAddress_SOAP=function (_data) {
    console.log(_data);
    var response;
    var _payload = "" +
        "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
        "    <Body>\n" +
        "        <validateEmailAddress xmlns=\"http://soap\">\n" +
        "            <_url>" + _data._url + "</_url>\n" +
        "        </validateEmailAddress>\n" +
        "    </Body>\n" +
        "</Envelope>";

    $.ajax({
        // async: true,
        crossDomain: true,
        url: "http://localhost:8080/services/SOAPServices",
        method: "POST",
        headers: {
            "content-type": "text/xml",
            "soapaction": "\\\"\\\"",
            "cache-control": "no-cache"
        },
        data: _payload,
        type: "post",   //请求方式,
        async:false,
        beforeSend: function () {
            //请求前的处理
        },
        success: function (data) {
            //请求成功时处理
            response = data.getElementsByTagName("validateEmailAddressReturn")[0].childNodes[0].textContent;
        },
        error: function () {
            //请求出错处理
            alert("Ajax Error")
        }
    });
    console.log(response);
    return response;
};

sendMail_Rest=function (_data) {
    console.log(_data);
    var response;
    $.ajax({
        "async": false,
        "crossDomain": true,
        "url": "http://localhost:8080/rest/Rest/sendEmail?_url=" + _data._url + "&_payload=" + _data._payload,
        "method": "POST",
        "headers": {
            "content-type": "application/x-www-form-urlencoded",
            "cache-control": "no-cache"
        },
        success: function (data) {
            //请求成功时处理
            response = data;
        },
        error:function () {
            alert("Ajax Error");
        }
    });
    return response;
};

sendMailBatch_Rest=function (_data) {
    console.log(_data);
    var urls="";
    _data._url.forEach(function (value) {
        urls=urls+value;
        urls=urls+',';
    });
    urls=urls.substring(0,urls.length-1);
    var response;
    $.ajax({
        "async": false,
        "crossDomain": true,
        "url": "http://localhost:8080/rest/Rest/sendEmail?_url=" + urls + "&_payload=" + _data._payload,
        "method": "POST",
        "headers": {
            "content-type": "application/x-www-form-urlencoded",
            "cache-control": "no-cache"
        },
        success: function (data) {
            //请求成功时处理
            response = data;
        },
        error:function () {
            alert("Ajax Error");
        }
    });
    return response;
};

validateEmailAddress_Rest=function (_data) {
    console.log(_data);
    var response;
    $.ajax({
        "async": false,
        "crossDomain": true,
        "url": "http://localhost:8080/rest/Rest/validateEmailAddress?_url=" + _data._url,
        "method": "POST",
        "headers": {
            "content-type": "application/x-www-form-urlencoded",
            "cache-control": "no-cache"
        },
        success: function (data) {
            //请求成功时处理
            response = data;
        },
        error:function () {
            alert("Ajax Error");
        }
    });
    return response;
};
// sendMail_SOAP=function (_data) {
//     console.log(_data);
//     var response;
//     var _payload = "" +
//         "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
//         "    <Body>\n" +
//         "        <sendEmail xmlns=\"http://soap\">\n" +
//         "            <_url>" + _data._url + "</_url>\n" +
//         "            <_payload>" + _data._payload + "</_payload>\n" +
//         "        </sendEmail>\n" +
//         "    </Body>\n" +
//         "</Envelope>";
//
//     $.ajax({
//         async: false,
//         crossDomain: true,
//         url: "http://54.224.98.206:8080/WebServices_war/services/SOAPServices",
//         method: "POST",
//         headers: {
//             "content-type": "text/xml",
//             "soapaction": "\\\"\\\"",
//             "cache-control": "no-cache"
//         },
//         data: _payload,
//         type: "post",   //请求方式,
//         beforeSend: function () {
//             //请求前的处理
//         },
//         success: function (data) {
//             //请求成功时处理
//             response = data.getElementsByTagName("sendEmailReturn")[0].childNodes[0].textContent;
//         },
//         error: function () {
//             //请求出错处理
//             alert("Ajax Error");
//             return null;
//         }
//     });
//     return response;
// };
//
// sendMailBatch_SOAP=function (_data) {
//     console.log(_data);
//     console.log(_data);
//     var response;
//     var urls="";
//     _data._url.forEach(function (value) {
//         urls=urls+"<_url>"+value+"</_url>\n";
//     });
//     var _payload ="" +
//         "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
//         "    <Body>\n" +
//         "        <sendEmailBatch xmlns=\"http://SOAP\">\n" +
//         "            <_url>\n" +
//                         urls+
//         "            </_url>\n" +
//         "            <_payload>"+_data._payload+"</_payload>\n" +
//         "        </sendEmailBatch>\n" +
//         "    </Body>\n" +
//         "</Envelope>";
//
//     $.ajax({
//         async: false,
//         crossDomain: true,
//         url: "http://54.224.98.206:8080/WebServices_war/services/SOAPServices",
//         method: "POST",
//         headers: {
//             "content-type": "text/xml",
//             "soapaction": "\\\"\\\"",
//             "cache-control": "no-cache"
//         },
//         data: _payload,
//         type: "post",   //请求方式,
//         beforeSend: function () {
//             //请求前的处理
//         },
//         success: function (data) {
//             //请求成功时处理
//             response = data.getElementsByTagName("sendEmailBatchReturn")[0].childNodes[0].textContent;
//         },
//         error: function () {
//             //请求出错处理
//             alert("Ajax Error");
//             return null;
//         }
//     });
//     return response;
// };
//
// validateEmailAddress_SOAP=function (_data) {
//     console.log(_data);
//     var response;
//     var _payload = "" +
//         "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
//         "    <Body>\n" +
//         "        <validateEmailAddress xmlns=\"http://soap\">\n" +
//         "            <_url>" + _data._url + "</_url>\n" +
//         "        </validateEmailAddress>\n" +
//         "    </Body>\n" +
//         "</Envelope>";
//
//     $.ajax({
//         // async: true,
//         crossDomain: true,
//         url: "http://54.224.98.206:8080/WebServices_war/services/SOAPServices",
//         method: "POST",
//         headers: {
//             "content-type": "text/xml",
//             "soapaction": "\\\"\\\"",
//             "cache-control": "no-cache"
//         },
//         data: _payload,
//         type: "post",   //请求方式,
//         async:false,
//         beforeSend: function () {
//             //请求前的处理
//         },
//         success: function (data) {
//             //请求成功时处理
//             response = data.getElementsByTagName("validateEmailAddressReturn")[0].childNodes[0].textContent;
//         },
//         error: function () {
//             //请求出错处理
//             alert("Ajax Error")
//         }
//     });
//     console.log(response);
//     return response;
// };
//
// sendMail_Rest=function (_data) {
//     console.log(_data);
//     var response;
//     $.ajax({
//         "async": false,
//         "crossDomain": true,
//         "url": "http://54.224.98.206:8080/WebServices_war/rest/Rest/sendEmail?_url=" + _data._url + "&_payload=" + _data._payload,
//         "method": "POST",
//         "headers": {
//             "content-type": "application/x-www-form-urlencoded",
//             "cache-control": "no-cache"
//         },
//         success: function (data) {
//             //请求成功时处理
//             response = data;
//         },
//         error:function () {
//             alert("Ajax Error");
//         }
//     });
//     return response;
// };
//
// sendMailBatch_Rest=function (_data) {
//     console.log(_data);
//     var urls="";
//     _data._url.forEach(function (value) {
//         urls=urls+value;
//         urls=urls+',';
//     });
//     urls=urls.substring(0,urls.length-1);
//     var response;
//     $.ajax({
//         "async": false,
//         "crossDomain": true,
//         "url": "http://54.224.98.206:8080/WebServices_war/rest/Rest/sendEmail?_url=" + urls + "&_payload=" + _data._payload,
//         "method": "POST",
//         "headers": {
//             "content-type": "application/x-www-form-urlencoded",
//             "cache-control": "no-cache"
//         },
//         success: function (data) {
//             //请求成功时处理
//             response = data;
//         },
//         error:function () {
//             alert("Ajax Error");
//         }
//     });
//     return response;
// };
//
// validateEmailAddress_Rest=function (_data) {
//     console.log(_data);
//     var response;
//     $.ajax({
//         "async": false,
//         "crossDomain": true,
//         "url": "http://54.224.98.206:8080/WebServices_war/rest/Rest/validateEmailAddress?_url=" + _data._url,
//         "method": "POST",
//         "headers": {
//             "content-type": "application/x-www-form-urlencoded",
//             "cache-control": "no-cache"
//         },
//         success: function (data) {
//             //请求成功时处理
//             response = data;
//         },
//         error:function () {
//             alert("Ajax Error");
//         }
//     });
//     return response;
// };