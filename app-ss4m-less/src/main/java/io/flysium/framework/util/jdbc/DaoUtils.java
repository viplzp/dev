package io.flysium.framework.util.jdbc;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.transaction.TransactionException;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.github.miemiedev.mybatis.paginator.domain.Paginator;

import io.flysium.framework.Consts;
import io.flysium.framework.jdbc.datasource.support.DynamicDataSourceContextHolder;
import io.flysium.framework.util.SpringContextUtils;
import io.flysium.framework.vo.PageModel;

/**
 * DAO工具类
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public final class DaoUtils {

	private static Logger logger = LoggerFactory.getLogger(DaoUtils.class);

	private DaoUtils() {
	}

	/**
	 * 获取当前mybatis数据库操作模板
	 * 
	 * @return
	 */
	public static SqlSessionTemplate getSqlTpl() {
		return getSqlTpl(DynamicDataSourceContextHolder.getDataSource());
	}

	/**
	 * 获取mybatis数据库操作模板
	 * 
	 * @param jndiName
	 * @return
	 */
	public static SqlSessionTemplate getSqlTpl(String jndiName) {
		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			throw new TransactionException("没有通过Spring的事务管控（@Transactional），不允许获取连接!");
		}

		if (Consts.JNDI.DEFAULT_JNDI_NAME.equals(jndiName)) {
			return SpringContextUtils.getBean("sqlSessionTemplate");
		} else if (Consts.JNDI.LOG_JNDI_NAME.equals(jndiName)) {
			return SpringContextUtils.getBean("logSqlSessionTemplate");
		}
		return SpringContextUtils.getBean("sqlSessionTemplate");
	}

	/**
	 * 分页查询(查询总数)
	 * 
	 * @param statement
	 * @param parameter
	 * @return
	 */
	public static <T> PageModel<T> selectPageModel(String statement, Map parameter) {
		return selectPageModel(statement, parameter, true);
	}

	/**
	 * 分页查询(查询总数)
	 * 
	 * @param statement
	 * @param parameter
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public static <T> PageModel<T> selectPageModel(String statement, Object parameter, int pageNumber, int pageSize) {
		return selectPageModel(statement, parameter, pageNumber, pageSize, true);
	}

	/**
	 * 分页查询(不查询总数)
	 * 
	 * @param statement
	 * @param parameter
	 * @return
	 */
	public static <T> PageModel<T> selectPageModelNoCount(String statement, Map parameter) {
		return selectPageModel(statement, parameter, false);
	}

	/**
	 * 分页查询(不查询总数)
	 * 
	 * @param statement
	 * @param parameter
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public static <T> PageModel<T> selectPageModelNoCount(String statement, Object parameter, int pageNumber,
			int pageSize) {
		return selectPageModel(statement, parameter, pageNumber, pageSize, false);
	}

	private static <T> PageModel<T> selectPageModel(String statement, Map parameter, boolean containsTotalCount) {
		int pageNumber = 1;
		try {
			pageNumber = Integer.parseInt(String.valueOf(parameter.get(Consts.FrontEndParamSet.PARAM_PAGE_NUMBER)));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		int pageSize = 10;
		try {
			pageSize = Integer.parseInt(String.valueOf(parameter.get(Consts.FrontEndParamSet.PARAM_PAGE_SIZE)));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return selectPageModel(DynamicDataSourceContextHolder.getDataSource(), statement, parameter, pageNumber,
				pageSize, containsTotalCount);
	}

	private static <T> PageModel<T> selectPageModel(String statement, Object parameter, int pageNumber, int pageSize,
			boolean containsTotalCount) {
		return selectPageModel(DynamicDataSourceContextHolder.getDataSource(), statement, parameter, pageNumber,
				pageSize, containsTotalCount);
	}

	/**
	 * 分页查询
	 * 
	 * @param jndiName
	 * @param statement
	 * @param parameter
	 * @param pageNumber
	 * @param pageSize
	 * @param containsTotalCount
	 * @return
	 */
	private static <T> PageModel<T> selectPageModel(String jndiName, String statement, Object parameter, int pageNumber,
			int pageSize, boolean containsTotalCount) {
		PageBounds pageBounds = new PageBounds();
		pageBounds.setPage(pageNumber);
		pageBounds.setLimit(pageSize);
		pageBounds.setContainsTotalCount(containsTotalCount);

		PageModel pageModel = new PageModel();
		List<T> list = getSqlTpl(jndiName).selectList(statement, parameter, pageBounds);
		if (list instanceof PageList) {
			PageList<T> pageList = (PageList<T>) list;
			for (T element : pageList) {
				pageModel.addRow(element);
			}

			Paginator paginator = pageList.getPaginator();
			if (paginator != null) {
				pageModel.setPageNumber(paginator.getPage());
				pageModel.setPageSize(paginator.getLimit());
				pageModel.setPageCount(paginator.getTotalPages());
				pageModel.setTotal(paginator.getTotalCount());
			}
		} else {
			pageModel.setRows(list);
		}
		return pageModel;
	}

}