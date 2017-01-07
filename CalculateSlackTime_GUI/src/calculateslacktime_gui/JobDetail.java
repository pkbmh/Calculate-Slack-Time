/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculateslacktime_gui;

import java.util.*;

/**
 *
 * @author pankajbirat
 */
class JobDetail implements Comparable<JobDetail> {

    String name;
    double StartTime;
    double EndTime;
    double CompleteTime;
    boolean InCritical;
    double SlackTime;
    int ScheduleRank;

    List<Integer> Dependencies;

    JobDetail() {
        Dependencies = new ArrayList<Integer>();
        InCritical = false;
        SlackTime = 0;
    }

    @Override
    public int compareTo(JobDetail t) {
        if (this.ScheduleRank == t.ScheduleRank) {
            return 0;
        }
        if (this.ScheduleRank < t.ScheduleRank) {
            return -1;
        }

        return 1;
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}