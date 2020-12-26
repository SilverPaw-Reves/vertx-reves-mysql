package com.bobo.reves.mysql;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * mysql client 查询封装
 *
 * @author BO
 * @date 2020-12-25 17:57
 * @since 2020/12/25
 **/
public class MysqlWrapper {
    private final MySQLConnectOptions connectOptions;
    private final PoolOptions poolOptions;
    private final Vertx vertx;

    public MysqlWrapper(Vertx vertx, JsonObject connectOptionsJson, JsonObject poolOptionsJson) {
        this.vertx = vertx;
        this.connectOptions = new MySQLConnectOptions(connectOptionsJson);
        this.poolOptions = new PoolOptions(poolOptionsJson);
    }

    public MysqlWrapper(Vertx vertx, MySQLConnectOptions connectOptions, PoolOptions poolOptions) {
        this.connectOptions = connectOptions;
        this.poolOptions = poolOptions;
        this.vertx = vertx;
    }

    private MySQLPool getClient(Vertx vertx, MySQLConnectOptions connectOptions, PoolOptions poolOptions) {
        return MySQLPool.pool(vertx, connectOptions, poolOptions);
    }

    /**
     * 查询列表
     *
     * @param params 参数列表
     * @param sql    sql
     * @return 返回所有的查询结果
     */
    public Future<List<JsonObject>> retrieve(List<Object> params, String sql) {
        Promise<List<JsonObject>> promise = Promise.promise();
        getClient(vertx, connectOptions, poolOptions)
                .preparedQuery(sql).execute(Tuple.tuple(params))
                .onFailure(promise::fail)
                .onSuccess(s -> {
                    List<JsonObject> rs = new ArrayList<>();
                    s.forEach(row -> rs.add(row.toJson()));
                    promise.complete(rs);
                });
        return promise.future();
    }

    /**
     * 查询单个对象
     * 只有结果为1时会返回,其他抛出对应异常
     *
     * @param params 参数列表
     * @param sql    sql语句
     * @return 返回查询的对象
     */
    public Future<JsonObject> retrieveOne(List<Object> params, String sql) {
        Promise<JsonObject> promise = Promise.promise();
        retrieve(params, sql).onFailure(promise::fail)
                .onSuccess(s -> {
                    if (s.size() < 1) {
                        promise.fail("not find");
                    } else if (s.size() != 1) {
                        promise.fail("select one , but find more!\nsql:" + sql + ",params:" + params + ",rowCount:" + s.size());
                    } else {
                        promise.complete(s.get(0));
                    }
                });
        return promise.future();
    }

    /**
     * 查询单个对象
     * 只有结果为1时会返回,其他抛出对应异常
     *
     * @param param 单个参数
     * @param sql   sql语句
     * @return 返回查询的对象
     */
    public Future<JsonObject> retrieveOne(Object param, String sql) {
        Promise<JsonObject> promise = Promise.promise();
        retrieve(Collections.singletonList(param), sql).onFailure(promise::fail)
                .onSuccess(s -> {
                    if (s.size() < 1) {
                        promise.fail("not find");
                    } else if (s.size() != 1) {
                        promise.fail("select one , but find more!\nsql:" + sql + ",param:" + param + ",rowCount:" + s.size());
                    } else {
                        promise.complete(s.get(0));
                    }
                });
        return promise.future();
    }

    /**
     * 通过分页查询数据列表
     *
     * @param params 参数列表
     * @param sql    sql
     * @param page   分页参数
     * @return 只返回数据列表
     */
    public Future<List<JsonObject>> retrieveByPage(List<Object> params, String sql, Page page) {
        sql += " limit ?,?";
        List<Object> objects = paramsAddLimit(params, page);
        return retrieve(objects, sql);
    }

    /**
     * 分页查询,返回分页结果对象
     *
     * @param params 参数列表
     * @param sql    sql
     * @param page   分页参数
     * @return Pagination 分页查询结果
     */
    public Future<Pagination<JsonObject>> pagination(List<Object> params, String sql, Page page) {
        Promise<Pagination<JsonObject>> promise = Promise.promise();
        String countSql = String.format("select count(1) as count from (%s) t", sql);
        String limitSql = sql + " limit ?,?";
        List<Object> objects = paramsAddLimit(params, page);
        retrieveOne(params, countSql).onFailure(promise::fail).onSuccess(countRes ->
                retrieve(objects, limitSql).onFailure(promise::fail).onSuccess(res -> {
                    Pagination<JsonObject> objectPagination = new Pagination<>();
                    objectPagination.setPage(page).setData(res).setCount(countRes.getInteger("count"));
                    promise.complete(objectPagination);
                }));
        return promise.future();
    }

    /**
     * 执行sql 不返回信息
     * 一般用于update 和 批量删除
     *
     * @param params 参数列表
     * @param sql    sql语句
     * @return 不返回数据, 返回受影响的行数
     */
    public Future<Integer> executeNoResult(List<Object> params, String sql) {
        Promise<Integer> promise = Promise.promise();
        getClient(vertx, connectOptions, poolOptions)
                .preparedQuery(sql).execute(Tuple.tuple(params))
                .onFailure(promise::fail)
                .onSuccess(s -> promise.complete(s.rowCount()));
        return promise.future();
    }

    /**
     * 执行sql 不返回信息
     * 一般用于单个update 和 单个删除
     *
     * @param param 参数
     * @param sql   sql语句
     * @return 不返回数据, 返回受影响的行数
     */
    public Future<Integer> executeNoResult(Object param, String sql) {
        Promise<Integer> promise = Promise.promise();
        getClient(vertx, connectOptions, poolOptions)
                .preparedQuery(sql).execute(Tuple.of(param))
                .onFailure(promise::fail)
                .onSuccess(s -> promise.complete(s.rowCount()));
        return promise.future();
    }

    /**
     * 插入数据,返回主键id
     *
     * @param params 参数列表
     * @param sql    sql语句
     * @return 返回主键id
     */
    public Future<Long> insertLastId(List<Object> params, String sql) {
        Promise<Long> promise = Promise.promise();
        getClient(vertx, connectOptions, poolOptions)
                .preparedQuery(sql).execute(Tuple.tuple(params))
                .onFailure(promise::fail)
                .onSuccess(rows -> promise.complete(rows.property(MySQLClient.LAST_INSERTED_ID)));
        return promise.future();
    }

    /**
     * 批量执行
     *
     * @param params 批量参数列表
     * @param sql    sql语句
     * @return 返回条数
     */
    public Future<Integer> executeBatch(List<List<Object>> params, String sql) {
        Promise<Integer> promise = Promise.promise();
        getClient(vertx, connectOptions, poolOptions)
                .preparedQuery(sql).executeBatch(params.stream().map(Tuple::tuple).collect(Collectors.toList()))
                .onFailure(promise::fail)
                .onSuccess(s -> {
                    int i = s.rowCount();
                    RowSet<Row> next = s;
                    while ((next = next.next()) != null) {
                        i += next.rowCount();
                    }
                    promise.complete(i);
                });
        return promise.future();
    }

    /**
     * 拼接分页查询的参数
     *
     * @param params 参数列表
     * @param page   分页参数
     * @return 返回新的参数
     */
    private List<Object> paramsAddLimit(List<Object> params, Page page) {
        List<Object> objects = new ArrayList<>(params);
        objects.add(page.calcPage());
        objects.add(page.getPageSize());
        return objects;
    }

}
