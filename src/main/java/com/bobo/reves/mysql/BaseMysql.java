package com.bobo.reves.mysql;

import com.bobo.reves.mysql.annotation.MySQLColumnName;
import com.bobo.reves.mysql.annotation.MySQLExclude;
import com.bobo.reves.mysql.annotation.MySQLId;
import com.bobo.reves.mysql.annotation.MySQLTableName;
import com.bobo.reves.mysql.utils.StringUtils;
import com.google.common.base.CaseFormat;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.PoolOptions;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * mysql 基础类
 *
 * @author BO
 * @date 2020-12-26 14:32
 * @since 2020/12/26
 **/
public abstract class BaseMysql<T> extends MysqlWrapper {

    public BaseMysql(Vertx vertx, MySQLConnectOptions connectOptions, PoolOptions poolOptions) {
        super(vertx, connectOptions, poolOptions);
    }

    /**
     * 插入数据
     *
     * @param t 插入对象
     * @return 返回对象, 如果有主键, 填充主键
     */
    public Future<T> insert(T t) {
        Promise<T> promise = Promise.promise();
        Class<?> aClass = t.getClass();
        MySQLTableName mySqlTableName = aClass.getAnnotation(MySQLTableName.class);
        if (mySqlTableName == null || StringUtils.isEmpty(mySqlTableName.value())) {
            return Future.failedFuture("Unknown table name , use MySQLTableName annotation");
        }
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder columnListBuilder = new StringBuilder();
        sqlBuilder.append("insert into ").append(mySqlTableName.value());
        int columnNum = 0;
        ArrayList<Object> params = new ArrayList<>();
        Field mysqlIdField = null;
        for (Field field : aClass.getDeclaredFields()) {
            if (field.getAnnotation(MySQLId.class) != null) {
                mysqlIdField = field;
            }
            if (field.getAnnotation(MySQLExclude.class) != null) {
                continue;
            }
            Object o = null;
            field.setAccessible(true);
            try {
                o = field.get(t);
                if (o == null || "".equals(o)) {
                    continue;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            String columnName = "";
            MySQLColumnName mySqlColumnName = field.getAnnotation(MySQLColumnName.class);
            if (mySqlColumnName != null) {
                columnName = mySqlColumnName.value();
            }
            if (StringUtils.isEmpty(columnName)) {
                //驼峰转_
                columnName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
            }
            columnListBuilder.append(columnName).append(",");
            columnNum++;
            if (o instanceof LocalDate || o instanceof LocalTime || o instanceof LocalDateTime) {
                params.add(o.toString());
            } else {
                params.add(o);
            }
        }
        if (columnNum == 0) {
            //没有字段捣啥乱
            return Future.succeededFuture();
        }
        sqlBuilder.append("(").append(columnListBuilder).deleteCharAt(sqlBuilder.length() - 1)
                .append(") values(").append(StringUtils.repeat("?,", columnNum)).deleteCharAt(sqlBuilder.length() - 1)
                .append(")");
        Field finalMysqlIdField = mysqlIdField;
        super.insertLastId(params, sqlBuilder.toString()).onFailure(promise::fail)
                .onSuccess(s -> {
                    if (null != finalMysqlIdField) {
                        finalMysqlIdField.setAccessible(true);
                        try {
                            finalMysqlIdField.set(t, s);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            promise.fail(e);
                        }
                    }
                    promise.complete(t);
                });
        return promise.future();
    }
}
