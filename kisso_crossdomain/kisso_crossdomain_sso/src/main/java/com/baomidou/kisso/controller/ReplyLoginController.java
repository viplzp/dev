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

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.kisso.AuthToken;
import com.baomidou.kisso.SSOConfig;
import com.baomidou.kisso.SSOHelper;
import com.baomidou.kisso.Token;
import com.baomidou.kisso.common.SSOProperties;

/**
 * <p>
 * 应答是否登录
 * </p>
 * 
 * @author hubin
 * @date 2015年3月25日
 * @version 1.0.0
 */
@Controller
public class ReplyLoginController extends BaseController {

	/**
	 * 回复子系统是否登录
	 */
	@ResponseBody
	@RequestMapping("/replylogin")
	public void replylogin() {
		StringBuffer replyData = new StringBuffer();
		replyData.append(request.getParameter("callback")).append("({\"msg\":\"");
		Token token = SSOHelper.getToken(request);
		if (token != null) {
			String askData = request.getParameter("askData");
			if (askData != null && !"".equals(askData)) {
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
				
				//下面开始验证票据，签名新的票据每一步都必须有。
				AuthToken at = SSOHelper.replyCiphertext(request, askData);
				if (at != null) {
					
					//1、业务系统公钥验证签名合法性（此处要支持多个跨域端，取 authToken 的 app 名找到对应系统公钥验证签名）
					at = at.verify(prop.get("sso.defined." + at.getApp() + "_public_key"));
					if (at != null) {
						
						//at.getUuid() 作为 key 设置 authToken 至分布式缓存中，然后 sso 系统二次验证
						//at.setData(data); 设置自定义信息，当然你也可以直接 at.setData(token.jsonToken()); 把当前 SSOToken 传过去。
						
						at.setUid(token.getUid());//设置绑定用户ID
						at.setTime(token.getTime());//设置登录时间
						
						//2、SSO 的私钥签名
						at.sign(prop.get("sso.defined.sso_private_key"));
						
						//3、生成回复密文票据
						replyData.append(at.encryptAuthToken());
					} else {
						//非法签名, 可以重定向至无权限界面，自己处理
						replyData.append("-2");
					}
				} else {
					//非法签名, 可以重定向至无权限界面，自己处理
					replyData.append("-2");
				}
			}
		} else {
			// 未登录
			replyData.append("-1");
		}
		try {
			replyData.append("\"})");
			AjaxHelper.outPrint(response, replyData.toString(), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
