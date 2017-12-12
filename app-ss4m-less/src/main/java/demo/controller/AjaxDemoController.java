package demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.flysium.framework.Consts;
import io.flysium.framework.message.ResponseResult;

/**
 * 一个简单的Controller样例（Html + Ajax/JQuery）
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月2日
 */
@RestController // 标识为restful风格，适合回参不跳转页面的ajax模式
@RequestMapping
public class AjaxDemoController {

	/**
	 * 测试方法：简单输出欢迎用语
	 * 
	 * @param params
	 * @return
	 */
	@RequestMapping
	public ResponseResult sayHi(@RequestBody Map<String, Object> params) {
		ResponseResult responseResult = new ResponseResult(Consts.CodeInfoSet.CODE_00000);
		String name = MapUtils.getString(params, "name");

		Map result = new HashMap();
		result.put("yourname", StringUtils.isEmpty(name) ? "" : name);
		responseResult.setResult(result);
		return responseResult;
	}

}
