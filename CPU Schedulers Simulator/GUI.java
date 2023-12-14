import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

public class GUI extends JFrame {
    int WIDTH = 1200, HEIGHT = 700;
    Scheduler scheduler;

    JPanel mainPanel = new JPanel();
    JPanel buttonsPanel = new JPanel();
    JPanel CPU_scheduler_graph = new JPanel();
    JPanel processes_information = new JPanel();
    JPanel statistics = new JPanel();
    JPanel quantum_history = new JPanel();

    JButton SJF = new JButton("SJF");
    JButton SRTF = new JButton("SRTF");
    JButton Priority = new JButton("Priority");
    JButton AG = new JButton("AG");

    JLabel statistics_label = new JLabel("Statistics");
    JLabel processes_information_label = new JLabel("Processes Information");
    JLabel CPU_scheduler_graph_label = new JLabel("CPU Scheduler Graph");
    JLabel quantum_history_label = new JLabel("Quantum History");
    JLabel scheduler_name = new JLabel("Scheduler Name: ");
    JLabel avg_waiting_time = new JLabel("Average Waiting Time: ");
    JLabel avg_turnaround_time = new JLabel("Average Turnaround Time: ");
    JLabel scheduler_name_value = new JLabel(" ");
    JLabel avg_waiting_time_value = new JLabel(" ");
    JLabel avg_turnaround_time_value = new JLabel(" ");

    JTable table;

    JScrollPane scrollPane;

    private void window() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        setTitle("CPU Schedulers Simulator");
        this.setLayout(new BorderLayout(0, 0));
    }

    private void mainPanel() {
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.gray);
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 5));
    }

    private void buttonsPanel() {
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 150, 10));
        buttonsPanel.setBackground(Color.cyan);
        buttonsPanel.add(SJF);
        buttonsPanel.add(SRTF);
        buttonsPanel.add(Priority);
        buttonsPanel.add(AG);
    }

    private void CPU_scheduler_graph() {
        CPU_scheduler_graph.setLayout(null);
        CPU_scheduler_graph.setBackground(Color.gray);

        CPU_scheduler_graph_label.setFont(new Font("Serif", Font.BOLD, 20));
        CPU_scheduler_graph_label.setForeground(Color.red);
        CPU_scheduler_graph_label.setBounds(10, 10, 200, 20);

        CPU_scheduler_graph.add(CPU_scheduler_graph_label);
    }

    private void processes_information() {
        processes_information.setLayout(null);
        processes_information.setBackground(Color.gray);

        processes_information_label.setFont(new Font("Serif", Font.BOLD, 20));
        processes_information_label.setForeground(Color.red);
        processes_information_label.setBounds(10, 10, 200, 20);

        processes_information.add(processes_information_label);
    }

    private void statistics() {
        statistics.setLayout(null);
        statistics.setBackground(Color.gray);

        statistics_label.setFont(new Font("Serif", Font.BOLD, 20));
        statistics_label.setForeground(Color.red);
        statistics_label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.red));

        avg_waiting_time.setForeground(Color.white);
        avg_turnaround_time.setForeground(Color.white);
        scheduler_name.setForeground(Color.white);
        avg_waiting_time_value.setForeground(Color.white);

        statistics.add(statistics_label);
        statistics.add(scheduler_name);
        statistics.add(avg_waiting_time);
        statistics.add(avg_turnaround_time);
        statistics.add(scheduler_name_value);
        statistics.add(avg_waiting_time_value);
        statistics.add(avg_turnaround_time_value);

        statistics_label.setBounds(10, 10, 100, 20);
        scheduler_name.setBounds(10, 40, 200, 20);
        avg_waiting_time.setBounds(10, 70, 200, 20);
        avg_turnaround_time.setBounds(10, 100, 200, 20);
        scheduler_name_value.setBounds(200, 40, 200, 20);
        avg_waiting_time_value.setBounds(200, 70, 200, 20);
        avg_turnaround_time_value.setBounds(200, 100, 200, 20);
    }

    private void quantum_history() {
        quantum_history.setLayout(null);
        quantum_history.setBackground(Color.gray);

        quantum_history_label.setFont(new Font("Serif", Font.BOLD, 20));
        quantum_history_label.setForeground(Color.red);
        quantum_history_label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.red));

        quantum_history.add(quantum_history_label);

        quantum_history_label.setBounds(10, 10, 100, 20);
    }

    private void updateCPUSchedulerGraph(int cs) {
        CPU_scheduler_graph.removeAll();
        scrollPane = new JScrollPane(getGanttChart(cs));
        scrollPane.setBounds(0, 40, 700, 410);
        CPU_scheduler_graph.add(scrollPane);
        CPU_scheduler_graph.add(CPU_scheduler_graph_label);
        CPU_scheduler_graph.revalidate();
        CPU_scheduler_graph.repaint();
    }

    private void updateProcessesInformation() {
        table = new JTable(scheduler.getProcessesInformation(), scheduler.getProcessesInformationColumns());
        table.setPreferredSize(new Dimension(75 * table.getColumnCount(), 16 * table.getRowCount()));
        // make the table column width fit the content
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(0, 40, 490, 410);
        processes_information.removeAll();
        processes_information.add(scrollPane);
        processes_information.add(processes_information_label);
        processes_information.revalidate();
        processes_information.repaint();
        table.setDefaultEditor(Object.class, null);
    }

    public void updateQuantumHistory(boolean show) {
        quantum_history.removeAll();
        quantum_history.add(quantum_history_label);
        if (show) {
            table = new JTable(scheduler.getQuantumHistory(), scheduler.getQuantumHistoryColumns());
            table.setPreferredSize(new Dimension(75 * table.getColumnCount(), 16 * table.getRowCount()));
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            scrollPane = new JScrollPane(table);
            scrollPane.setBounds(0, 40, 490, 120);
            quantum_history.add(scrollPane);
            table.setDefaultEditor(Object.class, null);
        }
        quantum_history.revalidate();
        quantum_history.repaint();
    }

    private Component getGanttChart(int cs) {
        JPanel ganttChart = new JPanel();
        ganttChart.setLayout(null);
        ganttChart.setBackground(Color.gray);
        ganttChart.setBorder(BorderFactory.createLineBorder(Color.gray, 5));
        ganttChart.setBounds(0, 0, 700, 450);
        // add the data from scheduler order
        ArrayList<Process> processesOrder = scheduler.getOrder(), processes = scheduler.getProcesses();
        ArrayList<Integer> times = scheduler.getTimes();
        ganttChart.setPreferredSize(new Dimension(processes.size() * 101, processes.size() * 50));
        // get the max time to make the graph
        int endTime = times.get(times.size() - 1), x = 100 + processesOrder.get(0).arrival * processes.size() * 100 / endTime;
        // make the graph.
        for (int i = 0; i < processesOrder.size(); i++) {
            JLabel label = new JLabel();
            int width = (times.get(i) - (i == 0 ? processesOrder.get(i).arrival : times.get(i - 1) + cs)) * processes.size() * 100 / endTime;
            label.setBounds(x, processesOrder.get(i).id * 50, width, 50);
            label.setOpaque(true);
            label.setBackground(Color.decode(processesOrder.get(i).color));
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);
            ganttChart.add(label);
            x += width + cs;
            // display a tooltip when hover over the process
            label.setToolTipText((i == 0 ? processesOrder.get(i).arrival : times.get(i - 1) + cs) + " - " + times.get(i) + " ms");
        }
        // make the y axis
        for (int i = 0; i < processes.size(); i++) {
            JLabel label = new JLabel(processes.get(i).name);
            label.setBounds(0, i * 50, 100, 50);
            label.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.black));
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);
            ganttChart.add(label);
        }
        return ganttChart;
    }

    public GUI(Scheduler scheduler) {
        this.scheduler = scheduler;

        // main frame
        window();

        // main panel
        mainPanel();

        // buttons panel
        buttonsPanel();

        // CPU scheduler graph panel
        CPU_scheduler_graph();

        // processes information panel
        processes_information();

        // statistics panel
        statistics();

        // quantum history panel
        quantum_history();

        //cpu scheduler graph in the center left
        CPU_scheduler_graph.setBounds(0, 0, 700, 450);
        // processes information in the center right
        processes_information.setBounds(700, 0, 500, 450);
        // statistics in the bottom
        statistics.setBounds(0, 450, 700, 160);
        // quantum history in the bottom right
        quantum_history.setBounds(700, 450, 500, 160);

        mainPanel.add(processes_information);
        mainPanel.add(CPU_scheduler_graph);
        mainPanel.add(statistics);
        mainPanel.add(quantum_history);

        mainPanel.setBounds(0, 0, WIDTH, HEIGHT);
        this.add(mainPanel, BorderLayout.CENTER);
        this.add(buttonsPanel, BorderLayout.SOUTH);

        SJF.addActionListener(e -> {
            scheduler.SJF();
            scheduler.printOutput();
            scheduler_name_value.setText("SJF");
            avg_waiting_time_value.setText(String.valueOf(scheduler.getAvgWaitingTime()));
            avg_turnaround_time_value.setText(String.valueOf(scheduler.getAvgTurnaroundTime()));
            updateCPUSchedulerGraph(scheduler.getContextSwitching());
            updateProcessesInformation();
            updateQuantumHistory(false);
        });

        SRTF.addActionListener(e -> {
            scheduler.SRTF();
            scheduler.printOutput();
            scheduler_name_value.setText("SRTF");
            avg_waiting_time_value.setText(String.valueOf(scheduler.getAvgWaitingTime()));
            avg_turnaround_time_value.setText(String.valueOf(scheduler.getAvgTurnaroundTime()));
            updateCPUSchedulerGraph(0);
            updateProcessesInformation();
            updateQuantumHistory(false);
        });

        Priority.addActionListener(e -> {
            scheduler.Priority();
            scheduler.printOutput();
            scheduler_name_value.setText("Priority");
            avg_waiting_time_value.setText(String.valueOf(scheduler.getAvgWaitingTime()));
            avg_turnaround_time_value.setText(String.valueOf(scheduler.getAvgTurnaroundTime()));
            updateCPUSchedulerGraph(0);
            updateProcessesInformation();
            updateQuantumHistory(false);
        });

        AG.addActionListener(e -> {
            scheduler.AG();
            scheduler.printOutput();
            scheduler_name_value.setText("AG");
            avg_waiting_time_value.setText(String.valueOf(scheduler.getAvgWaitingTime()));
            avg_turnaround_time_value.setText(String.valueOf(scheduler.getAvgTurnaroundTime()));
            updateCPUSchedulerGraph(0);
            updateProcessesInformation();
            updateQuantumHistory(true);
        });
    }
}
