import java.util.Random;

enum Usage {
    LANDING,
    TAKEOFF
}

/**
 * Represents a plane coming to an airport with flight id, runway usage, current fuel level, emergency status, and current time spent waiting.
 */
public class Plane {
    private String id;
    private Usage type;
    private float fuelLevel;
    private boolean emergency;
    private int currentTime;

    static Random rand = new Random();

    /**
     * Creates a plane with a given id and random attributes
     * @param i, the flight id of the plane
     */
    public Plane(String i) {
        id = i;

        type = Usage.values()[rand.nextInt(2)];

        if (type == Usage.LANDING) {
            fuelLevel = (rand.nextFloat() * 99) + 1;
        } else {
            fuelLevel = 100.f;
        }

        emergency = (rand.nextInt(100) == 0) ? true : false;

        currentTime = rand.nextInt(9) - 6;
    }

    /**
     * Creates a plane with the given attributes
     * @param i, the flight id of the plane
     * @param t, the runway usage type of the plane
     * @param f, the fuel level of the plane
     * @param e, whether the plane is suffering an emergency
     * @param c, the current time the plane has waited for
     */
    public Plane(String i, Usage t, float f, boolean e, int c) {
        id = i;
        type = t;
        fuelLevel = f;
        emergency = e;
        currentTime = c;
    }

    // Getters and Setters

    /**
     * @return the flight id of the plane
     */
    public String getId() {
        return id;
    }

    /**
     * @return the runway usage type of the plane
     */
    public Usage getType() {
        return type;
    }

    /**
     * @return the current fuel level of the plane
     */
    public float getFuelLevel() {
        return fuelLevel;
    }

    /**
     * @return whether the plane is suffering an emergency
     */
    public boolean getEmergency() {
        return emergency;
    }

    /**
     * @return the current time spent waiting
     */
    public int getCurrentTime() {
        return currentTime;
    }

    /**
     * @return the priority level of the flight
     */
    public int getPriority() {
        if ((emergency || fuelLevel < 10.f) && type == Usage.LANDING) {
            // Emergency Landing
            return 4;
        } else if (fuelLevel < 30.f && type == Usage.LANDING) {
            // Critical Landing
            return 3;
        } else if ((emergency || currentTime > 3) && type == Usage.TAKEOFF) {
            // Urgent Takeoff
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * Updates the attributes of the plane to simulate one hour of time passing
     */
    public void updateValues() {
        if (type == Usage.LANDING) {
            fuelLevel = Math.max(fuelLevel - ((rand.nextFloat() * 10) + 5), 0.f);
        }
        if (!emergency) {
            emergency = (rand.nextInt(100) == 0) ? true : false;
        }
        currentTime++;
    }

    /**
     * Prints the flight details to the console
     */
    public void printDetails() {
        System.out.print("Flight: " + id + " ");
        System.out.print(type.name() + " ");
        String fuelString = String.format("%.1f", fuelLevel);
        System.out.print("Fuel: " + fuelString + "% ");
        System.out.print("Time: " + Integer.toString(currentTime) + " ");

        switch (getPriority()) {
            case 4:
                System.out.println("Emergency Landing");
                break;
            case 3:
                System.out.println("Critical Landing");
                break;
            case 2:
                System.out.println("Urgent Takeoff");
                break;
            case 1:
                if (type == Usage.TAKEOFF) {
                    System.out.println("Normal Takeoff");
                } else {
                    System.out.println("Normal Landing");
                }
                break;
            default:
                break;
        }
    }

    /**
     * @return a string containing the flight id of the plane in the form "Flight: {id}"
     */
    @Override
    public String toString() {
        return "Flight: " + id;
    }
}
