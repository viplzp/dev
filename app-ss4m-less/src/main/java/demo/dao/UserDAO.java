package demo.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import demo.common.dao.IUserDAO;
import demo.common.vo.User;
import io.flysium.framework.util.jdbc.DaoUtils;
import io.flysium.framework.vo.PageModel;

/**
 * 用户DAO
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月8日
 */
@Repository // DAO层注解
public class UserDAO implements IUserDAO {

	/**
	 * 查询用户信息
	 */
	@Override
	public PageModel<User> queryUser(Map params) {
		PageModel<User> pageModel = DaoUtils.selectPageModel("User.queryUserByUserName", params);
		return pageModel;
	}

}
