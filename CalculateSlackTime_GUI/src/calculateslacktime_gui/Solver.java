/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculateslacktime_gui;

/**
 *
 * @author pankajbirat
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;


class Solver extends JFrame implements Runnable {

    boolean[] visited; //booean array used for schedule job
    int size; // size of job array 
    static int indicator = 0;
    static JLabel[][] label = new JLabel[40][40]; // lable for add into jframe
    Thread DepAni;
    Border border, border1, border2, border3, border4, border5;
    Panel MyPanel;
    Vector<Integer> pvec;

    // function for checking space key strok

    public static void waitForSpace() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        KeyEventDispatcher dispatcher = new KeyEventDispatcher() {
            // Anonymous class invoked from EDT
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    latch.countDown();
                }
                return false;
            }

        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher);
        latch.await();  // current thread waits here until countDown() is called
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(dispatcher);
    }

    // constructor of Sovler class
    Solver() {
    }

    // constructor with parameter
    Solver(int s) {

        super("Job Scheduler ");

        //DepAni = new Thread();
        setMinimumSize(new Dimension(800, 600));
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);

        setLocation(x, y);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.setLayout(new BorderLayout());
        // MyJframe = new Solver();

        border = BorderFactory.createLineBorder(Color.yellow);
        border2 = BorderFactory.createLineBorder(Color.green);
        border3 = BorderFactory.createLineBorder(Color.red);
        border4 = BorderFactory.createLineBorder(Color.blue);
        border5 = BorderFactory.createLineBorder(Color.black);

        MyPanel = new Panel(new GridLayout(s + 1, 6, 4, 4));
        MyPanel.setBackground(Color.pink);

       // JScrollPane scrPane = new JScrollPane(MyPanel);
        //getContentPane().add(scrPane);
        //this.add(scrPane);
        this.setVisible(true);
        this.add(MyPanel, BorderLayout.CENTER);

        visited = new boolean[s];
        size = s;
        for (int i = 0; i < s; i++) {
            visited[i] = false;
        }
    }

    // function for checking all jobs are scheduled or not
    public boolean AllVisited() {
        for (int i = 0; i < size; i++) {
            if (!visited[i]) {
                return false;
            }
        }
        return true;
    }

    public void Set_Panel(JobDetail job[]) {
        label[0][0] = new JLabel("Job name");
        label[0][0].setHorizontalAlignment(SwingConstants.CENTER);
        label[0][0].setBorder(border5);
        MyPanel.add(label[0][0]);

        label[0][1] = new JLabel("Length");
        label[0][1].setHorizontalAlignment(SwingConstants.CENTER);
        label[0][1].setBorder(border5);
        MyPanel.add(label[0][1]);

        label[0][2] = new JLabel("Start Time");
        label[0][2].setHorizontalAlignment(SwingConstants.CENTER);
        label[0][2].setBorder(border5);
        MyPanel.add(label[0][2]);

        label[0][3] = new JLabel("End Time");
        label[0][3].setHorizontalAlignment(SwingConstants.CENTER);
        label[0][3].setBorder(border5);
        MyPanel.add(label[0][3]);

        label[0][4] = new JLabel("In Critical");
        label[0][4].setHorizontalAlignment(SwingConstants.CENTER);
        label[0][4].setBorder(border5);
        MyPanel.add(label[0][4]);

        label[0][5] = new JLabel("Slack Time");
        label[0][5].setHorizontalAlignment(SwingConstants.CENTER);
        label[0][5].setBorder(border5);
        MyPanel.add(label[0][5]);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < 6; j++) {

                if (j == 0) {
                    label[i + 1][j] = new JLabel(job[i].name);
                }
                if (j == 1) {

                    label[i + 1][j] = new JLabel(Double.toString(job[i].CompleteTime));
                }
                if (j == 2) {

                    label[i + 1][j] = new JLabel("null");

                }
                if (j == 3) {
                    label[i + 1][j] = new JLabel("null");
                }
                if (j == 4) {
                    label[i + 1][j] = new JLabel("null");
                }
                if (j == 5) {
                    label[i + 1][j] = new JLabel("null");
                }

                label[i + 1][j].setBorder(border);

                label[i + 1][j].setHorizontalAlignment(SwingConstants.CENTER);

                // Thread.sleep(1000);
                MyPanel.add(label[i + 1][j]);
                //jf.add(p, BorderLayout.CENTER);

            }
        }
        this.pack();
    }

    // function  for first time Schedule all the jobs
    public void Calculate_Start_End_Time(JobDetail job[]) throws InterruptedException {

        Set_Panel(job);

        //Thread.sleep(100);
        JOptionPane.showMessageDialog(this, "Calculate Start And End Time for Jobs");

        int sd = 1;

        while (!AllVisited()) { // while all jobs are not scheduled

            for (int i = 0; i < size; i++) { // check for every job
                if (!visited[i]) { // if this job not scheduled
                    Iterator<Integer> DependenciesIterator = job[i].Dependencies.iterator();
                    boolean flag = true;
                    double maxp = 0;

                    label[i + 1][3].setOpaque(true);
                    label[i + 1][0].setBorder(border5);
                    label[i + 1][0].setOpaque(true);
                    label[i + 1][0].setBackground(Color.yellow);

                    pvec = new Vector<Integer>();

                    while (DependenciesIterator.hasNext()) { // check all jobs on which this job is dependent
                        int parent = DependenciesIterator.next();

                        label[parent + 1][3].setOpaque(true);
                        label[parent + 1][3].setBackground(Color.blue);
                        label[parent + 1][3].setBorder(border);
                        pvec.add(parent);

                        if (!visited[parent]) { // if any parent job is not scheduled then we cannot schedule this job try to first schedule parent of this job 
                            flag = false;
                           // break;
                        }
                        maxp = (maxp > job[parent].EndTime) ? maxp : job[parent].EndTime;    // take maximum finish time among all dependencies
                    }

                    this.pack();

                    /*   int coun = 50;
                    
                     Thread t = new Thread(this);
                     t.start();
                    
                     Random randomGenerator = new Random();
                     while(glob != 1) {
                     int g1 = randomGenerator.nextInt(255);
                     int g2 = randomGenerator.nextInt(255);
                     int g3 = randomGenerator.nextInt(255);
                     for(int g = 0; g < vector.size(); g++) {
                     int temp = vector.elementAt(g);
                     //  System.out.println(temp);
                     label[temp][3].setOpaque(true);
                     label[temp][3].setBackground(new Color(g1, g2, g3));
                     }
                     this.pack();
                     Thread.sleep(50);
                
                     }
                     t.stop();
                     */
                     // if all dependencies of this is schedule or this job is not dependent on any one 

                        DepAni = new Thread(this);
                        DepAni.start();

                        indicator = 1;
                        while (indicator == 1) {
                            Random randomGenerator = new Random();

                            int g1 = randomGenerator.nextInt(255);
                            int g2 = randomGenerator.nextInt(255);
                            int g3 = randomGenerator.nextInt(255);
                            int g4 = randomGenerator.nextInt(255);
                            int g5 = randomGenerator.nextInt(255);
                            int g6 = randomGenerator.nextInt(255);
                            
                            for (int g = 0; g < pvec.size(); g++) {
                                int temp = pvec.elementAt(g);
                                //  System.out.println(temp);
                                label[temp + 1][3].setOpaque(true);
                            label[temp+1][3].setBackground(new Color(g4,g5,g6));
                            label[temp+1][3].setBackground(new Color(g1,g2,g3));
                                //label[temp+1][3].setBackground(Color.red);
                                // label[temp+1][3].setBackground(Color.darkGray);
                            
                            }
                            this.pack();
                            Thread.sleep(50);
                        }
                        
                        if (flag) {
                        DepAni.stop();

                        visited[i] = true;
                        job[i].StartTime = maxp;// schedule this job
                        job[i].EndTime = maxp + job[i].CompleteTime; // assign start time and end time
                        job[i].ScheduleRank = sd++; // rank of schedule

                        label[i + 1][2].setText(Double.toString(job[i].StartTime)); //= new JLabel(Double.toString(job[i].StartTime));
                        label[i + 1][2].setBorder(border);

                        label[i + 1][3].setText(Double.toString(job[i].EndTime)); // = new JLabel(Double.toString(job[i].EndTime));
                        //label[i][0].setBackground(Color.yellow);
                        label[i + 1][3].setBorder(border);
                        //this.pack();

                        // this.pack();
                        waitForSpace();

                        for (int j = 0; j < pvec.size(); j++) {
                            int tmp = pvec.elementAt(j) + 1;
                            label[tmp][0].setBorder(border);
                            //label[j][3].setBorder(border2);
                            label[tmp][3].setOpaque(true);
                            label[tmp][3].setBackground(Color.green);
                            label[tmp][3].setBorder(border);

                        }

                        label[i + 1][0].setOpaque(true);
                        label[i + 1][0].setBorder(border);
                        label[i + 1][0].setBackground(Color.green);

                        label[i + 1][1].setOpaque(true);
                        label[i + 1][1].setBackground(Color.green);

                        label[i + 1][2].setOpaque(true);
                        label[i + 1][2].setBackground(Color.green);

                        label[i + 1][3].setOpaque(true);
                        label[i + 1][3].setBackground(Color.green);

                        this.pack();
                        break;
                    }
                        else {
                            for (int j = 0; j < pvec.size(); j++) {
                            int tmp = pvec.elementAt(j) + 1;
                            label[tmp][0].setBorder(border);
                            //label[j][3].setBorder(border2);
                            label[tmp][3].setOpaque(true);
                            label[tmp][3].setBackground(Color.pink);
                            label[tmp][3].setBorder(border);
                            label[i+1][0].setOpaque(true);
                            label[i+1][0].setBorder(border);
                            label[i+1][0].setBackground(Color.pink);
                        }
                            this.pack();
                        }

                }
            }
        }

    }

    // function for calculate critical path 
    public List<Integer> Calculate_Critical_Path(JobDetail job[]) throws InterruptedException {

        JOptionPane.showMessageDialog(this, "Find Critical path");

        List<Integer> critical_path = new ArrayList<Integer>(); // create a empty path list 
        boolean flag = true;

        double max_end = job[0].EndTime;
        int max_endid = 0;
        label[1][4].setText("false");
        for (int i = 1; i < size; i++) {
            label[i + 1][4].setText("false");
            if (job[i].EndTime > max_end) {

                max_endid = i;
                max_end = job[i].EndTime;

            }
        }

        critical_path.add(max_endid);
        job[max_endid].InCritical = true;

        label[max_endid + 1][4].setBorder(border5);

        waitForSpace();

        label[max_endid + 1][4].setText(Boolean.toString(job[max_endid].InCritical));
        label[max_endid + 1][4].setBackground(Color.red);
        label[max_endid + 1][4].setOpaque(true);

        while (true) {
            Iterator<Integer> it = job[max_endid].Dependencies.iterator();
            int nextid = -1;
            max_end = -1;
            waitForSpace();
            while (it.hasNext()) {
                nextid = it.next();
                if (max_end < job[nextid].EndTime) {
                    max_endid = nextid;
                    max_end = job[nextid].EndTime;
                }
            }
            if (nextid == -1) {
                break;
            } else {

                critical_path.add(max_endid);
                job[max_endid].InCritical = true;

                label[max_endid + 1][4].setOpaque(true);
                label[max_endid + 1][4].setBorder(border5);

                waitForSpace();

                label[max_endid + 1][4].setText(Boolean.toString(job[max_endid].InCritical));
                label[max_endid + 1][4].setBackground(Color.red);

            }

        }

        for (int i = 0; i < size; i++) {
            if (!job[i].InCritical) {
                label[i + 1][4].setOpaque(true);
                label[i + 1][4].setBackground(Color.magenta);
            }
        }
        this.pack();
        return critical_path;
    }

    public void Calculate_Slack_Time(JobDetail job[]) throws InterruptedException {
        int i, j;

        JOptionPane.showMessageDialog(this, "Calculate Slack time ");

        for (i = 1; i <= size; i++) {
            label[i][5].setText("0");
        }

        for (i = size - 1; i >= 0; i--) {
            if (job[i].InCritical) {
                break;
            }
        }
        double EndJob = job[i].EndTime;

        for (i = size - 1; i >= 0; i--) {

            if (job[i].InCritical) {
                continue;
            }
            label[i + 1][5].setOpaque(true);
            label[i + 1][5].setBorder(border5);
            double max_end = EndJob;
            waitForSpace();

            for (j = i + 1; j < size; j++) {
                Iterator<Integer> it = job[j].Dependencies.iterator();
                while (it.hasNext()) {
                    if (it.next() == i) {
                        max_end = (max_end > (job[j].StartTime + job[j].SlackTime)) ? (job[j].StartTime + job[j].SlackTime) : max_end;
                        break;
                    }
                }
            }

            job[i].SlackTime = max_end - (job[i].StartTime + job[i].CompleteTime);

            waitForSpace();

            label[i + 1][5].setText(Double.toString(job[i].SlackTime));
            label[i + 1][5].setBackground(Color.CYAN);
            this.pack();
            //label[i+1][5].setBorder(border5);
        }
    }

    @Override
    public void run() {

        //System.out.println("New thred ");
        try {
            waitForSpace();
        } catch (InterruptedException ex) {
            Logger.getLogger(Solver.class.getName()).log(Level.SEVERE, null, ex);
        }
        indicator = 0;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}