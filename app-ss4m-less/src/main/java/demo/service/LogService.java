package demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import demo.common.dao.ILogDAO;
import demo.common.service.ILogService;
import demo.common.vo.SysOperLog;
import io.flysium.framework.transaction.annotation.LogTransactional;
import io.flysium.framework.vo.PageModel;

/**
 * 系统操作日志服务
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月6日
 */
@Service
public class LogService implements ILogService {

	@Autowired
	private ILogDAO logDAO;

	@Override
	@LogTransactional
	public PageModel<SysOperLog> queryLogByUserId(String userId) {
		// 如果可能，在此做业务逻辑判断，业务逻辑处理。
		return logDAO.queryLogByUserId(userId);
	}
}
