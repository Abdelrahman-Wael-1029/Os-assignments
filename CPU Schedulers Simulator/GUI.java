import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class GUI extends JFrame {
    int WIDTH = 1200, HEIGHT = 700;
    Scheduler scheduler;

    JPanel mainPanel = new JPanel();
    JPanel buttonsPanel = new JPanel();
    JPanel CPU_scheduler_graph = new JPanel();
    JPanel processes_information = new JPanel();
    JPanel statistics = new JPanel();

    JButton SJF = new JButton("SJF");
    JButton SRTF = new JButton("SRTF");
    JButton Priority = new JButton("Priority");
    JButton AG = new JButton("AG");

    JLabel statistics_label = new JLabel("Statistics");
    JLabel processes_information_label = new JLabel("Processes Information");
    JLabel CPU_scheduler_graph_label = new JLabel("CPU Scheduler Graph");
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

    private Component getGanttChart() {
        JPanel ganttChart = new JPanel();
        ganttChart.setLayout(null);
        ganttChart.setBackground(Color.gray);
        ganttChart.setBorder(BorderFactory.createLineBorder(Color.gray, 5));
        ganttChart.setBounds(0, 0, 700, 450);
        // add the data from scheduler order
        ArrayList<Process> processesOrder = scheduler.getOrder(), processes = scheduler.getProcesses();
        ArrayList<Integer> times = scheduler.getTimes();
        // get the max time to make the graph
        int endTime = times.get(times.size() - 1), x = 100;
        // make the graph.
        for (int i = 0; i < processesOrder.size(); i++) {
            JLabel label = new JLabel();
            int width = (times.get(i) - (i == 0 ? 0 : times.get(i - 1))) * 1000 / endTime;
            label.setBounds(x, processesOrder.get(i).id * 50, width, 50);
            label.setOpaque(true);
            label.setBackground(Color.decode(processesOrder.get(i).color));
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);
            ganttChart.add(label);
            x += width;
            // display a tooltip when hover over the process
            label.setToolTipText(times.get(i) + "ms");
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

        //cpu scheduler graph in the center left
        CPU_scheduler_graph.setBounds(0, 0, 700, 450);
        // processes information in the center right
        processes_information.setBounds(700, 0, 500, 450);
        // statistics in the bottom
        statistics.setBounds(0, 450, 1200, 150);

        mainPanel.add(processes_information);
        mainPanel.add(CPU_scheduler_graph);
        mainPanel.add(statistics);

        mainPanel.setBounds(0, 0, WIDTH, HEIGHT);
        this.add(mainPanel, BorderLayout.CENTER);
        this.add(buttonsPanel, BorderLayout.SOUTH);

        SJF.addActionListener(e -> {
            scheduler.sjf();
            scheduler_name_value.setText("SJF");
            avg_waiting_time_value.setText(String.valueOf(scheduler.getAvgWaitingTime()));
            avg_turnaround_time_value.setText(String.valueOf(scheduler.getAvgTurnaroundTime()));
            // processes information
            table = new JTable(scheduler.getProcessesInformation(), scheduler.getProcessesInformationColumns());
            scrollPane = new JScrollPane(table);
            scrollPane.setBounds(0, 40, 490, 410);
            processes_information.removeAll();
            processes_information.add(scrollPane);
            processes_information.add(processes_information_label);
            processes_information.revalidate();
            processes_information.repaint();
            table.setDefaultEditor(Object.class, null);
            // cpu scheduler graph
            CPU_scheduler_graph.removeAll();
            scrollPane = new JScrollPane(getGanttChart());
            scrollPane.setBounds(0, 40, 700, 410);
            CPU_scheduler_graph.add(scrollPane);
            CPU_scheduler_graph.add(CPU_scheduler_graph_label);
            CPU_scheduler_graph.revalidate();
            CPU_scheduler_graph.repaint();
        });

        SRTF.addActionListener(e -> {
            scheduler.srtf();
            scheduler_name_value.setText("SRTF");
            avg_waiting_time_value.setText(String.valueOf(scheduler.getAvgWaitingTime()));
            avg_turnaround_time_value.setText(String.valueOf(scheduler.getAvgTurnaroundTime()));
            // processes information
            table = new JTable(scheduler.getProcessesInformation(), scheduler.getProcessesInformationColumns());
            scrollPane = new JScrollPane(table);
            scrollPane.setBounds(0, 40, 490, 410);
            processes_information.removeAll();
            processes_information.add(scrollPane);
            processes_information.add(processes_information_label);
            processes_information.revalidate();
            processes_information.repaint();
            table.setDefaultEditor(Object.class, null);
            // cpu scheduler graph
            CPU_scheduler_graph.removeAll();
            scrollPane = new JScrollPane(getGanttChart());
            scrollPane.setBounds(0, 40, 700, 410);
            CPU_scheduler_graph.add(scrollPane);
            CPU_scheduler_graph.add(CPU_scheduler_graph_label);
            CPU_scheduler_graph.revalidate();
            CPU_scheduler_graph.repaint();
        });

        Priority.addActionListener(e -> {
            scheduler.priority();
            scheduler_name_value.setText("Priority");
            avg_waiting_time_value.setText(String.valueOf(scheduler.getAvgWaitingTime()));
            avg_turnaround_time_value.setText(String.valueOf(scheduler.getAvgTurnaroundTime()));
            // processes information
            table = new JTable(scheduler.getProcessesInformation(), scheduler.getProcessesInformationColumns());
            scrollPane = new JScrollPane(table);
            scrollPane.setBounds(0, 40, 490, 410);
            processes_information.removeAll();
            processes_information.add(scrollPane);
            processes_information.add(processes_information_label);
            processes_information.revalidate();
            processes_information.repaint();
            table.setDefaultEditor(Object.class, null);
            // cpu scheduler graph
            CPU_scheduler_graph.removeAll();
            scrollPane = new JScrollPane(getGanttChart());
            scrollPane.setBounds(0, 40, 700, 410);
            CPU_scheduler_graph.add(scrollPane);
            CPU_scheduler_graph.add(CPU_scheduler_graph_label);
            CPU_scheduler_graph.revalidate();
            CPU_scheduler_graph.repaint();
        });

        AG.addActionListener(e -> {
            scheduler.AG();
            scheduler_name_value.setText("AG");
            avg_waiting_time_value.setText(String.valueOf(scheduler.getAvgWaitingTime()));
            avg_turnaround_time_value.setText(String.valueOf(scheduler.getAvgTurnaroundTime()));
            // processes information
            table = new JTable(scheduler.getProcessesInformation(), scheduler.getProcessesInformationColumns());
            scrollPane = new JScrollPane(table);
            scrollPane.setBounds(0, 40, 490, 410);
            processes_information.removeAll();
            processes_information.add(scrollPane);
            processes_information.add(processes_information_label);
            processes_information.revalidate();
            processes_information.repaint();
            table.setDefaultEditor(Object.class, null);
            // cpu scheduler graph
            CPU_scheduler_graph.removeAll();
            scrollPane = new JScrollPane(getGanttChart());
            scrollPane.setBounds(0, 40, 700, 410);
            CPU_scheduler_graph.add(scrollPane);
            CPU_scheduler_graph.add(CPU_scheduler_graph_label);
            CPU_scheduler_graph.revalidate();
            CPU_scheduler_graph.repaint();
        });
    }
}
