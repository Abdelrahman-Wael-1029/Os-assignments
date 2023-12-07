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

// A class to represent a process
class Process {
    String name; // process name
    int arrival; // arrival time
    int burst; // burst time
    int priority; // priority number
    int waiting; // waiting time
    int turnaround; // turnaround time
    int remaining; // remaining time for SRTF

    // Constructor
    public Process(String name, int arrival, int burst, int priority) {
        this.name = name;
        this.arrival = arrival;
        this.burst = burst;
        this.priority = priority;
        this.waiting = 0;
        this.turnaround = 0;
        this.remaining = burst;
    }
}

// A class to simulate the schedulers
class Scheduler {
    int n; // number of processes
    int cs; // context switching
    Process[] processes; // array of processes
    ArrayList<Process> order; // list of processes in execution order
    ArrayList<Integer> times; // list of times
    double avgWaiting; // average waiting time
    double avgTurnaround; // average turnaround time

    // Constructor
    public Scheduler(int n, int cs, Process[] processes) {
        this.n = n;
        this.cs = cs;
        this.processes = processes;
    }

    // Method to sort the processes by arrival time
    public void sortByArrival() {
        Arrays.sort(processes, new Comparator<Process>() {
            @Override
            public int compare(Process p1, Process p2) {
                return p1.arrival - p2.arrival;
            }
        });
    }

    private void init() {
        order = new ArrayList<>();
        times = new ArrayList<>();
        avgWaiting = 0;
        avgTurnaround = 0;
    }

    // Method to simulate non-preemptive SJF scheduler
    public void sjf() {
        init();
        // Sort the processes by arrival time
        sortByArrival();

        // Create a priority queue to store the ready processes
        // The priority is based on the burst time (shorter burst time has higher
        // priority)
        PriorityQueue<Process> queue = new PriorityQueue<>(new Comparator<Process>() {
            @Override
            public int compare(Process p1, Process p2) {
                return p1.burst - p2.burst;
            }
        });

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

                // Update the current time and the waiting time of the process
                currentTime += current.burst + cs;
                current.waiting = currentTime - current.arrival - current.burst - cs;
                // Update the turnaround time and the average waiting time
                current.turnaround = currentTime - cs;
                times.add(current.turnaround);

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
        // Sort the processes by arrival time
        sortByArrival();

        // Create a priority queue to store the ready processes
        // The priority is based on the remaining time (shorter remaining time has
        // higher priority)
        PriorityQueue<Process> queue = new PriorityQueue<>(new Comparator<Process>() {
            @Override
            public int compare(Process p1, Process p2) {
                return p1.remaining - p2.remaining;
            }
        });

        // Initialize the current time and the index of the next process
        int currentTime = 0;
        int nextProcess = 0;

        // Loop until all processes are executed
        while (!queue.isEmpty() || nextProcess < n) {
            boolean arrive = false;
            // Add the processes that have arrived by the current time to the queue
            while (nextProcess < n && processes[nextProcess].arrival <= currentTime) {
                queue.add(processes[nextProcess]);
                nextProcess++;
                arrive = true;
            }

            // If the queue is not empty, then execute the process with the shortest
            // remaining time
            if (!queue.isEmpty()) {
                // Remove the process from the queue and add it to the execution order
                Process current = queue.poll();
                if (!arrive && current == order.get(order.size() - 1)) {
                    order.removeLast();
                    times.removeLast();
                }
                order.add(current);
                // Check if the process has finished or not
                if (current.remaining == 1) {
                    // If the process has finished, then update the current time and the waiting
                    // time of the process
                    currentTime += current.remaining + cs;
                    current.waiting = currentTime - current.arrival - current.burst - cs;
                    // Update the turnaround time and the average waiting time
                    current.turnaround = currentTime - cs;
                    times.add(current.turnaround);

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

        // Calculate the average turnaround time
        avgTurnaround = avgWaiting + n * cs;
        for (Process p : processes) {
            avgTurnaround += p.burst;
        }

        // Divide the average waiting and turnaround time by the number of processes
        avgWaiting /= n;
        avgTurnaround /= n;
    }

    // Method to simulate non-preemptive priority scheduler
    public void priority() {
        init();
        // Sort the processes by arrival time
        sortByArrival();

        // Create a priority queue to store the ready processes
        // The priority is based on the priority number (lower priority number has
        // higher priority)
        PriorityQueue<Process> queue = new PriorityQueue<>(new Comparator<Process>() {
            @Override
            public int compare(Process p1, Process p2) {
                return p1.priority - p2.priority;
            }
        });

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

                // Update the current time and the waiting time of the process
                currentTime += current.burst + cs;
                current.waiting = currentTime - current.arrival - current.burst - cs;

                // Update the turnaround time and the average waiting time
                current.turnaround = currentTime - cs;
                times.add(current.turnaround);

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
        // Create an array of processes
        int n;
        n = 6;
        Process[] processes = new Process[n];
        processes[0] = new Process("P1", 0, 8, 1);
        processes[1] = new Process("P2", 1, 4, 1);
        processes[2] = new Process("P3", 2, 2, 1);
        processes[3] = new Process("P4", 3, 1, 1);
        processes[4] = new Process("P5", 4, 3, 1);
        processes[5] = new Process("P6", 5,     2, 1);
        // Create a scheduler object with 5 processes and 2 context switching
        Scheduler scheduler = new Scheduler(n, 0, processes);

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
    }
}
