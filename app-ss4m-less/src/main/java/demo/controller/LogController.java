package demo.controller;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.common.service.ILogService;
import demo.common.vo.SysOperLog;
import io.flysium.framework.Consts;
import io.flysium.framework.message.ResponseResult;
import io.flysium.framework.vo.PageModel;

/**
 * 日志Controller
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月6日
 */
@RestController // 标识为restful风格，适合回参不跳转页面的ajax模式
@RequestMapping
public class LogController {

	@Autowired
	private ILogService logService;

	/**
	 * 测试方法：查询日志
	 * 
	 * @param params
	 * @return
	 */
	@RequestMapping
	public ResponseResult queryLogInfo(@RequestBody Map<String, Object> params) {
		ResponseResult responseResult = new ResponseResult(Consts.CodeInfoSet.CODE_00000);

		PageModel<SysOperLog> pageModel = logService.queryLogByUserId(MapUtils.getString(params, "user_id"));
		responseResult.setResult(pageModel);
		return responseResult;
	}

}
