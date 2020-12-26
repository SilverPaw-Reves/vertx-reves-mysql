package com.bobo.reves.mysql;

import com.bobo.reves.mysql.annotation.MySQLColumnName;
import com.bobo.reves.mysql.annotation.MySQLExclude;
import com.bobo.reves.mysql.annotation.MySQLId;
import com.bobo.reves.mysql.annotation.MySQLTableName;
import com.bobo.reves.mysql.utils.MyCompositeFuture;
import com.bobo.reves.mysql.utils.StringUtils;
import com.google.common.base.CaseFormat;
import com.google.common.reflect.TypeToken;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.PoolOptions;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 批量 插入
     *
     * @param tList 批量插入对象
     * @return 返回执行结果,
     */
    public Future<BatchExecutionResults<T>> insert(List<T> tList) {
        Promise<BatchExecutionResults<T>> promise = Promise.promise();
        BatchExecutionResults<T> tBatchExecutionResults = new BatchExecutionResults<>();
        ArrayList<Future<T>> futures = new ArrayList<>();
        tList.forEach(t -> futures.add(this.insert(t).onFailure(f -> {
            tBatchExecutionResults.getFailedList().add(t);
            tBatchExecutionResults.getException().add(f);
        }).onSuccess(s -> tBatchExecutionResults.getSuccessList().add(s))));
        MyCompositeFuture.join(futures).onComplete(s -> promise.complete(tBatchExecutionResults));
        return promise.future();
    }

    /**
     * 根据主键删除
     *
     * @param t
     * @return
     */
    public Future<Integer> delete(T t) {
        Class<?> aClass = t.getClass();
        Future<Integer> future = null;
        for (Field declaredField : aClass.getDeclaredFields()) {
            MySQLId mySQLID = declaredField.getAnnotation(MySQLId.class);
            if (mySQLID != null) {
                try {
                    future = deleteById(declaredField.get(t));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    future = Future.failedFuture(e);
                }
            }
        }
        if (future == null) {
            return Future.failedFuture("主键不存在");
        } else {
            return future;
        }
    }

    /**
     * 删除函数
     */
    public Future<Integer> deleteById(Object object) {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) genericSuperclass;
        Type actualType = pt.getActualTypeArguments()[0];
        Class<?> clazz = TypeToken.of(actualType).getRawType();
        MySQLTableName mySQLTableName = clazz.getAnnotation(MySQLTableName.class);
        if (mySQLTableName == null || StringUtils.isEmpty(mySQLTableName.value())) {
            return Future.failedFuture("Unknown table name , use MySQLTableName annotation");
        }
        String primary = "";
        for (Field field : clazz.getDeclaredFields()) {
            MySQLId mySQLID = field.getAnnotation(MySQLId.class);
            if (mySQLID != null) {
                MySQLColumnName mySQLColumnName = field.getAnnotation(MySQLColumnName.class);
                if (mySQLColumnName != null && !StringUtils.isEmpty(mySQLColumnName.value())) {
                    primary = mySQLColumnName.value();
                } else {
                    primary = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
                }
            }
        }
        if (StringUtils.isEmpty(primary)) {
            return Future.failedFuture("PRIMARY Undefined , Use MySQLID annotation ");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("delete from ").append(mySQLTableName.value())
                .append(" where ").append(primary).append(" = ?");
        return super.executeNoResult(object, stringBuilder.toString());
    }

    public Future<Integer> update(T t) {
        

        return null;

    }


}
