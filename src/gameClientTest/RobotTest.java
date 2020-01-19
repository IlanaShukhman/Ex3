package gameClientTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gameClient.Fruit;
import gameClient.Robot;
import utils.Point3D;

class RobotTest {

	@Test
	final void testInitFromJson() {
		String str="{\"Robot\":{\"id\":0,\"value\":0.0,\"src\":9,\"dest\":-1,\"speed\":1.0,\"pos\":\"35.19597880064568,32.10154696638656,0.0\"}}";
		Robot ACTUAL=new Robot();
		ACTUAL.initFromJson(str);
		Point3D p=new Point3D(35.19597880064568,32.10154696638656,0.0);
		Robot EXPECTED=new Robot(0,p, 0,9, -1, 1);
		assertTrue(ACTUAL.equals(EXPECTED), "ERR: Didn't return true when the robots are the same");

	}

	@Test
	final void testToJSON() {
		Point3D p=new Point3D(35.19597880064568,32.10154696638656,0.0);
		Robot EXPECTED=new Robot(0,p, 0,9, -1, 1);
		String str=EXPECTED.toJSON();
		Robot ACTUAL=new Robot();
		ACTUAL.initFromJson(str);
		assertTrue(ACTUAL.equals(EXPECTED), "ERR: Didn't return true when the fruits are the same");
	}

}
