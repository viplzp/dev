package demo.common.dao;

import demo.common.vo.SysOperLog;
import io.flysium.framework.vo.PageModel;

/**
 * 日志DAO
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月8日
 */
public interface ILogDAO {

	/**
	 * 查询日志信息
	 * 
	 * @param userId
	 * @return
	 */
	public PageModel<SysOperLog> queryLogByUserId(String userId);

}
