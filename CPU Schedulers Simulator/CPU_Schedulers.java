// Write a java program to simulate the following schedulers: 
// 1. Non-Preemptive Shortest- Job First (SJF) (using context switching) 
// 2. Shortest- Remaining Time First (SRTF) Scheduling (with the solving of starvation 
// problem using any way can be executed correctly) 
// 3. Non-preemptive Priority Scheduling (with the solving of starvation problem using any 
// way can be executed correctly) 
// Program Input 
// ▪ Number of processes 
// ▪ context switching 
// For Each Process you need to receive the following parameters from the user: 
// ▪ Process Name 
// ▪ Process Arrival Time 
// ▪ Process Burst Time 
// ▪ Process Priority Number 
// Program Output
//  For each scheduler output the following: 
// ▪ Processes execution order 
// ▪ Waiting Time for each process 
// ▪ Turnaround Time for each process 
// ▪ Average Waiting Time 
// ▪ Average Turnaround Time 

import java.util.*;
import java.util.Random;

// A class to represent a process
class Process {
    int id; // process id
    String name; // process name
    int arrival; // arrival time
    int burst; // burst time
    int priority; // priority number
    int waiting; // waiting time
    int turnaround; // turnaround time
    int remaining; // remaining time for SRTF
    int AG; // AG for RR
    String color;

    // Constructor
    public Process(int id, String name, int arrival, int burst, int priority, String color) {
        this.id = id;
        this.name = name;
        this.arrival = arrival;
        this.burst = burst;
        this.priority = priority;
        this.waiting = 0;
        this.turnaround = 0;
        this.remaining = burst;
        this.color = color;
        int randomFactor = new Random().nextInt(21);
        if (randomFactor < 10) {
            AG = randomFactor + arrival + burst;
        } else if (randomFactor > 10) {
            AG = 10 + arrival + burst;
        } else {
            AG = priority + arrival + burst;
        }
    }
}

// A class to simulate the schedulers
class Scheduler {
    private int n; // number of processes
    private int cs; // context switching
    private int quantum; // quantum for RR
    private Process[] processes; // array of processes
    private ArrayList<Process> order; // list of processes in execution order
    private ArrayList<Integer> times; // list of times
    private double avgWaiting; // average waiting time
    private double avgTurnaround; // average turnaround time
    private HashMap<Integer, Integer> quantums; // map of quantums for RR

    // Constructor
    public Scheduler(int n, int cs, int quantum, Process[] processes) {
        this.n = n;
        this.cs = cs;
        this.processes = processes;
        this.quantum = quantum;
        this.quantums = new HashMap<>();
    }

    private void init() {
        order = new ArrayList<>();
        times = new ArrayList<>();
        quantums = new HashMap<>();
        avgWaiting = 0;
        avgTurnaround = 0;

        for (int i = 0; i < n; i++) {
            processes[i].remaining = processes[i].burst;
            processes[i].waiting = 0;
            processes[i].turnaround = 0;
            quantums.put(processes[i].id, quantum);
        }

        Arrays.sort(processes, Comparator.comparingInt(p -> p.arrival));
    }

    public int getN() {
        return n;
    }

    public Process[] getProcesses() {
        return processes;
    }

    public ArrayList<Process> getOrder() {
        return order;
    }

    public ArrayList<Integer> getTimes() {
        return times;
    }

    public double getAvgWaiting() {
        return avgWaiting;
    }

    public double getAvgTurnaround() {
        return avgTurnaround;
    }

    // Method to simulate non-preemptive SJF scheduler
    public void sjf() {
        init();

        // Create a priority queue to store the ready processes
        // The priority is based on the burst time (shorter burst time has higher
        // priority)
        PriorityQueue<Process> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.burst));

        // Initialize the current time and the index of the next process
        int currentTime = 0;
        int nextProcess = 0;

        // Loop until all processes are executed
        while (!queue.isEmpty() || nextProcess < n) {
            // Add the processes that have arrived by the current time to the queue
            while (nextProcess < n && processes[nextProcess].arrival <= currentTime) {
                queue.add(processes[nextProcess]);
                nextProcess++;
            }

            // If the queue is not empty, then execute the process with the shortest burst
            // time
            if (!queue.isEmpty()) {
                // Remove the process from the queue and add it to the execution order
                Process current = queue.poll();
                order.add(current);

                // Update the current time and the waiting time and turnaround time of the
                // process
                currentTime += current.burst + cs;
                current.turnaround = currentTime - current.arrival - cs;
                current.waiting = current.turnaround - current.burst;
                times.add(currentTime - cs);

                avgWaiting += current.waiting;
                avgTurnaround += current.turnaround;
            } else {
                // If the queue is empty, then increment the current time
                currentTime++;
            }
        }

        // Divide the average waiting and turnaround time by the number of processes
        avgWaiting /= n;
        avgTurnaround /= n;
    }

    // Method to simulate SRTF scheduler
    public void srtf() {
        init();

        // Create a priority queue to store the ready processes
        // The priority is based on the remaining time (shorter remaining time has
        // higher priority)
        PriorityQueue<Process> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.remaining));

        // Initialize the current time and the index of the next process
        int currentTime = 0;
        int nextProcess = 0;

        // Loop until all processes are executed
        while (!queue.isEmpty() || nextProcess < n) {
            // Add the processes that have arrived by the current time to the queue
            while (nextProcess < n && processes[nextProcess].arrival <= currentTime) {
                queue.add(processes[nextProcess]);
                nextProcess++;
            }

            // If the queue is not empty, then execute the process with the shortest
            // remaining time
            if (!queue.isEmpty()) {
                // Remove the process from the queue and add it to the execution order
                Process current = queue.poll();
                if (!order.isEmpty() && current == order.get(order.size() - 1)) {
                    order.remove(order.size() - 1);
                    times.remove(times.size() - 1);
                }
                order.add(current);
                // Check if the process has finished or not
                if (current.remaining == 1) {
                    // If the process has finished, then update the current time and the waiting
                    // time and turnaround time of the process
                    currentTime++;
                    current.turnaround = currentTime - current.arrival;
                    current.waiting = current.turnaround - current.burst;
                    times.add(currentTime);

                    avgWaiting += current.waiting;
                    avgTurnaround += current.turnaround;
                } else {
                    // If the process has not finished, then decrement its remaining time and add it
                    // back to the queue
                    current.remaining--;
                    queue.add(current);

                    // Update the current time
                    currentTime++;
                    times.add(currentTime);
                }

            } else {
                // If the queue is empty, then increment the current time
                currentTime++;
            }
        }

        // Divide the average waiting and turnaround time by the number of processes
        avgWaiting /= n;
        avgTurnaround /= n;
    }

    // Method to simulate non-preemptive priority scheduler
    public void priority() {
        init();

        // Create a priority queue to store the ready processes
        // The priority is based on the priority number (lower priority number has
        // higher priority)
        PriorityQueue<Process> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.priority));

        // Initialize the current time and the index of the next process
        int currentTime = 0;
        int nextProcess = 0;

        // Loop until all processes are executed
        while (!queue.isEmpty() || nextProcess < n) {
            // Add the processes that have arrived by the current time to the queue
            while (nextProcess < n && processes[nextProcess].arrival <= currentTime) {
                queue.add(processes[nextProcess]);
                nextProcess++;
            }

            // If the queue is not empty, then execute the process with the highest priority
            if (!queue.isEmpty()) {
                // Remove the process from the queue and add it to the execution order
                Process current = queue.poll();
                order.add(current);

                // Update the current time and the waiting time and turnaround time of the
                // process
                currentTime += current.burst;
                current.turnaround = currentTime - current.arrival;
                current.waiting = current.turnaround - current.burst;
                times.add(currentTime);

                avgWaiting += current.waiting;
                avgTurnaround += current.turnaround;
            } else {
                // If the queue is empty, then increment the current time
                currentTime++;
            }
        }

        // Divide the average waiting and turnaround time by the number of processes
        avgWaiting /= n;
        avgTurnaround /= n;
    }

    protected int meanQuantum() {
        int sum = 0;
        for (int i : quantums.values())
            sum += i;
        return sum / n;
    }

    public void RR() {
        init();

        // for ready processes
        ArrayDeque<Process> ready = new ArrayDeque<>();
        // for sort processes by AG
        PriorityQueue<Process> AGQueue = new PriorityQueue<>(Comparator.comparingInt(p -> p.AG));

        int currentTime = 0;
        int nextProcess = 0;
        while (!ready.isEmpty() || nextProcess < n) {
            // add new arrived processes to ready queue
            while (nextProcess < n && processes[nextProcess].arrival <= currentTime) {
                ready.add(processes[nextProcess]);
                AGQueue.add(processes[nextProcess]);
                nextProcess++;
            }
            if (ready.isEmpty()) {
                currentTime++;
                continue;
            }

            Process current = ready.poll();
            if (current.remaining == 0) {
                continue;
            }
            // get quantum of current process
            int currentQuantum = quantums.get(current.id);
            order.add(current);
            // run ceil (50%)) of its Quantum time for current process
            currentTime += Math.min(current.remaining, (currentQuantum + 1) / 2);
            current.remaining -= Math.min(current.remaining, (currentQuantum + 1) / 2);

            boolean complete = false;
            // run second by second and check if the process is preempted or not
            for (int i = 0; i < currentQuantum / 2 && current.remaining > 0; i++) {
                // add new arrived processes to ready queue
                while (nextProcess < n && processes[nextProcess].arrival <= currentTime) {
                    ready.add(processes[nextProcess]);
                    AGQueue.add(processes[nextProcess]);
                    nextProcess++;
                }
                // if the process is preempted then add it to ready queue and update its quantum
                assert AGQueue.peek() != null;
                if (AGQueue.peek().AG < current.AG) {
                    if (!ready.isEmpty() && ready.peek().id != AGQueue.peek().id)
                        ready.addFirst(AGQueue.peek());
                    quantums.put(current.id, currentQuantum + currentQuantum / 2 - i);
                    complete = true;
                    break;
                }
                currentTime++;
                current.remaining--;
            }

            // if the process is not finished then add it to ready queue and update its
            // quantum
            if (current.remaining > 0) {
                ready.add(current);
                if (!complete)
                    quantums.put(current.id, currentQuantum + (meanQuantum() + 9) / 10);
            } else {
                // if the process is finished then update its waiting and turnaround time
                current.turnaround = currentTime - current.arrival;
                current.waiting = current.turnaround - current.burst;
                avgWaiting += current.waiting;
                avgTurnaround += current.turnaround;
                quantums.put(current.id, 0);
                AGQueue.remove(current);
            }
            times.add(currentTime);
        }
        avgWaiting /= n;
        avgTurnaround /= n;
    }

    // Method to print the output of the scheduler
    public void printOutput() {
        // Print the processes execution order
        System.out.println("Processes execution order:");
        // print order and times
        for (int i = 0; i < order.size(); i++) {
            System.out.print(order.get(i).name + "(" + times.get(i) + ") ");
        }
        System.out.println();

        // Print the waiting time and turnaround time for each process
        System.out.println("Waiting Time and Turnaround Time for each process:");
        Arrays.sort(processes, Comparator.comparingInt(p -> p.id));
        for (Process p : processes) {
            System.out.println(p.name + ": Waiting Time = " + p.waiting + ", Turnaround Time = " + p.turnaround);
        }

        // Print the average waiting time and average turnaround time
        System.out.println("Average Waiting Time = " + avgWaiting);
        System.out.println("Average Turnaround Time = " + avgTurnaround);
    }
}

// A class to test the schedulers
class CPU_Schedulers {
    public static void main(String[] args) {
        // test case
        // // number of processes
        // int num_processes = 2, RR_quantum = 3, context_switching = 0;

        // // Create an array of processes
        // Process[] processes = new Process[num_processes];
        // processes[0] = new Process(0, "P1", 0, 1, 3, "#123300");
        // processes[1] = new Process(1, "P2", 1, 7, 6, "#123300");

        // number of processes and context switching and quantum
        int num_processes, RR_quantum, context_switching;
        Scanner input = new Scanner(System.in);
        System.out.print("Enter number of processes: ");
        num_processes = input.nextInt();
        System.out.print("Enter context switching: ");
        context_switching = input.nextInt();
        System.out.print("Enter Round Robin quantum: ");
        RR_quantum = input.nextInt();

        // Create an array of processes
        Process[] processes = new Process[num_processes];
        for (int i = 0; i < num_processes; i++) {
            System.out.println("Enter process " + (i + 1) + " parameters:");
            System.out.print("Name: ");
            String name = input.next();
            System.out.print("Arrival Time: ");
            int arrival = input.nextInt();
            System.out.print("Burst Time: ");
            int burst = input.nextInt();
            System.out.print("Priority Number: ");
            int priority = input.nextInt();
            String color = "#";
            for (int j = 0; j < 6; j++) {
                color += Integer.toHexString(new Random().nextInt(16));
            }
            
            processes[i] = new Process(i + 1, name, arrival, burst, priority, color);
        }
        System.out.println();

        // Create a scheduler object with 5 processes and 2 context switching
        Scheduler scheduler = new Scheduler(num_processes, context_switching, RR_quantum, processes);
        
        // Simulate the non-preemptive SJF scheduler
        System.out.println("Non-preemptive SJF scheduler:");
        scheduler.sjf();
        scheduler.printOutput();
        System.out.println();

        // Simulate the SRTF scheduler
        System.out.println("SRTF scheduler:");
        scheduler.srtf();
        scheduler.printOutput();
        System.out.println();

        // Simulate the non-preemptive priority scheduler
        System.out.println("Non-preemptive priority scheduler:");
        scheduler.priority();
        scheduler.printOutput();
        System.out.println();

        // Simulate the RR scheduler
        System.out.println("RR scheduler:");
        scheduler.RR();
        scheduler.printOutput();
        System.out.println();

        GUI gui = new GUI(scheduler);
    }
}