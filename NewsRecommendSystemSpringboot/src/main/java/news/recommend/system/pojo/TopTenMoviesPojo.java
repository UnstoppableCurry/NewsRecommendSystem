package news.recommend.system.pojo;

public class TopTenMoviesPojo {
private String genners;
private String topTenMovies;
@Override
public String toString() {
	return "TopTenMoviesPojo [genners=" + genners + ", topTenGennersCollection="
			+ topTenMovies + "]";
}
public String getGenners() {
	return genners;
}
public void setGenners(String genners) {
	this.genners = genners;
}
public String getTopTenGennersCollection() {
	return topTenMovies;
}
public void setTopTenGennersCollection(String topTenGennersCollection) {
	this.topTenMovies = topTenGennersCollection;
}
public TopTenMoviesPojo(String genners, String topTenGennersCollection) {
	super();
	this.genners = genners;
	this.topTenMovies = topTenGennersCollection;
}
public TopTenMoviesPojo() {
	super();
}

}
