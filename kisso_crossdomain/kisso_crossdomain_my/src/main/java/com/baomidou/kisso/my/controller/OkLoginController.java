/**
 * Copyright (c) 2011-2014, hubin (243194995@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.kisso.my.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.kisso.AuthToken;
import com.baomidou.kisso.SSOConfig;
import com.baomidou.kisso.SSOHelper;
import com.baomidou.kisso.SSOToken;
import com.baomidou.kisso.common.SSOProperties;

/**
 * 
 * 考虑到 浏览器 url 地址长度限制，跨域不采用地址重定向方式、依旧使用  jsonp 跨域。
 * 
 * <p>
 * IE6.0               		:url最大长度2083个字符，超过最大长度后无法提交。
 * IE7.0               		:url最大长度2083个字符，超过最大长度后仍然能提交，但是只能传过去2083个字符。
 * firefox 3.0.3     		:url最大长度7764个字符，超过最大长度后无法提交。
 * Opera 9.52       		:url最大长度7648个字符，超过最大长度后无法提交。
 * Google Chrome 2.0.168    :url最大长度7713个字符，超过最大长度后无法提交
 * </p>
 * 
 * @author hubin
 * @date 2015年3月25日
 * @version 1.0.0
 */
@Controller
public class OkLoginController extends BaseController {

	/**
	 * 跨域登录成功
	 */
	@ResponseBody
	@RequestMapping("/oklogin")
	public void oklogin() {
		String returl = "http://my.web.com:8091/timeout.html";
		/*
		 * <p>
		 * 回复密文是否存在
		 * </p>
		 * <p>
		 * SSO 公钥验证回复密文是否正确
		 * </p>
		 * <p>
		 * 设置 MY 系统自己的 Cookie
		 * </p>
		 */
		String replyTxt = request.getParameter("replyTxt");
		if (replyTxt != null && !"".equals(replyTxt)) {
			/**
			 * 
			 * 用户自定义配置获取
			 * 
			 * <p>
			 * 由于不确定性，kisso 提倡，用户自己定义配置。
			 * </p>
			 * 
			 */
			SSOProperties prop = SSOConfig.getSSOProperties();
			
			AuthToken at = SSOHelper.ok(request, response, replyTxt, prop.get("sso.defined.my_public_key"),
				prop.get("sso.defined.sso_public_key"));
			if (at != null) {
				returl = "http://my.web.com:8091/index.html";
				SSOToken st = new SSOToken();
				st.setUid(at.getUid());
				st.setTime(at.getTime());
				
				/*
				 * 设置 true 时添加 cookie 同时销毁当前 JSESSIONID 创建信任的 JSESSIONID
				 */
				SSOHelper.setSSOCookie(request, response, st, true);
			}
		}
		try {
			AjaxHelper.outPrint(response, "{\"returl\":\"" + returl + "\"}", "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
