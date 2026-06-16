import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Implements a simulator for controlling air traffic by use of a priority queue
 */
public class AirTrafficSimulator {
    private static Plane[] dailyPlanes;
    private static ArrayList<Plane> dequeuedPlanes;
    private static AirTrafficPriorityQueue planes;

	private static String csvWrite = "";

    public static void main(String args[]) throws InterruptedException, IOException {
        for (int i = 0; i < 7; i++) {
            simulateDay();
			populateCSVRow();
        }

		FileWriter w = new FileWriter("simulation.csv");
		w.write(csvWrite);
		w.close();
    }

	private static void populateCSVRow(){
		for(Plane p : dequeuedPlanes){
			csvWrite += p.getId() + ",";
		}
		csvWrite += "\n";
	}

    /**
     * Simulates a day in the air traffic simulation
     * @throws InterruptedException
     */
    private static void simulateDay() throws InterruptedException {
        setupDay();
        for (int i = 0; i < 24; i++) {
            System.out.println("Hour " + Integer.toString(i + 1) + ":");
            simulateHour();
            planes.updateValues();
        }
    }

    /**
     * Sets up the plane priority queue for the daily simulation
     */
    private static void setupDay() {
        dailyPlanes = new Plane[100];
        dequeuedPlanes = new ArrayList<Plane>();
        for (int i = 0; i < 100; i++) {
            dailyPlanes[i] = new Plane("NZ" + Integer.toString(i));
        }
        planes = new AirTrafficPriorityQueue(dailyPlanes);
    }

    /**
     * Simulates an hour of the simulation in 5 seconds of real time
     * @throws InterruptedException
     */
    private static void simulateHour() throws InterruptedException {
        for (int i = 0; i < 4; i++) {
            // dequeue 2 planes
            for (int j = 0; j < 2; j++) {
                Plane currentPlane = planes.dequeue();
                if (currentPlane != null) {
                    System.out.print("Serving ");
                    currentPlane.printDetails();
                    dequeuedPlanes.add(currentPlane);
                }
            }
            Thread.sleep(5000 / 4);
        }
    }
}
