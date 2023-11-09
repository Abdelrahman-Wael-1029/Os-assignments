import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/* 
can you write this program in java (take care of the print messages scenario (such as randomness and "arrived and waiting" message)) using given semaphore class:

"Problem Definition:
It is required to simulate a limited number of devices connected to a router’s Wi-Fi
using Java threading and semaphore. Routers can be designed to limit the number of
open connections. For example, a Router may wish to have only N connections at any
point in time. As soon as N connections are made, the Router will not accept other
incoming connections until an existing connection is released. Explain how
semaphores can be used by a Router to limit the number of concurrent connections.
Following rules should be applied:
. The Wi-Fi number of connected devices is initially empty.
. If a client is logged in (print a message that a client has logged in) and if it can
be served (means that it can reach the internet), then the client should
perform the following activities:
. Connect
. Perform online activity
. Log out
Note: these actions will be represented by printed messages, such that there is a
random waiting time between the printed messages when a client connects, do
some online activities and logged out.

. If a client arrives and all connections are occupied, it must wait until one of
the currently available clients finish his service and leave.
. After a client finishes his service, he leave and one of the waiting clients (if
exist) will connect to the internet.
Solution Design:
Your program must contain the following classes:
1. Router Class: This class contains a list of connections and methods to occupy a
connection and release a connection.
2. Semaphore Class:
class semaphore {
    protected int value = 0 ;
    protected semaphore() { value = 0 ; }
    protected semaphore(int initial) { value = initial ; }
    public synchronized void P() {
    
    value-- ;
    if (value < 0)
    try { wait() ; } catch( InterruptedException e ) { }
    }
    public synchronized void V() {
    value++ ; if (value <= 0) notify() ;
    }
   }
3. Device Class: Represent different devices (threads) that can be connected to the
router;
Each device has its own name (i.e. C1) and type (i.e. mobile, pc, tablet...) and it may
perform three activities: connect, perform online activity and disconnect/logout.
4. Network Class: This class contains the main method in which the user is asked
for two inputs:
. N: max number of connections a router can accept
. TC: total number of devices that wish to connect).
. TC lines that contain: Name of each device, and its Type.
Program Output:
You will print the output logs in a file, which simulates the execution order of the
devices threads and the printed messages of each device.
NOTE THAT: This is just an example not the only scenario that can be applied.
Sample Input:
What is the number of WI-FI Connections?
2
What is the number of devices Clients want to connect?
4
C1 mobile
C2 tablet
C3 pc
C4 pc
Sample Output:
- (C1)(mobile)arrived
- (C2)(tablet)arrived
- Connection 1: C1 Occupied
- Connection 2: C2 Occupied
- C4(pc) arrived and waiting
- C3(pc)arrived and waiting
- Connection 1: C1 login
- Connection 1: C1 performs online activity
- Connection 2: C2 login
- Connection 2: C2 performs online activity
- Connection 1: C1 Logged out
- Connection 1: C4 Occupied
- Connection 1: C4 log in
- Connection 1: C4 performs online activity
- Connection 2: C2 Logged out
- Connection 2: C3 Occupied
"
*/

class Router {

    private List<String> devices;

    public Semaphore semaphore;

    public Router(int maxConnections) {

        devices = new ArrayList<>();

        semaphore = new Semaphore(maxConnections);
    }

    public void occupyConnection(Device device) {

        semaphore.P();

        devices.add(device.getName());

        System.out.println("Connection " + devices.size() + ": " + device + " Occupied");

    }

    public void releaseConnection(Device device) {

        devices.remove(device.getName());

        semaphore.V();

        System.out.println("Connection " + (devices.size() + 1) + ": " + device + " Released");
    }
}

class Semaphore {
    protected int value = 0;

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
            } catch (InterruptedException e) {
            }
    }

    public synchronized void V() {
        value++;
        if (value <= 0)
            notify();
    }
}

class Device implements Runnable {

    private String name;

    private String type;

    private Router router;

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

        System.out.println(this + " arrived");

        // print "arrived and waiting" message when all connections are occupied but
        // how?

        router.occupyConnection(this);

        login();
        onlineActivity();
        logout();

        router.releaseConnection(this);
    }

    private void login() {

        System.out.println(this + " logged in");

        waitRandomTime(0, 5);
    }

    private void onlineActivity() {

        System.out.println(this + " performs online activity");

        waitRandomTime(0, 10);
    }

    private void logout() {

        System.out.println(this + " logged out");

        waitRandomTime(0, 5);
    }

    private void waitRandomTime(int min, int max) {
        try {

            int random = (int) (Math.random() * (max - min + 1) + min);

            Thread.sleep(random * 1000);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }

    public String toString() {
        return "(" + name + ")(" + type + ")";
    }
}

public class Network {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("What is the number of Wi-Fi connections?");

        int maxConnections = scanner.nextInt();

        System.out.println("What is the number of devices that want to connect?");

        int totalDevices = scanner.nextInt();

        Router router = new Router(maxConnections);

        List<Device> devices = new ArrayList<>();

        for (int i = 0; i < totalDevices; i++) {

            System.out.println("Enter the name and type of device " + (i + 1));

            String name = scanner.next();
            String type = scanner.next();

            Device device = new Device(name, type, router);

            devices.add(device);
        }

        scanner.close();

        List<Thread> threads = new ArrayList<>();

        for (Device device : devices) {

            Thread thread = new Thread(device);

            threads.add(thread);

            thread.start();
        }

        for (Thread thread : threads) {
            try {

                thread.join();
            } catch (InterruptedException e) {

                e.printStackTrace();
            }
        }

        System.out.println("Program done");
    }
}