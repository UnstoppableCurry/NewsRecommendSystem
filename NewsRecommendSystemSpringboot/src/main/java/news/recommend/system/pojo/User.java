package news.recommend.system.pojo;

import java.util.Date;

public class User {
	private int id;
	private String username;
	private String password;
	private String phonenumber;
	private String nickname;
	private Date  regdate;
	private String email;
	private String sex;
	private String remark;
	private String role;
	private String genner;
	public String getGenner() {
		return genner;
	}
	public void setGenner(String genner) {
		this.genner = genner;
	}
	public User(int id, String username, String password, String phonenumber,
			String nickname, Date regdate, String email, String sex,
			String remark, String role, String genner) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.phonenumber = phonenumber;
		this.nickname = nickname;
		this.regdate = regdate;
		this.email = email;
		this.sex = sex;
		this.remark = remark;
		this.role = role;
		this.genner = genner;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password="
				+ password + ", phonenumber=" + phonenumber + ", nickname="
				+ nickname + ", regdate=" + regdate + ", email=" + email
				+ ", sex=" + sex + ", remark=" + remark + ", role=" + role
				+ ", genner=" + genner + "]";
	}
	public User(int id, String username, String password, String phonenumber,
			String nickname, Date regdate, String email, String sex,
			String remark, String role) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.phonenumber = phonenumber;
		this.nickname = nickname;
		this.regdate = regdate;
		this.email = email;
		this.sex = sex;
		this.remark = remark;
		this.role = role;
	}
	public User(String username, String password, String nickname, String email) {
		super();
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.email = email;
	}
	public User(String password, String nickname, String email) {
		super();
		this.password = password;
		this.nickname = nickname;
		this.email = email;
	}
	public User(String username, String password, String phonenumber,
			String nickname, Date regdate, String email, String sex,
			String remark, String role) {
		super();
		this.username = username;
		this.password = password;
		this.phonenumber = phonenumber;
		this.nickname = nickname;
		this.regdate = regdate;
		this.email = email;
		this.sex = sex;
		this.remark = remark;
		this.role = role;
	}
	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	public User() {
		super();
	}
	public User(String username) {
		super();
		this.username = username;
	}
	public User(int id) {
		super();
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Date getRegdate() {
		return regdate;
	}
	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	

}
