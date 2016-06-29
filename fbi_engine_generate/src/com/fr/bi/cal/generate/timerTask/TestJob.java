package com.fr.bi.cal.generate.timerTask;

import com.fr.third.org.quartz.Job;
import com.fr.third.org.quartz.JobExecutionContext;
import com.fr.third.org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * Created by Kary on 2016/6/29.
 */
public class TestJob implements Job {
    SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date d = new Date();
    String returnstr = DateFormat.format(d);

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        // TODO Auto-generated method stub
        System.out.println(returnstr+"★★★★★★★★★★★");
    }

}
