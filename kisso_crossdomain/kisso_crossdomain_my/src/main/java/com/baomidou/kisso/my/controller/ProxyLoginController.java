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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baomidou.kisso.AuthToken;
import com.baomidou.kisso.SSOConfig;
import com.baomidou.kisso.SSOHelper;
import com.baomidou.kisso.common.SSOProperties;

/**
 * <p>
 * 代理登录，跨域中间状态处理
 * </p>
 * 
 * @author hubin
 * @Date 2014-12-23
 */
@Controller
public class ProxyLoginController extends BaseController {

	/**
	 * 跨域登录
	 */
	@RequestMapping("/proxylogin")
	public String proxylogin(Model model) {
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
		System.out.println(prop.get("sso.defined.my_private_key"));
		//业务系统私钥签名 authToken 自动设置临时会话 cookie 授权后自动销毁
		AuthToken at = SSOHelper.askCiphertext(request, response, prop.get("sso.defined.my_private_key"));
		
		//at.getUuid() 作为 key 设置 authToken 至分布式缓存中，然后 sso 系统二次验证
		
		//askurl 询问 sso 是否登录地址
		model.addAttribute("askurl", prop.get("sso.defined.askurl"));
		
		//askTxt 询问 token 密文
		model.addAttribute("askData", at.encryptAuthToken());
		
		//my 确定是否登录地址
		model.addAttribute("okurl", prop.get("sso.defined.oklogin"));
		return "proxylogin";
	}
	
}