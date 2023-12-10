import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.Border;

public class GUI extends JFrame {
    int WEIDTH = 1000, HEIGHT = 700;
    Scheduler scheduler;

    JPanel mainPanel = new JPanel();
    JPanel buttonsPanel = new JPanel();
    JPanel CPU_scheduler_graph = new JPanel();
    JPanel processes_information = new JPanel();
    JPanel statistics = new JPanel();

    JButton FCFS = new JButton("FCFS");
    JButton SJF = new JButton("SJF");
    JButton SRTF = new JButton("SRTF");
    JButton RR = new JButton("RR");

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
        setSize(WEIDTH, HEIGHT);
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
        buttonsPanel.add(FCFS);
        buttonsPanel.add(SJF);
        buttonsPanel.add(SRTF);
        buttonsPanel.add(RR);
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

    public GUI(Scheduler scheduler) {
        this.scheduler = scheduler;

        // main frame
        window();

        // main panel
        mainPanel();

        // buttons panel
        buttonsPanel();

        // // CPU scheduler graph panel
        CPU_scheduler_graph();
        // // end of CPU scheduler graph panel

        // // processes information panel
        processes_information();

        // statistics panel
        statistics();

        //cpu scheduler graph in the center left
        CPU_scheduler_graph.setBounds(0, 0, 700, 450);
        // processes information in the center right
        processes_information.setBounds(700, 0, 300, 450);
        // statistics in the bottom
        statistics.setBounds(0, 450, 1000, 150);

        mainPanel.add(processes_information);
        mainPanel.add(CPU_scheduler_graph);
        mainPanel.add(statistics);

        mainPanel.setBounds(0, 0, WEIDTH, HEIGHT);
        this.add(mainPanel, BorderLayout.CENTER);
        this.add(buttonsPanel, BorderLayout.SOUTH);
    }
}
