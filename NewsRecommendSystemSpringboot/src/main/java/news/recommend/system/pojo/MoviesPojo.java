package news.recommend.system.pojo;

public class MoviesPojo {
private String movieId;
private String title;
private String genres;
@Override
public String toString() {
	return "MoviesPojo [movieId=" + movieId + ", title=" + title + ", genres="
			+ genres + "]";
}
public String getMovieId() {
	return movieId;
}
public void setMovieId(String movieId) {
	this.movieId = movieId;
}
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public String getGenres() {
	return genres;
}
public void setGenres(String genres) {
	this.genres = genres;
}
public MoviesPojo(String movieId, String title, String genres) {
	super();
	this.movieId = movieId;
	this.title = title;
	this.genres = genres;
}
public MoviesPojo() {
	super();
}

}
