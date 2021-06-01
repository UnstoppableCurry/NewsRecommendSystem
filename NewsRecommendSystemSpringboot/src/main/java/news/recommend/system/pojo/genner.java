package news.recommend.system.pojo;

public class genner {
	private  String Comedy;private  String Western;private 
	String Children;private  String Action;private  String Sci_Fi;private  String Thriller;private 
	String Fantasy;private  String Romance;private  String Drama;private  String Documentary;private 
	String War;private  String Film_Noir;private  String Animation;private  String no;private 
	String Horror;private  String IMAX;private  String Mystery;private  String Musical;

	public genner(String comedy, String western, String children,
			String action, String sci_Fi, String thriller, String fantasy,
			String romance, String drama, String documentary, String war,
			String film_Noir, String animation, String no, String horror,
			String iMAX, String mystery, String musical) {
		super();
		Comedy = comedy;
		Western = western;
		Children = children;
		Action = action;
		Sci_Fi = sci_Fi;
		Thriller = thriller;
		Fantasy = fantasy;
		Romance = romance;
		Drama = drama;
		Documentary = documentary;
		War = war;
		Film_Noir = film_Noir;
		Animation = animation;
		this.no = no;
		Horror = horror;
		IMAX = iMAX;
		Mystery = mystery;
		Musical = musical;
	}

	public genner() {
		super();
	}

	@Override
	public String toString() {
		return "genner [Comedy=" + Comedy + ", Western=" + Western
				+ ", Children=" + Children + ", Action=" + Action + ", Sci_Fi="
				+ Sci_Fi + ", Thriller=" + Thriller + ", Fantasy=" + Fantasy
				+ ", Romance=" + Romance + ", Drama=" + Drama
				+ ", Documentary=" + Documentary + ", War=" + War
				+ ", Film_Noir=" + Film_Noir + ", Animation=" + Animation
				+ ", no=" + no + ", Horror=" + Horror + ", IMAX=" + IMAX
				+ ", Mystery=" + Mystery + ", Musical=" + Musical + "]";
	}

	public String getComedy() {
		return Comedy;
	}

	public void setComedy(String comedy) {
		Comedy = comedy;
	}

	public String getWestern() {
		return Western;
	}

	public void setWestern(String western) {
		Western = western;
	}

	public String getChildren() {
		return Children;
	}

	public void setChildren(String children) {
		Children = children;
	}

	public String getAction() {
		return Action;
	}

	public void setAction(String action) {
		Action = action;
	}

	public String getSci_Fi() {
		return Sci_Fi;
	}

	public void setSci_Fi(String sci_Fi) {
		Sci_Fi = sci_Fi;
	}

	public String getThriller() {
		return Thriller;
	}

	public void setThriller(String thriller) {
		Thriller = thriller;
	}

	public String getFantasy() {
		return Fantasy;
	}

	public void setFantasy(String fantasy) {
		Fantasy = fantasy;
	}

	public String getRomance() {
		return Romance;
	}

	public void setRomance(String romance) {
		Romance = romance;
	}

	public String getDrama() {
		return Drama;
	}

	public void setDrama(String drama) {
		Drama = drama;
	}

	public String getDocumentary() {
		return Documentary;
	}

	public void setDocumentary(String documentary) {
		Documentary = documentary;
	}

	public String getWar() {
		return War;
	}

	public void setWar(String war) {
		War = war;
	}

	public String getFilm_Noir() {
		return Film_Noir;
	}

	public void setFilm_Noir(String film_Noir) {
		Film_Noir = film_Noir;
	}

	public String getAnimation() {
		return Animation;
	}

	public void setAnimation(String animation) {
		Animation = animation;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getHorror() {
		return Horror;
	}

	public void setHorror(String horror) {
		Horror = horror;
	}

	public String getIMAX() {
		return IMAX;
	}

	public void setIMAX(String iMAX) {
		IMAX = iMAX;
	}

	public String getMystery() {
		return Mystery;
	}

	public void setMystery(String mystery) {
		Mystery = mystery;
	}

	public String getMusical() {
		return Musical;
	}

	public void setMusical(String musical) {
		Musical = musical;
	} 

}
