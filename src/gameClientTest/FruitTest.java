package gameClientTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gameClient.Fruit;
import utils.Point3D;

class FruitTest {

	@Test
	final void testInitFromJson() {
		String str="{\"Fruit\":{\"value\":13.0,\"type\":1,\"pos\":\"35.2036278051956,32.10225131838408,0.0\"}}";
		Fruit ACTUAL=new Fruit();
		ACTUAL.initFromJson(str);
		Point3D p=new Point3D(35.2036278051956,32.10225131838408,0.0);
		Fruit EXPECTED=new Fruit(13, p, 1);
		
		assertTrue(ACTUAL.equals(EXPECTED), "ERR: Didn't return true when the fruits are the same");
	}

	@Test
	final void testToJSON() {
		Point3D p=new Point3D(35.2036278051956,32.10225131838408,0.0);
		Fruit EXPECTED=new Fruit(13, p, 1);
		String str=EXPECTED.toJSON();
		Fruit ACTUAL=new Fruit();
		ACTUAL.initFromJson(str);
		assertTrue(ACTUAL.equals(EXPECTED), "ERR: Didn't return true when the fruits are the same");
	}

	@Test
	final void testEqualsFruit() {
		Point3D p1=new Point3D(36,32,0.0);
		Fruit f1=new Fruit(13, p1, 1);
		
		Point3D p2=new Point3D(35,32,0.0);
		Fruit f2=new Fruit(13, p2, 1);
		
		assertFalse(f1.equals(f2), "ERR: Returned true when the fruits are different");
		
	}

}
