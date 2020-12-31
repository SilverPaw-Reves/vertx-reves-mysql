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

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getUserAge() {
		return userAge;
	}

	public void setUserAge(Integer userAge) {
		this.userAge = userAge;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}
}
