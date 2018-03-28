package model;
/**

 */
public class MIdRated {
	private Integer movieId;
	private float rated;
	
	public MIdRated(){}
	public MIdRated(int id,float rated){
		this.setMovieId(id);
		this.setRated(rated);
	}
	
	public MIdRated(String id,String rated){
		this.setMovieId(Integer.parseInt(id));
		this.setRated(Float.parseFloat(rated));
	}
	public Integer getMovieId() {
		return movieId;
	}
	public void setMovieId(Integer movieId) {
		this.movieId = movieId;
	}
	public float getRated() {
		return rated;
	}
	public void setRated(float rated) {
		this.rated = rated;
	}
}
