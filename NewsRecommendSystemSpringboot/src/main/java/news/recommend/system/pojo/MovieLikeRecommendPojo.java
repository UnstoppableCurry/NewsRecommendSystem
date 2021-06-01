package news.recommend.system.pojo;

public class MovieLikeRecommendPojo {
private int movieId;
private String recommend_collections;
@Override
public String toString() {
	return "MovieLikeRecommendPojo [movieId=" + movieId
			+ ", recommend_collections=" + recommend_collections + "]";
}
public MovieLikeRecommendPojo(int movieId, String recommend_collections) {
	super();
	this.movieId = movieId;
	this.recommend_collections = recommend_collections;
}
public int getMovieId() {
	return movieId;
}
public void setMovieId(int movieId) {
	this.movieId = movieId;
}
public String getRecommend_collections() {
	return recommend_collections;
}
public void setRecommend_collections(String recommend_collections) {
	this.recommend_collections = recommend_collections;
}
public MovieLikeRecommendPojo() {
	super();
}

}
