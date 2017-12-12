package demo.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import demo.common.dao.ILogDAO;
import demo.common.vo.SysOperLog;
import io.flysium.framework.util.jdbc.DaoUtils;
import io.flysium.framework.vo.PageModel;

/**
 * 日志DAO
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月8日
 */
@Repository // DAO层注解
public class LogDAO implements ILogDAO {

	/**
	 * 查询日志信息
	 */
	@Override
	public PageModel<SysOperLog> queryLogByUserId(String userId) {
		Map params = new HashMap();
		params.put("USER_ID", userId);
		PageModel<SysOperLog> pageModel = DaoUtils.selectPageModel("SysOperLog.queryLogByUserId", params);
		return pageModel;
	}

}
