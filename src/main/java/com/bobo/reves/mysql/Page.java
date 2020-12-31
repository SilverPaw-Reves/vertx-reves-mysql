package com.bobo.reves.mysql;

/**
 * page 分页参数对象
 *
 * @author BO
 * @date 2020-12-26 13:31
 * @since 2020/12/26
 **/
public class Page {
	/**
	 * 页码
	 */
	private Integer page;
	/**
	 * 行数
	 */
	private Integer pageSize;

	public Page() {
	}

	public Page(Integer page, Integer pageSize) {
		this.page = page;
		this.pageSize = pageSize;
	}

	public Integer calcPage() {
		if (page <= 0) {
			return 0;
		}
		return pageSize * (page - 1);
	}

	public Integer getPage() {
		return page;
	}

	public Page setPage(Integer page) {
		this.page = page;
		return this;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public Page setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}
}
