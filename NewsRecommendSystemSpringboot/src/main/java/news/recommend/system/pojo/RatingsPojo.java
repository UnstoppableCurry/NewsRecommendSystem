package news.recommend.system.pojo;

public class RatingsPojo {
private String userId;
private String movieId;
private String rating;
private String timestamp;
public RatingsPojo(String userId, String movieId, String rating,
		String timestamp) {
	super();
	this.userId = userId;
	this.movieId = movieId;
	this.rating = rating;
	this.timestamp = timestamp;
}
public RatingsPojo() {
	super();
}
@Override
public String toString() {
	return "RatingsPojo [userId=" + userId + ", movieId=" + movieId
			+ ", rating=" + rating + ", timestamp=" + timestamp + "]";
}
public String getUserId() {
	return userId;
}
public void setUserId(String userId) {
	this.userId = userId;
}
public String getMovieId() {
	return movieId;
}
public void setMovieId(String movieId) {
	this.movieId = movieId;
}
public String getRating() {
	return rating;
}
public void setRating(String rating) {
	this.rating = rating;
}
public String getTimestamp() {
	return timestamp;
}
public void setTimestamp(String timestamp) {
	this.timestamp = timestamp;
}

}
