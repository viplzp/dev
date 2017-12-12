package demo.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.flysium.framework.Consts;
import io.flysium.framework.message.ResponseResult;
import io.flysium.framework.util.encrypt.HashUtil;

/**
 * 一个简单的Controller样例（JSONP）
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月7日
 */
@RestController // 标识为restful风格，适合回参不跳转页面的ajax模式
@RequestMapping
public class JSONPDemoController {

	private static final String[] jsonpQueryParamNames = new String[]{Consts.FrontEndParamSet.JSONP_CALLBACK, "_"};

	/**
	 * jsonp只能GET（也就是传参不能太多），不能POST，参数只能使用@RequestParam 注解，因此此方法不完全兼容其他MVC
	 * Controller方法。 不过一般为其他应用，比如移动端app才会用到jsonp
	 */
	/// 分层建议，建立一个统一的JSONPController，负责转发服务
	@RequestMapping
	public Object sayHi(@RequestParam Map<String, Object> params) {
		ResponseResult responseResult = new ResponseResult(Consts.CodeInfoSet.CODE_00000);

		// 1、签名检验
		if (!check(params)) {
			responseResult = new ResponseResult(Consts.CodeInfoSet.CODE_11000.replaceMessage("message", "签名错误"));
			return responseResult;
		}

		// 2、业务处理
		String name = MapUtils.getString(params, "name");

		Map result = new HashMap();
		result.put("yourname", StringUtils.isEmpty(name) ? "" : name);
		responseResult.setResult(result);
		return responseResult;
	}

	private boolean isJsonpQueryParamNames(String paramName) {
		// 根据JSONPResponseBodyAdvice定义
		for (int j = 0; j < jsonpQueryParamNames.length; j++) {
			if (jsonpQueryParamNames[j].equals(paramName))
				return true;
		}
		return false;
	}

	private String getSignContent(Map<String, ?> params, String appsecret) {
		StringBuilder content = new StringBuilder();
		List keys = new ArrayList(params.keySet());
		Collections.sort(keys);

		Iterator it = keys.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = String.valueOf(params.get(key));
			if (isJsonpQueryParamNames(key) || Consts.FrontEndParamSet.JSONP_SIGN.equals(key)) {
				continue;
			}
			content.append(key).append("=").append(value).append("&");
		}
		content.append("&appsecret=").append(appsecret);
		return content.toString();
	}

	private String signature(Map<String, ?> params, String appsecret) {
		String content = getSignContent(params, appsecret);
		return HashUtil.md5(content);
	}

	private boolean check(Map<String, ?> params) {
		String appsecret = "A6E454315154DE4D54B1820525190899";

		String timestamp = MapUtils.getString(params, Consts.FrontEndParamSet.JSONP_TIMESTAMP);
		String appid = MapUtils.getString(params, Consts.FrontEndParamSet.JSONP_APPID);
		String sign = MapUtils.getString(params, Consts.FrontEndParamSet.JSONP_SIGN);
		if (StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(appid) || StringUtils.isEmpty(sign)) {
			return false;
		}
		String rightSign = signature(params, appsecret);
		return rightSign.equalsIgnoreCase(sign);
	}

}
