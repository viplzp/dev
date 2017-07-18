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
package com.baomidou.kisso.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baomidou.kisso.SSOConfig;
import com.baomidou.kisso.SSOHelper;
import com.baomidou.kisso.SSOToken;
import com.baomidou.kisso.Token;
import com.baomidou.kisso.annotation.Action;
import com.baomidou.kisso.annotation.Login;
import com.baomidou.kisso.common.util.HttpUtil;
import com.baomidou.kisso.web.waf.request.WafRequestWrapper;

/**
 * <p>
 * 登录
 * </p>
 * 
 * @author hubin
 * @date 2015年3月25日
 * @version 1.0.0
 */
@Controller
public class LoginController extends BaseController {

	/**
	 * 登录 （注解跳过权限验证）
	 */
	@Login(action = Action.Skip)
	@RequestMapping("/login")
	public String login(Model model) {
		String returnUrl = request.getParameter(SSOConfig.getInstance().getParamReturl());
		Token token = SSOHelper.getToken(request);
		if (token == null) {
			/**
			 * 正常登录 需要过滤sql及脚本注入
			 */
			WafRequestWrapper wr = new WafRequestWrapper(request);
			String name = wr.getParameter("userid");
			if (name != null && !"".equals(name)) {
				/*
				 * 设置登录 Cookie
				 * 最后一个参数 true 时添加 cookie 同时销毁当前 JSESSIONID 创建信任的 JSESSIONID
				 */
				SSOToken st = new SSOToken(request, "1000");
				SSOHelper.setSSOCookie(request, response, st, true);

				// 重定向到指定地址 returnUrl
				if (StringUtils.isEmpty(returnUrl)) {
					returnUrl = "/index.html";
				} else {
					returnUrl = HttpUtil.decodeURL(returnUrl);
				}
				return redirectTo(returnUrl);
			} else {
				if (StringUtils.isNotEmpty(returnUrl)) {
					model.addAttribute("ReturnURL", returnUrl);
				}
				return "login";
			}
		} else {
			if (StringUtils.isEmpty(returnUrl)) {
				returnUrl = "/index.html";
			}
			return redirectTo(returnUrl);
		}
	}
}