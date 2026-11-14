package robots;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RobotTest {
	@Test
	public void test() {
		String monde = "...2...........";
		Robot r = new Robot(5, monde);
		assertEquals(5, r.getMinerai());
		assertEquals(1, r.getPosition());
		String journal = r.mission();
		assertEquals("avance\n"+
					"récolte : 0 [4] (2)\n"+
					"avance\n"+
					"récolte : 2 [5] (3)\n"+
					"avance\n"+
					"récolte : 0 [4] (4)\n"+
					"recule [3] (3)\n"+
					"recule [2] (2)\n"+
					"recule [1] (1)\n"+
					"recule [0] (0)\n"
					,journal);
		System.out.println(journal);
	}
}
