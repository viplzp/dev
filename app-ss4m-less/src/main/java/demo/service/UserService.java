package demo.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import demo.common.dao.IUserDAO;
import demo.common.service.IUserService;
import demo.common.vo.User;
import io.flysium.framework.vo.PageModel;

/**
 * 用户服务
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月5日
 */
@Service
public class UserService implements IUserService {

	@Autowired
	private IUserDAO userDAO;

	/**
	 * 查询用户信息
	 */
	@Transactional
	public PageModel<User> queryUser(Map params) {
		// 如果可能，在此做业务逻辑判断，业务逻辑处理。
		return userDAO.queryUser(params);
	}

}
