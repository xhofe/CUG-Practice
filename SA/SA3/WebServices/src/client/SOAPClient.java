package client;

import org.apache.axis.utils.StringUtils;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;


public class SOAPClient {
    //实现WebService上发布的服务调用
    public String CallSOAPMethod(String url, String method, Object[] args) {
        String result = null;
        if(StringUtils.isEmpty(url)) {
            return "url地址为空";
        }
        if(StringUtils.isEmpty(method)) {
            return "method地址为空";
        }
        Call rpcCall = null;
        try {
            //实例websevice调用实例
            Service webService = new Service();
            rpcCall = (Call) webService.createCall();
            rpcCall.setTargetEndpointAddress(new java.net.URL(url));
            rpcCall.setOperationName(method);
            //执行webservice方法
            result = rpcCall.invoke(args).toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public String sendEmailSOAP(String _url,String _payload){
        String url = "http://localhost:8080/services/SOAPServices?wsdl";
        //调用的方法
        String method = "sendEmail";
        //调用方法的参数列表
        Object[] parms = new Object[]{_url,_payload};
        //调用方法
        return CallSOAPMethod(url, method, parms);
    }

    public String sendEmailBatchSOAP(String[] _url,String _payload){
        String url = "http://localhost:8080/services/SOAPServices?wsdl";
        //调用的方法
        String method = "sendEmailBatch";
        //调用方法的参数列表
        Object[] parms = new Object[]{_url,_payload};
        //调用方法
        return CallSOAPMethod(url, method, parms);
    }

    public String  validateEmailAddressSOAP(String _url){
        String url = "http://localhost:8080/services/SOAPServices?wsdl";
        //调用的方法
        String method = "validateEmailAddress";
        //调用方法的参数列表
        Object[] parms = new Object[]{_url};
        //调用方法
        return CallSOAPMethod(url, method, parms);
    }
}