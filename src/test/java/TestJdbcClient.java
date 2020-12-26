import com.bobo.reves.mysql.MysqlWrapper;
import com.bobo.reves.mysql.Page;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.PoolOptions;

import java.util.ArrayList;

/**
 * 测试工具类
 *
 * @author BO
 * @date 2020-12-25 18:40
 * @since 2020/12/25
 **/
public class TestJdbcClient {
    public static void main(String[] args) {
        MySQLConnectOptions mySQLConnectOptions = new MySQLConnectOptions();
        mySQLConnectOptions
                .setHost("127.0.0.1")
                .setDatabase("test")
                .setUser("root")
                .setPassword("123456");


        MysqlWrapper mysqlWrapper = new MysqlWrapper(Vertx.vertx(), mySQLConnectOptions, new PoolOptions());
        ArrayList<Object> objects = new ArrayList<>();
        objects.add("王五2");
        mysqlWrapper.pagination(objects, "select * from user_info where user_name = ?", new Page(1, 2))
                .onFailure(Throwable::printStackTrace)
                .onSuccess(s -> System.out.println(s.toString()));
    }
}
