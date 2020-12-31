import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.PoolOptions;

import java.time.LocalDateTime;
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
			.setUser("root").setCharset("utf8mb4")
			.setPassword("123456");
		UserWrapper userWrapper = new UserWrapper(Vertx.vertx(), mySQLConnectOptions, new PoolOptions());
		userWrapper.insert(new UserInfo().setUserName("ins").setUserAge(22).setCreateTime(LocalDateTime.now()))
			.onSuccess(s -> System.out.println(s.toString()));
		ArrayList<UserInfo> objects = new ArrayList<>();
		objects.add(new UserInfo().setUserName("ins").setUserAge(22).setCreateTime(LocalDateTime.now()));
		objects.add(new UserInfo().setUserName("ins2").setUserAge(22).setCreateTime(LocalDateTime.now()));
		objects.add(new UserInfo().setUserName("ins3ins3ins3ins3ins3").setUserAge(22).setCreateTime(LocalDateTime.now()));
		objects.add(new UserInfo().setUserName("ins4").setUserAge(22).setCreateTime(LocalDateTime.now()));
		userWrapper.insert(objects).onSuccess(s -> System.out.println(s.toString()));
		userWrapper.getById(1).onSuccess(s -> System.out.println(s.toString())).onFailure(Throwable::printStackTrace);
	}
}
