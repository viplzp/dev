package demo.common.service;

import java.util.Map;

import demo.common.vo.User;
import io.flysium.framework.vo.PageModel;

/**
 * 用户服务
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月5日
 */
public interface IUserService {

	/**
	 * 查询用户信息列表
	 * 
	 * @param params
	 * @return
	 */
	public PageModel<User> queryUser(Map params);

}