package net.gfeng;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TestHttpClient {


    public static void upload() {
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("http://localhost:8080/testMvnWeb/FileUpLoadServlet");
            httpPost.setConfig(RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000).build());
            httpPost.setEntity(MultipartEntityBuilder.create()
                    .addBinaryBody("file",new File("C:\\tmp\\VID_20181210_202253.mp4"))
                    .addTextBody("comment", "this is comment").build());
            System.out.println("executing request [" + httpPost.getRequestLine()+"]");

            CloseableHttpResponse response = null;
            try {
                response = httpclient.execute(httpPost);

                System.out.println("response: [" + response.getStatusLine()+"]");
                System.out.println("statusCode: [" + response.getStatusLine().getStatusCode()+"]");
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    System.out.println("responseEntry: [" + EntityUtils.toString(responseEntity,"UTF-8")+"]");
                }
                EntityUtils.consume(responseEntity);
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {httpclient.close();} catch (Exception e) {}
        }
    }

    public String post() throws  IOException{
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse httpResponse = null;
        HttpPost httpPost = new HttpPost("http://localhost:8080/testMvnWeb/FileUpLoadServlet");
        httpPost.setConfig(RequestConfig.custom().setConnectTimeout(20000).setSocketTimeout(20000).build());
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("user.loginId", "Lin"));
        params.add(new BasicNameValuePair("user.employeeName","令狐冲"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Charset.forName("UTF-8"));
        httpPost.setEntity(entity);
        httpResponse = httpClient.execute(httpPost);
        HttpEntity responseEntity = httpResponse.getEntity();
        if(responseEntity!=null){
            System.out.println(EntityUtils.toString(responseEntity,"UTF-8"));
        }

        if(httpResponse!=null){
            httpResponse.close();
        }
        if(httpClient!=null){
            httpClient.close();
        }
        return null;
    }

    public static void main( String[] args )
    {
        new TestHttpClient().upload();
    }
}
