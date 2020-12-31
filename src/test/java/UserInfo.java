import com.bobo.reves.mysql.annotation.MySQLColumnName;
import com.bobo.reves.mysql.annotation.MySQLId;
import com.bobo.reves.mysql.annotation.MySQLTableName;

import java.time.LocalDateTime;

/**
 * userInfo
 *
 * @author BO
 * @date 2020-12-26 14:30
 * @since 2020/12/26
 **/
@MySQLTableName("user_info")
public class UserInfo {
	@MySQLId
	private Long id;
	@MySQLColumnName("user_name")
	private String userName;
	private Integer userAge;
	private LocalDateTime createTime;

	public Long getId() {
		return id;
	}

	public UserInfo setId(Long id) {
		this.id = id;
		return this;
	}

	public String getUserName() {
		return userName;
	}

	public UserInfo setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public Integer getUserAge() {
		return userAge;
	}

	public UserInfo setUserAge(Integer userAge) {
		this.userAge = userAge;
		return this;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public UserInfo setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
		return this;
	}
}
