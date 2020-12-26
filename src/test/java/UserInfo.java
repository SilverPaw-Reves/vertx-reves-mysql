import com.bobo.reves.mysql.annotation.MySQLColumnName;
import com.bobo.reves.mysql.annotation.MySQLId;
import com.bobo.reves.mysql.annotation.MySQLTableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * userInfo
 *
 * @author BO
 * @date 2020-12-26 14:30
 * @since 2020/12/26
 **/
@Data
@Accessors(chain = true)
@MySQLTableName("user_info")
public class UserInfo {
    @MySQLId
    private Long id;
    @MySQLColumnName("user_name")
    private String userName;
    private Integer userAge;
    private LocalDateTime createTime;
}
