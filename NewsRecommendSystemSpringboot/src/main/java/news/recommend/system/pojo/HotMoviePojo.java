package news.recommend.system.pojo;

public class HotMoviePojo {
private int movieId;
private String count;
private String yearmonth;
public HotMoviePojo(int movieId, String count, String yearmonth) {
	super();
	this.movieId = movieId;
	this.count = count;
	this.yearmonth = yearmonth;
}
public HotMoviePojo() {
	super();
}
@Override
public String toString() {
	return "HotMoviePojo [movieId=" + movieId + ", count=" + count
			+ ", yearmonth=" + yearmonth + "]";
}
public int getMovieId() {
	return movieId;
}
public void setMovieId(int movieId) {
	this.movieId = movieId;
}
public String getCount() {
	return count;
}
public void setCount(String count) {
	this.count = count;
}
public String getYearmonth() {
	return yearmonth;
}
public void setYearmonth(String yearmonth) {
	this.yearmonth = yearmonth;
}

}
