import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions.*;
import java.io.*;

public class PlaneTest {
	private final PrintStream standardOut = System.out;
	private final ByteArrayOutputStream outputCaptor = new ByteArrayOutputStream();

	//getPriority
	@Test
	@DisplayName("Landing plane with an emergency has priority 4")
	public void priorityTest1(){
		Plane plane = new Plane("1", Usage.LANDING, 100.f, true, 0);

		int actual = plane.getPriority();

		Assertions.assertEquals(4, actual);					
	}

	@Test
	@DisplayName("Landing plane with exactly 30% fuel and no emergency is still a normal landing")
	public void priorityTest2(){
		Plane plane = new Plane("2", Usage.LANDING, 30.f, false, 0);

		int actual = plane.getPriority();

		Assertions.assertEquals(1, actual);
	}

	//updateValues
	@Test
	@DisplayName("A plane does not go below 0% fuel when updating")
	public void updateValuesTest1(){
		Plane plane = new Plane("3", Usage.LANDING, 2.f, false, 0);

		plane.updateValues();
		float actual = plane.getFuelLevel();

		Assertions.assertEquals(0, actual);
	}

	@Test
	@DisplayName("Update values increases current time for a plane on the max initial time")
	public void updateValuesTest2(){
		Plane plane = new Plane("4", Usage.TAKEOFF, 100.f, false, 6);

		plane.updateValues();
		int actual = plane.getCurrentTime();

		Assertions.assertEquals(7, actual);
	}

	//printDetails
	@Test
	@DisplayName("A normal takeoff is displayed specifically as a takeoff and all other values are correct")
	public void printDetailsTest1(){
		System.setOut(new PrintStream(outputCaptor));
		Plane plane = new Plane("5", Usage.TAKEOFF, 100.f, false, 0);

		plane.printDetails();
		String actual = outputCaptor.toString().trim();

		Assertions.assertEquals("Flight: 5 TAKEOFF Fuel: 100.0% Time: 0 Normal Takeoff", actual);
		
		System.setOut(standardOut);
	}

	@Test
	@DisplayName("A plane landing at 25% fuel and no emergency is displayed as a critical landing and all other values are correct")
	public void printDetailsTest2(){
		System.setOut(new PrintStream(outputCaptor));
		Plane plane = new Plane("6", Usage.LANDING, 25.f, false, -4);

		plane.printDetails();
		String actual = outputCaptor.toString().trim();

		Assertions.assertEquals("Flight: 6 LANDING Fuel: 25.0% Time: -4 Critical Landing", actual);

		System.setOut(standardOut);
	}
}
