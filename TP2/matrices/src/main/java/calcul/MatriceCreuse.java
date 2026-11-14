package calcul;

import geometrie.XY;
import java.util.HashMap;
import java.util.Map;

public class MatriceCreuse {
	private Map<XY, Integer> elements;
	private int hauteur;
	private int largeur;

	public MatriceCreuse(int hauteur, int largeur) {
		this.hauteur = hauteur;
		this.largeur = largeur;
		elements = new HashMap<XY, Integer>();
	}

	public int getLargeur() {
		return largeur;
	}

	public int getHauteur() {
		return hauteur;
	}

	public void set(int x, int y, int value) {
		if (x < 1 || x > getHauteur() || y < 1 || y > getLargeur())
			throw new IndexOutOfBoundsException();
		XY xy = new XY(x, y);
		if (value != 0)
			elements.put(xy, value);
		else
			elements.remove(xy);
	}

	public int get(int x, int y) {
		if (x < 1 || x > getHauteur() || y < 1 || y > getLargeur())
			throw new IndexOutOfBoundsException();
		XY xy = new XY(x, y);
		if (!elements.containsKey(xy))
			return 0;
		else
			return elements.get(xy).intValue();
	}

	public MatricePleine versPleine() {
		MatricePleine mp = new MatricePleine(hauteur, largeur);
		for (int x = 1; x <= hauteur; ++x)
			for (int y = 1; y <= largeur; ++y)
				mp.set(x, y, get(x, y));
		return mp;
	}

	public double getTauxRemplissage() {
		return getNbNonNuls() / (double) (getLargeur() * getHauteur());
	}

	private int getNbNonNuls() {
		return elements.size();
	}


}
