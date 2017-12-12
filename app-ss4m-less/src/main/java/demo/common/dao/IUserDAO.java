package demo.common.dao;

import java.util.Map;

import demo.common.vo.User;
import io.flysium.framework.vo.PageModel;

/**
 * 用户DAO
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月8日
 */
public interface IUserDAO {

	/**
	 * 查询用户信息
	 * 
	 * @param params
	 * @return
	 */
	public PageModel<User> queryUser(Map params);
}
