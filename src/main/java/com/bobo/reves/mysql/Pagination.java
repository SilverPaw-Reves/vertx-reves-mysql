package com.bobo.reves.mysql;


import java.util.List;

/**
 * 分页查询结果对象
 *
 * @author BO
 * @date 2020-12-26 13:31
 * @since 2020/12/26
 **/
public class Pagination<T> {
	/**
	 * 查询结果
	 */
	private List<T> data;
	/**
	 * 分页对象
	 */
	private Page page;
	/**
	 * 数据总数
	 */
	private Integer count;

	public List<T> getData() {
		return data;
	}

	public Pagination setData(List<T> data) {
		this.data = data;
		return this;
	}

	public Page getPage() {
		return page;
	}

	public Pagination setPage(Page page) {
		this.page = page;
		return this;
	}

	public Integer getCount() {
		return count;
	}

	public Pagination setCount(Integer count) {
		this.count = count;
		return this;
	}
}
