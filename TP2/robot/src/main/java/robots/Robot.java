package robots;

public class Robot {
	private int minerai;
	private int position;
	private StringBuilder monde;
	private static final char VIDE = '.';

	public Robot(int minerai, String monde) {
		this.minerai = minerai;
		this.position = 1;
		this.monde = new StringBuilder(monde);
	}

	public int getMinerai() {
		return minerai;
	}
	
	public int getPosition() {
		return position;
	}
	
	
	public String avancer() {
		if (minerai == 0) 
			return "épuisé";
		++position;
		--minerai;
		return "avance";
	}

	public String reculer() {
		if (minerai == 0) 
			return "épuisé";
		--position;
		--minerai;
		return "recule";
	}
	
	public char regarder() {
		return monde.charAt(position);
	}
	
	public int getMinerai(char c) {
		if (c <'0' || c > '9')
			return 0; // pas de minerai
		else return c -'0';
	}
	
	public String récolter() {
		int récolte = getMinerai(regarder());
		minerai += récolte;
		monde.setCharAt(position, VIDE);
		return "récolte : " + récolte;
	}
	public boolean ilFautRentrer() {
		return position >0 && (position +1) > minerai;
	}
	
	public String pas() {
		if (ilFautRentrer())
			return reculer();
		return avancer() + "\n"+ récolter();
	}
	
	String getStatut() {
		return " ["+minerai+"] ("+position+")";
	}
	
	public String mission() {
		String journalDeBord = "";
		while (position >0  && minerai >0)
			journalDeBord += pas() +getStatut() +"\n";
		return journalDeBord;
	}
}
