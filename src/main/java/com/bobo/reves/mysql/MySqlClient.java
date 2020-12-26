package com.bobo.reves.mysql;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

/**
 * 数据库连接池
 *
 * @author BO
 * @date 2020-12-25 17:47
 * @since 2020/12/25
 **/
public class MySqlClient {
    public static MySQLPool getClient(Vertx vertx, JsonObject mySQLConnectOptionsJson, JsonObject poolOptionsJson) {
        return getClient(vertx, new MySQLConnectOptions(mySQLConnectOptionsJson), new PoolOptions(poolOptionsJson));
    }

    public static MySQLPool getClient(Vertx vertx, MySQLConnectOptions connectOptions, PoolOptions poolOptions) {
        return MySQLPool.pool(vertx, connectOptions, poolOptions);
    }
}
