package client;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class RestClient {
    private CloseableHttpClient httpClient;
    public RestClient(){
        httpClient=HttpClients.createDefault();
    }
    public String sendEmailRest(String _url,String _payload){
        String res=null;
        String uri="http://localhost:8080/rest/Rest/sendEmail";
        List<NameValuePair> formparams=new ArrayList<>();
        formparams.add(new BasicNameValuePair("_url",_url));
        formparams.add(new BasicNameValuePair("_payload",_payload));
        UrlEncodedFormEntity entity=new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        HttpPost httpPost=new HttpPost(uri);
        httpPost.setEntity(entity);
        CloseableHttpResponse response=null;
        try {
            response=httpClient.execute(httpPost);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            HttpEntity entity1=response.getEntity();
            res= EntityUtils.toString(entity1);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
    public String sendEmailBatchRest(String[] _url,String _payload){
        String res=null;
        StringBuilder sb=new StringBuilder();
        for (String url :
                _url) {
            sb.append(url).append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        String uri="http://localhost:8080/rest/Rest/sendEmail";
        List<NameValuePair> formparams=new ArrayList<>();
        formparams.add(new BasicNameValuePair("_url",sb.toString()));
        formparams.add(new BasicNameValuePair("_payload",_payload));
        UrlEncodedFormEntity entity=new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        HttpPost httpPost=new HttpPost(uri);
        httpPost.setEntity(entity);
        CloseableHttpResponse response=null;
        try {
            response=httpClient.execute(httpPost);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            HttpEntity entity1=response.getEntity();
            res= EntityUtils.toString(entity1);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
    public String validateEmailAddressRest(String _url){
        String res=null;
        String uri="http://localhost:8080/rest/Rest/validateEmailAddress";
        List<NameValuePair> formparams=new ArrayList<>();
        formparams.add(new BasicNameValuePair("_url",_url));
        UrlEncodedFormEntity entity=new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        HttpPost httpPost=new HttpPost(uri);
        httpPost.setEntity(entity);
        CloseableHttpResponse response=null;
        try {
            response=httpClient.execute(httpPost);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            HttpEntity entity1=response.getEntity();
            res= EntityUtils.toString(entity1);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
