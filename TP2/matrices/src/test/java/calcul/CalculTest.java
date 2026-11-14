package calcul;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CalculTest {
	@Test
	public void tester() {
		Calcul c = new Calcul(2, 3);
		c.set(1, 1, 7);
		c.set(1, 2, 3);
		c.set(1, 3, 18);
		assertEquals(c.get(1, 1), 7);
		assertEquals(c.get(1, 2), 3);
		assertEquals(c.get(1, 3), 18);
		c.optimiser();
		assertEquals(c.get(1, 1), 7);
		assertEquals(c.get(1, 2), 3);
		assertEquals(c.get(1, 3), 18);
	}
}
