import com.bobo.reves.mysql.BaseMysql;
import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.PoolOptions;

/**
 * @author BO
 * @date 2020-12-26 14:51
 * @since 2020/12/26
 **/
public class UserWrapper extends BaseMysql<UserInfo> {
    public UserWrapper(Vertx vertx, MySQLConnectOptions connectOptions, PoolOptions poolOptions) {
        super(vertx, connectOptions, poolOptions);
    }
}
