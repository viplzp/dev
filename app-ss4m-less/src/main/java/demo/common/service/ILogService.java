package demo.common.service;

import demo.common.vo.SysOperLog;
import io.flysium.framework.vo.PageModel;

/**
 * 系统操作日志服务
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月6日
 */
public interface ILogService {

	/**
	 * 根据用户ID查询日志信息列表
	 * 
	 * @param userId
	 * @return
	 */
	public PageModel<SysOperLog> queryLogByUserId(String userId);

}