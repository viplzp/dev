package io.flysium.framework.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页对象
 * 
 * @param <T>
 */
public class PageModel<T> implements Serializable {

	private static final long serialVersionUID = 401709279809798382L;

	private int pageNumber = 1;// 第几页
	private int pageSize = 10;// 每页多少数据
	private int pageCount = 1;// 共几页
	private int total = 0;// 共多少数据

	private List<Serializable> rows;// 分页数据

	/**
	 * 构造器
	 */
	public PageModel() {
		super();
	}

	/**
	 * 构造器
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param pageCount
	 * @param total
	 * @param rows
	 */
	public PageModel(int pageNumber, int pageSize, int pageCount, int total, List rows) {
		super();
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.pageCount = pageCount;
		this.total = total;
		this.rows = rows;
	}

	/** 获取第几页 */
	public int getPageNumber() {
		return pageNumber;
	}

	/** 设置第几页 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	/** 获取每页多少数据 */
	public int getPageSize() {
		return pageSize;
	}

	/** 设置每页多少数据 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/** 获取总页数 */
	public int getPageCount() {
		return pageCount;
	}

	/** 设置总页数 */
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	/** 获取总记录数 */
	public int getTotal() {
		return total;
	}

	/** 设置总记录数 */
	public void setTotal(int total) {
		this.total = total;
	}

	/** 获取分页数据 */
	public List getRows() {
		if (rows == null) {
			rows = new ArrayList();
		}
		return rows;
	}

	/** 设置分页数据 */
	public void setRows(List rows) {
		this.rows = rows;
	}

	/** 加入分页数据 */
	public void addRow(T element) {
		if (element != null) {
			getRows().add(element);
		}
	}

}