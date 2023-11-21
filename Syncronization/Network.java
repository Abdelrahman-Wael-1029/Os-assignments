import java.util.*;
import java.io.*;

class Router {
    private final Map<Device, Integer> devices;

    private final List<Integer> availableConnections;

    public Semaphore semaphore;

    public Router(int maxConnections) {
        devices = new HashMap<>();

        availableConnections = new ArrayList<>();
        for (int i = 1; i <= maxConnections; i++) {
            availableConnections.add(i);
        }

        semaphore = new Semaphore(maxConnections);
    }

    public int getConnection(Device device) {
        return devices.get(device);
    }

    public void occupyConnection(Device device) {
        System.out.println(device + " arrived" + (semaphore.value <= 0 ? " and waiting" : ""));

        semaphore.P();

        devices.put(device, availableConnections.remove(0));

        System.out.println("Connection " + getConnection(device) + ": " + device.getName() + " Occupied");
    }

    public void releaseConnection(Device device) {
        System.out.println("Connection " + getConnection(device) + ": " + device.getName() + " Logged out");

        availableConnections.add(devices.remove(device));

        semaphore.V();
    }
}

class Semaphore {
    protected int value;

    protected Semaphore() {
        value = 0;
    }

    protected Semaphore(int initial) {
        value = initial;
    }

    public synchronized void P() {
        value--;
        if (value < 0)
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
    }

    public synchronized void V() {
        value++;
        if (value <= 0)
            notify();
    }
}

class Device implements Runnable {
    private final String name;

    private final String type;

    private final Router router;

    public Device(String name, String type, Router router) {
        this.name = name;
        this.type = type;
        this.router = router;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void run() {
        router.occupyConnection(this);
        waitRandomTime(1, 2);

        login();
        onlineActivity();
        logout();
    }

    private void login() {
        System.out.println("Connection " + router.getConnection(this) + ": " + getName() + " Login");

        waitRandomTime(1, 2);
    }

    private void onlineActivity() {
        System.out.println("Connection " + router.getConnection(this) + ": " + getName() + " Performs online activity");

        waitRandomTime(2, 4);
    }

    private void logout() {
        router.releaseConnection(this);

        waitRandomTime(1, 2);
    }

    private void waitRandomTime(int min, int max) {
        try {
            int random = (int) (Math.random() * (max - min + 1) + min);
            Thread.sleep(random * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return "(" + name + ")(" + type + ")";
    }
}

public class Network {

    // Define a fixed file path for the output history
    private static final String OUTPUT_FILE_PATH = "output_history.txt";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("What is the number of Wi-Fi connections?");

        int maxConnections = scanner.nextInt();

        System.out.println("What is the number of devices that want to connect?");

        int totalDevices = scanner.nextInt();

        Router router = new Router(maxConnections);

        List<Device> devices = new ArrayList<>();

        for (int i = 0; i < totalDevices; i++) {

            System.out.print("Enter the name and type of device " + (i + 1) + ": ");

            String name = scanner.next();
            String type = scanner.next();

            Device device = new Device(name, type, router);

            devices.add(device);
        }

        scanner.close();

        List<Thread> threads = new ArrayList<>();

        // Redirect output to a file
        try {
            System.setOut(new PrintStream(new FileOutputStream(OUTPUT_FILE_PATH, true)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        for (int i = 0; i < totalDevices; i++) {
            Thread thread = new Thread(devices.get(i));

            threads.add(thread);

            thread.start();

            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("----------------------------------------");
        // reassign to the standard output
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

        System.out.println("Program done (output history is in " + OUTPUT_FILE_PATH + ")");
    }
}
