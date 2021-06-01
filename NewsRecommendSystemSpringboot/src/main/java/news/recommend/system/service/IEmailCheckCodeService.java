package news.recommend.system.service;

public interface IEmailCheckCodeService {


	public boolean checkCodeTime(long localTime, long beforeEmailCodeTime,int seconde);

}
