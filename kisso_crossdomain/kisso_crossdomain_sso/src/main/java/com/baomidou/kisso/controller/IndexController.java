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

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.baomidou.kisso.SSOHelper;
import com.baomidou.kisso.SSOToken;

/**
 * <p>
 * </p>
 * 
 * @author hubin
 * @date 2015年3月25日
 * @version 1.0.0
 */
@Controller
public class IndexController extends BaseController {

	@RequestMapping("/index")
	public String index(Model model) {
		SSOToken st = SSOHelper.getToken(request);
		if (st == null) {
			try {
				response.sendRedirect("/login.html");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		} else {
			model.addAttribute("userId", st.getUid());
		}

		return "index";
	}

}