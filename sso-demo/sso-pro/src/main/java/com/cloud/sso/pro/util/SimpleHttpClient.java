/*************************************************************************************
 * Copyright (C) 2015 Shenzhen Zhubaodai Internet Financial Services Co.,Ltd
 * All Rights Reserved.
 * 本软件为深圳市珠宝贷互联网金融服务股份有限公司开发研制。未经本公司正式书面同意，其他任何个人、团体不得使用、复制、修改或发布本软件。
 *************************************************************************************/
 /*************************************************************************************
 * Copyright (C) 2015 Shenzhen Zhubaodai Internet Financial Services Co.,Ltd
 * All Rights Reserved.
 * 本软件为深圳市珠宝贷互联网金融服务股份有限公司开发研制。未经本公司正式书面同意，其他任何个人、团体不得使用、复制、修改或发布本软件。
 *************************************************************************************/
	

 package com.cloud.sso.pro.util;
 import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.cookie.BasicClientCookie;

 /**
  * 简易http请求
  * @author rubekid
  * @date 2016年10月11日
  */
 public class SimpleHttpClient {

     private static CloseableHttpClient httpClient;
 	private static String serverLogin="http://localhost:8081/sso/user/loginPage";

     
     private static CookieStore cookieStore;

     static {
         cookieStore  = new BasicCookieStore();

         // 将CookieStore设置到httpClient中
         httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
     }
     
     public static String getCookie(String name){
         List<Cookie> cookies =  cookieStore.getCookies();
         for(Cookie cookie : cookies){
             if(cookie.getName().equalsIgnoreCase(name)){
                 return cookie.getValue();
             }
         }
         return null;
         
     }

     /**
      * GET 请求
      * @param url
      * @return
      * @throws ClientProtocolException
      * @throws IOException
      */
     public static String get(String url) throws ClientProtocolException, IOException {
         HttpGet httpGet = new HttpGet(url);
         HttpResponse httpResponse = httpClient.execute(httpGet);
         return EntityUtils.toString(httpResponse.getEntity());
     }

     /**
      * POST 请求
      * @param url
      * @param params
      * @param headers
      * @return
      * @throws ParseException
      * @throws IOException
      */
     public static String post(String url, Map<String, Object> params, Map<String, String> headers) throws ParseException, IOException{
         HttpPost httpPost = new HttpPost(url);
         UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(getParam(params), "UTF-8");
         httpPost.setEntity(postEntity);
         if(headers != null){
             addHeaders(httpPost, headers);
         }
         HttpResponse httpResponse = httpClient.execute(httpPost);
         return EntityUtils.toString(httpResponse.getEntity());
     }

     /**
      * 参数
      * @param parameterMap
      * @return
      */
     private static List<NameValuePair> getParam(Map<String, Object> parameterMap) {
         List<NameValuePair> param = new ArrayList<NameValuePair>();
         for(Map.Entry<String, Object> entry : parameterMap.entrySet()){
             param.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
         }
         return param;
     }
     
     /**
      * 添加头
      * @param httpRequest
      * @param headerMap
      */
     private static void addHeaders(HttpEntityEnclosingRequestBase httpRequest, Map<String, String> headerMap){
         for(Map.Entry<String, String> entry : headerMap.entrySet()){
             httpRequest.addHeader(entry.getKey(), entry.getValue());
         }
     }
     public static void main(String[] args) throws ClientProtocolException {  
        /* CookieStore cookieStore = new BasicCookieStore();  
         CloseableHttpClient httpClient = HttpClients.custom()  
                  .setDefaultCookieStore(cookieStore)  
                  .build();  
          try {  
               
               HttpPost post = new HttpPost(serverLogin);  
               BasicClientCookie cookie = new BasicClientCookie("name", "zhaoke");   
               cookie.setVersion(0);    
               cookie.setDomain("/pms/");   //设置范围  
               cookie.setPath("/");   
               cookieStore.addCookie(cookie);  
               httpClient.execute(post);//  
               List<Cookie> cookies = cookieStore.getCookies();  
               for (int i = 0; i < cookies.size(); i++) {  
                   System.out.println("Local cookie: " + cookies.get(i));  
               }  
             } catch (Exception e) {  
                 e.printStackTrace();  
             }finally{  
                   
             }  */
    	 try {
			Map<String, Object> params=new HashMap<String, Object>();
			params.put("name", "cl");
			Map<String, String> headers = null;
			String result = SimpleHttpClient.post(serverLogin, params, headers);
			System.out.println("==="+result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
				
		}
         }  
 }