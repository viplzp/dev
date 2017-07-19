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
	

 package com.cloud.sso.oa.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieStore;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
 /**
 * 类名: HttpUtil
 * 类描述: TODO (用一句话描述这个类做什么).
 * 创建日期: 2017-7-17 下午6:04:56
 * @author lzp
 * @version
 * @since JDK 1.6
 * @see
 */
/**
 * 类名: HttpUtil
 * 类描述: TODO (用一句话描述这个类做什么).
 * 创建日期: 2017-7-17 下午6:04:56
 *
 * @author lzp
 * @version 
 * @since JDK 1.6
 */

public class HttpUtil {

	/**
     * HttpClient  post请求
     * @param url
     * @param params
     * @param charset
     * @return
     */
    public static String sendPost(String url, Map<String, String> params,String charset,String outTyp) {
       StringBuffer result = new StringBuffer();
       HttpClient client = new HttpClient();
       PostMethod method = new PostMethod(url);
       method.addRequestHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
       method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
//       method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
//       client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY); 
//       client.getParams().setParameter("http.protocol.single-cookie-header", true); 

/*       method.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
       method.getParams().setContentCharset("UTF-8");

       method.setRequestHeader("Cookie", "special-cookie=value");*/
    //   method.setRequestHeader("Charset", "UTF-8");
       // 设置Http Post数据
       if (params != null) {
            HttpMethodParams p = new HttpMethodParams();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                 p.setParameter(entry.getKey(), entry.getValue());
                 System.out.println(entry.getKey()+"=="+entry.getValue());
            }
//            method.setParams(p);
            method.addParameter("username", "cloud");
            method.addParameter("password", "cloud");

       }
       try {
            Cookie setcookie =new Cookie("localhost", "sso", "o54u7r5t5y1v8v4ycyhg0g6");
            client.getState().addCookie(setcookie); 
            client.executeMethod(method);
//            client.getState().setCookiePolicy(0);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                Cookie[] cookies = client.getState().getCookies(); 
                for (Cookie cookie : cookies) {
                	System.out.println("获取sso cookie:"+cookie);
                	/*if ("sso".equals(cookie.getName())) {
                		result.append(cookie.getValue());
                		break;
                	}			*/			
                }
            	if("1".equals(outTyp)){
            		/*Cookie[] cookies = client.getState().getCookies(); 
            		for (Cookie cookie : cookies) {
            			if ("sso".equals(cookie.getName())) {
            				result.append(cookie.getValue());
            				break;
            			}						
					}*/
            	}else{
            		BufferedReader reader = new BufferedReader(
            				new InputStreamReader(method.getResponseBodyAsStream(),
            						charset));
            		String line;
            		while ((line = reader.readLine()) != null) {
            			result.append(line);
            		}
            		reader.close();
            	}
            }
       } catch (IOException e) {
            System.out.println("执行HTTP Post请求" + url + "时，发生异常！");
       } finally {
            method.releaseConnection();
       }
       return result.toString();
  }
   
}

	