import java.util.*;

// A class to represent a process
class Process {
    int id; // process id
    String name; // process name
    int arrival; // arrival time
    int burst; // burst time
    int priority; // priority number
    int waiting; // waiting time (turnaround - burst)
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
    private final int n; // number of processes
    private final int cs; // context switching
    private final int quantum; // quantum for RR
    private final Process[] processes; // array of processes
    private ArrayList<Process> order; // list of processes in execution order
    private ArrayList<ArrayList<Integer>> times; // list of times
    private double avgWaiting; // average waiting time
    private double avgTurnaround; // average turnaround time
    private HashMap<Integer, Integer> quantums; // map of quantums for RR
    private final ArrayList<ArrayList<Integer>> quantumHistory; // list of quantums for RR

    // Constructor
    public Scheduler(int n, int cs, int quantum, Process[] processes) {
        this.n = n;
        this.cs = cs;
        this.processes = processes;
        this.quantum = quantum;
        this.quantums = new HashMap<>();
        this.quantumHistory = new ArrayList<>();
    }

    private void init() {
        order = new ArrayList<>();
        times = new ArrayList<>();
        quantums = new HashMap<>();
        quantumHistory.clear();
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

    public ArrayList<Process> getProcesses() {
        return new ArrayList<>(Arrays.asList(processes));
    }

    public Object[][] getProcessesInformation(int type) {
        // return processes rows for JPanel
        Object[][] rows = new Object[n][8 - type];
        for (int i = 0; i < n; i++) {
            rows[i][0] = processes[i].name;
            rows[i][1] = processes[i].arrival;
            rows[i][2] = processes[i].burst;
            if (type < 2)
                rows[i][3] = processes[i].priority;
            if (type == 0)
                rows[i][4] = processes[i].AG;
            rows[i][5 - type] = processes[i].color;
            rows[i][6 - type] = processes[i].waiting;
            rows[i][7 - type] = processes[i].turnaround;
        }
        return rows;
    }

    public Object[][] getQuantumHistory() {
        // return quantum history rows for JPanel
        Object[][] rows = new Object[quantumHistory.size()][n];
        for (int i = 0; i < quantumHistory.size(); i++) {
            for (int j = 0; j < n; j++) {
                rows[i][j] = quantumHistory.get(i).get(j);
            }
        }
        return rows;
    }

    public Object[] getProcessesInformationColumns(int type) {
        // return processes columns names for JPanel
        Object[] columns = new Object[8 - type];
        columns[0] = "Name";
        columns[1] = "Arrival Time";
        columns[2] = "Burst Time";
        if (type < 2)
            columns[3] = "Priority";
        if (type == 0)
            columns[4] = "AG";
        columns[5 - type] = "Color";
        columns[6 - type] = "Waiting Time";
        columns[7 - type] = "Turnaround Time";
        return columns;
    }

    public Object[] getQuantumHistoryColumns() {
        // return quantum history columns names for JPanel
        Object[] columns = new Object[n];
        for (int i = 0; i < n; i++) {
            columns[i] = processes[i].name;
        }
        return columns;
    }

    public ArrayList<Process> getOrder() {
        return order;
    }

    public ArrayList<ArrayList<Integer>> getTimes() {
        return times;
    }

    public double getAvgWaitingTime() {
        return avgWaiting;
    }

    public double getAvgTurnaroundTime() {
        return avgTurnaround;
    }
    // Method to simulate non-preemptive SJF scheduler

    public void SJF() {
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
                times.add(new ArrayList<>(Arrays.asList(currentTime - current.burst - cs, currentTime - cs)));

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
        Arrays.sort(processes, Comparator.comparingInt(p -> p.id));
    }
    // Method to simulate SRTF scheduler

    public void SRTF() {
        init();

        // Create a priority queue to store the ready processes
        // The priority is based on the remaining time (shorter remaining time has
        // higher priority)
        PriorityQueue<Process> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.remaining - p.waiting));

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
                    // If the process has finished, then update the current time and turnaround time of the process
                    currentTime++;
                    current.turnaround = currentTime - current.arrival;

                    avgTurnaround += current.turnaround;
                } else {
                    // If the process has not finished, then decrement its remaining time and add it
                    // back to the queue
                    current.remaining--;
                    queue.add(current);

                    // Update the current time
                    currentTime++;
                }
                times.add(new ArrayList<>(Arrays.asList(Math.max(current.arrival, (times.size() != 0? times.get(times.size() - 1).get(1) : 0)), currentTime)));
                for (Process p : queue) {
                    if (p != current) {
                        p.waiting++;
                        avgWaiting++;
                    }
                }
            } else {
                // If the queue is empty, then increment the current time
                currentTime++;
            }
        }

        // Divide the average waiting and turnaround time by the number of processes
        avgWaiting /= n;
        avgTurnaround /= n;
        Arrays.sort(processes, Comparator.comparingInt(p -> p.id));
    }
    // Method to simulate non-preemptive priority scheduler

    public void Priority() {
        init();

        // Create a priority queue to store the ready processes
        // The priority is based on the priority number (lower priority number has
        // higher priority)
        PriorityQueue<Process> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.priority - p.waiting));

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
                times.add(new ArrayList<>(Arrays.asList(currentTime - current.burst, currentTime)));

                avgTurnaround += current.turnaround;

                for (Process p : queue) {
                    if (p != current)
                        p.waiting = currentTime - p.arrival;
                }
            } else {
                // If the queue is empty, then increment the current time
                currentTime++;
            }
        }

        // Divide the average waiting and turnaround time by the number of processes
        avgWaiting = Arrays.stream(processes).mapToDouble(p -> p.waiting).sum() / n;
        avgTurnaround /= n;
        Arrays.sort(processes, Comparator.comparingInt(p -> p.id));
    }

    protected int meanQuantum() {
        int sum = 0;
        for (int i : quantums.values())
            sum += i;
        return sum / n;
    }

    public void AG() {
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
            if (!order.isEmpty() && current == order.get(order.size() - 1)) {
                order.remove(order.size() - 1);
                times.remove(times.size() - 1);
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
            times.add(new ArrayList<>(Arrays.asList(Math.max(current.arrival, (times.size() != 0? times.get(times.size() - 1).get(1) : 0)), currentTime)));
            quantumHistory.add(new ArrayList<>(quantums.values()));
        }
        avgWaiting /= n;
        avgTurnaround /= n;
        Arrays.sort(processes, Comparator.comparingInt(p -> p.id));
    }
    // Method to print the output of the scheduler

    public void printOutput() {
        // Print the processes execution order
        System.out.println("Processes execution order:");
        // print order and times
        for (int i = 0; i < order.size(); i++) {
            System.out.print(order.get(i).name + "(" + times.get(i).get(0) + " -> " + times.get(i).get(1) + ") ");
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
        // number of processes and context switching and quantum
//        int num_processes, RR_quantum, context_switching;
//        Scanner input = new Scanner(System.in);
//        System.out.print("Enter number of processes: ");
//        num_processes = input.nextInt();
//        System.out.print("Enter context switching: ");
//        context_switching = input.nextInt();
//        System.out.print("Enter Round Robin quantum: ");
//        RR_quantum = input.nextInt();
//
//        // Create an array of processes
//        Process[] processes = new Process[num_processes];
//        for (int i = 0; i < num_processes; i++) {
//            System.out.println("Enter process " + (i + 1) + " parameters:");
//            System.out.print("Name: ");
//            String name = input.next();
//            System.out.print("Arrival Time: ");
//            int arrival = input.nextInt();
//            System.out.print("Burst Time: ");
//            int burst = input.nextInt();
//            System.out.print("Priority Number: ");
//            int priority = input.nextInt();
//            String color;
//            do {
//                System.out.print("Color (in hexadecimal): #");
//                color = "#" + input.next();
//            } while (!color.matches("#[0-9a-fA-F]{6}"));
//
//            processes[i] = new Process(i, name, arrival, burst, priority, color);
//        }
        int num_processes = 5, RR_quantum = 2, context_switching = 1;

        Process[] processes = new Process[num_processes];
        for (int i = 0; i < num_processes; i++) {
            String name = "P" + (i + 1);
            int arrival = new Random().nextInt(10);
            int burst = new Random().nextInt(10) + 1;
            int priority = new Random().nextInt(10);
            String color = String.format("#%06x", new Random().nextInt(0xffffff + 1));
            processes[i] = new Process(i, name, arrival, burst, priority, color);
        }
        
        // Create a scheduler object with 5 processes and 2 context switching
        Scheduler scheduler = new Scheduler(num_processes, context_switching, RR_quantum, processes);

        new GUI(scheduler);
    }
}