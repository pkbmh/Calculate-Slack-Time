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


public class CalculateSlackTime_GUI {

    /**
     * @param args the command line arguments
     */
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        // System.out.println("Enter number of jobs");
        File fp = new File("input.txt");

        Scanner scan = new Scanner(fp);

        int TotalJob = Integer.parseInt(scan.nextLine());

        JobDetail[] job = new JobDetail[TotalJob];

        for (int i = 0; i < TotalJob; i++) {
            job[i] = new JobDetail();
            String line = scan.nextLine();
            StringTokenizer st = new StringTokenizer(line);

            job[i].name = st.nextToken();

            //System.out.print("Enter job Completion time :: ");
            job[i].CompleteTime = Double.parseDouble(st.nextToken());
            //System.out.println("Enter Dependencies (-1 exit)");
            //scan.nextLine();

            int tmp = Integer.parseInt(st.nextToken());

            while (tmp != -1) {
                job[i].Dependencies.add(tmp);

                tmp = Integer.parseInt(st.nextToken());
            }
        }

        Solver solve = new Solver(TotalJob);
        solve.Calculate_Start_End_Time(job);
        List<Integer> critical_path = solve.Calculate_Critical_Path(job);

        Arrays.sort(job);

        solve.Calculate_Slack_Time(job);

        /* System.out.println("--------------------------------------------------------------------------------------------");
         System.out.print("job name  :  Brust time :  start time :  end time :   slack :  Schedule rank :  InCritical path :   depend on  :: ");

         for (int i = 0; i < TotalJob; i++) {
         System.out.print(i + " : " + job[i].name + " : " + job[i].CompleteTime + " : " + job[i].StartTime + ": " + job[i].EndTime + ": " + job[i].SlackTime + ":" + job[i].ScheduleRank + ": " + job[i].InCritical + " :: ");
         Iterator<Integer> DependenciesIterator = job[i].Dependencies.iterator();
         while (DependenciesIterator.hasNext()) {

         System.out.print(" " + DependenciesIterator.next());
         }

         System.out.println("");
         }
         System.out.println("----------------------------------------------------------------------------------------------------");

         /* print critical path
         Iterator<Integer> it = critical_path.iterator();
         
         while(it.hasNext()) {
         System.out.println("job id = " + it.next());
         }
         */
    }

}