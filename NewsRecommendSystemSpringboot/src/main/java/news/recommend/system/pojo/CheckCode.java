package news.recommend.system.pojo;

import news.recommend.system.exception.SSMException;

public class CheckCode {
	private StringBuilder codeBuilder;
	private long localTime;
	private String userIP;
	
	public CheckCode(StringBuilder codeBuilder, long localTime, String userIP) {
		super();
		this.codeBuilder = codeBuilder;
		this.localTime = localTime;
		this.userIP = userIP;
	}
	public StringBuilder getCodeBuilder() {
		return codeBuilder;
	}
	public void setCodeBuilder(StringBuilder codeBuilder) {
		this.codeBuilder = codeBuilder;
	}
	public long getLocalTime() {
		return localTime;
	}
	public void setLocalTime(long localTime) {
		this.localTime = localTime;
	}
	public String getUserIP() {
		return userIP;
	}
	public void setUserIP(String userIP) {
		this.userIP = userIP;
	}
	
}
