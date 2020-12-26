package com.bobo.reves.mysql;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;

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

    public MysqlWrapper(Vertx vertx, JsonObject mySQLConnectOptionsJson, JsonObject poolOptionsJson) {
        this.vertx = vertx;
        this.connectOptions = new MySQLConnectOptions(mySQLConnectOptionsJson);
        this.poolOptions = new PoolOptions(poolOptionsJson);

    }

    public MysqlWrapper(Vertx vertx, MySQLConnectOptions connectOptions, PoolOptions poolOptions) {
        this.connectOptions = connectOptions;
        this.poolOptions = poolOptions;
        this.vertx = vertx;
    }


    protected Future<RowSet<Row>> executeNoResult(List<Object> params, String sql) {
        Future<RowSet<Row>> execute = MySqlClient.getClient(vertx, connectOptions, poolOptions)
                .preparedQuery(sql).execute(Tuple.tuple(params));
        return execute;
    }

    public Future<List<JsonObject>> query(List<Object> params, String sql) {
        Promise<List<JsonObject>> promise = Promise.promise();
        MySqlClient.getClient(vertx, connectOptions, poolOptions)
                .preparedQuery(sql).execute(Tuple.tuple(params))
                .onFailure(f -> promise.fail(f))
                .onSuccess(s -> {
                    List<JsonObject> rs = new ArrayList<>();
                    s.forEach(row -> rs.add(row.toJson()));
                    promise.complete(rs);
                });
        return promise.future();
    }

    public Future<JsonObject> queryOne(List<Object> params, String sql) {
        Promise<JsonObject> promise = Promise.promise();
        query(params, sql).onFailure(promise::fail)
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

    public <T> Future<T> install(T t, String sql) {
        Promise<T> promise = Promise.promise();


        return promise.future();
    }
}
