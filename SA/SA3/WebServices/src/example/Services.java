package example;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.regex.Pattern;
//import com.aliyuncs.http.MethodType;

@WebService
public class Services {
    @WebMethod(operationName = "sendEmail")
    public String sendEmail(String _url,String _payload) {
        System.out.println(_url+"|"+_payload+"|");
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4FxkVudwmCbuiCifABmd", "z81Wu7QTy3c5ta9cB9S86LKiaL9cWX");
        IAcsClient client = new DefaultAcsClient(profile);
        SingleSendMailRequest request = new SingleSendMailRequest();
        try {
            request.setAccountName("no-reply@service.hoxu.xyz");
            request.setFromAlias("xhofe");
            request.setAddressType(1);
            request.setTagName("控制台创建的标签");
            request.setReplyToAddress(true);
            request.setToAddress(_url);
            //可以给多个收件人发送邮件，收件人之间用逗号分开，批量发信建议使用BatchSendMailRequest方式
            //request.setToAddress("邮箱1,邮箱2");
            request.setSubject("Test");
            request.setHtmlBody(_payload);
            //开启需要备案，0关闭，1开启
            //request.setClickTrace("0");
            //如果调用成功，正常返回httpResponse；如果调用失败则抛出异常，需要在异常中捕获错误异常码；错误异常码请参考对应的API文档;
            SingleSendMailResponse httpResponse = client.getAcsResponse(request);
            return "Y";
        } catch (ServerException e) {
            //捕获错误异常码
            System.out.println("ErrCode : " + e.getErrCode());
            e.printStackTrace();
        }
        catch (ClientException e) {
            //捕获错误异常码
            System.out.println("ErrCode : " + e.getErrCode());
            e.printStackTrace();
        }
        return "N";
    }
    @WebMethod(operationName = "sendEmailBatch")
    public String sendEmailBatch(String[] _url,String _payload){
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4FxkVudwmCbuiCifABmd", "z81Wu7QTy3c5ta9cB9S86LKiaL9cWX");
        IAcsClient client = new DefaultAcsClient(profile);
        SingleSendMailRequest request = new SingleSendMailRequest();
        try {
            request.setAccountName("no-reply@service.hoxu.xyz");
            request.setFromAlias("xhofe");
            request.setAddressType(1);
            request.setTagName("控制台创建的标签");
            request.setReplyToAddress(true);
            StringBuilder sb=new StringBuilder();
            for (String addr :
                    _url) {
                sb.append(addr).append(",");
            }
            sb.deleteCharAt(sb.length()-1);
            request.setToAddress(sb.toString());
            //可以给多个收件人发送邮件，收件人之间用逗号分开，批量发信建议使用BatchSendMailRequest方式
            //request.setToAddress("邮箱1,邮箱2");
            request.setSubject("Test");
            request.setHtmlBody(_payload);
            //开启需要备案，0关闭，1开启
            //request.setClickTrace("0");
            //如果调用成功，正常返回httpResponse；如果调用失败则抛出异常，需要在异常中捕获错误异常码；错误异常码请参考对应的API文档;
            SingleSendMailResponse httpResponse = client.getAcsResponse(request);
            return "Y";
        } catch (ServerException e) {
            //捕获错误异常码
            System.out.println("ErrCode : " + e.getErrCode());
            e.printStackTrace();
        }
        catch (ClientException e) {
            //捕获错误异常码
            System.out.println("ErrCode : " + e.getErrCode());
            e.printStackTrace();
        }
        return "N";
    }
    @WebMethod(operationName = "validateEmailAddress")
    public String validateEmailAddress(String _url){
        //名字不包含中文
        String pattern1="^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        //名字包含中文
        String pattern2="^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        return Pattern.matches(pattern1,_url)? "Y":"N";
    }
//    public static void main(String[] args) {
//        String[] url={"xhofe@qq.com","709124735@qq.com"};
//        sendEmailBatch(url,"I'm your father.");
//        System.out.println(validateEmailAddress("xhofe@qq.com"));
//    }
}
