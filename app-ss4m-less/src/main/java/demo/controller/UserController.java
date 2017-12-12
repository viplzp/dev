package demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.common.service.IUserService;
import demo.common.vo.User;
import io.flysium.framework.Consts;
import io.flysium.framework.message.ResponseResult;
import io.flysium.framework.vo.PageModel;

/**
 * 用户Controller
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月6日
 */
@RestController // 标识为restful风格，适合回参不跳转页面的ajax模式
@RequestMapping
public class UserController {

	@Autowired
	private IUserService userService;

	/**
	 * 查询用户信息
	 * 
	 * @param params
	 * @return
	 */
	@RequestMapping
	public ResponseResult queryUserInfo(@RequestBody Map<String, Object> params) {
		ResponseResult responseResult = new ResponseResult(Consts.CodeInfoSet.CODE_00000);
		PageModel<User> pageModel = userService.queryUser(params);
		responseResult.setResult(pageModel);
		return responseResult;
	}

}
