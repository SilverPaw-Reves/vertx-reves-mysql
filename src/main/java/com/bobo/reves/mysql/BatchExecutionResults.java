package com.bobo.reves.mysql;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量处理的返回
 *
 * @author BO
 * @date 2020-12-26 15:22
 * @since 2020/12/26
 **/

public class BatchExecutionResults<T> {
	/**
	 * 构造函数
	 */
	public BatchExecutionResults() {
		this.successList = new ArrayList<>();
		this.failedList = new ArrayList<>();
		this.exception = new ArrayList<>();
	}

	/**
	 * 插入成功的集合
	 */
	private List<T> successList;
	/**
	 * 插入失败的集合
	 */
	private List<T> failedList;
	/**
	 * 失败原因
	 */
	private List<Throwable> exception;
	

	public List<T> getSuccessList() {
		return successList;
	}

	public BatchExecutionResults setSuccessList(List<T> successList) {
		this.successList = successList;
		return this;
	}

	public List<T> getFailedList() {
		return failedList;
	}

	public BatchExecutionResults setFailedList(List<T> failedList) {
		this.failedList = failedList;
		return this;
	}

	public List<Throwable> getException() {
		return exception;
	}

	public BatchExecutionResults setException(List<Throwable> exception) {
		this.exception = exception;
		return this;
	}
}
