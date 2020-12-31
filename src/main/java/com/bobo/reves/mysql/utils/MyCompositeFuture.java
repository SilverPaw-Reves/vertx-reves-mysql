package com.bobo.reves.mysql.utils;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.impl.future.CompositeFutureImpl;

import java.util.List;

/**
 * 解决 CompositeFuture 无法使用泛型的Future ，会产生 Raw use of parameterized class 'Future' 警告⚠️️
 *
 * @author bobo
 */
public interface MyCompositeFuture extends CompositeFuture {
	/**
	 * Like {@link #all(Future, Future)} but with a list of futures.
	 *
	 * <p>When the list is empty, the returned future will be already completed.
	 *
	 * @param futures future list
	 * @param <T>     泛型
	 * @return 无
	 */
	static <T> CompositeFuture all(List<Future<T>> futures) {
		return CompositeFutureImpl.all(futures.toArray(new Future[0]));
	}

	/**
	 * Like {@link #join(Future, Future)} but with a list of futures.
	 *
	 * <p>When the list is empty, the returned future will be already completed.
	 *
	 * @param futures future list
	 * @param <T>     泛型
	 * @return 无
	 */
	static <T> CompositeFuture join(List<Future<T>> futures) {
		return CompositeFutureImpl.join(futures.toArray(new Future[0]));
	}

	/**
	 * * Like {@link #any(Future, Future)} but with a list of futures.
	 *
	 * <p>* * When the list is empty, the returned future will be already completed.
	 *
	 * @param futures future list
	 * @param <T>     泛型
	 * @return 无
	 */
	static <T> CompositeFuture any(List<Future<T>> futures) {
		return CompositeFutureImpl.any(futures.toArray(new Future[0]));
	}
}
