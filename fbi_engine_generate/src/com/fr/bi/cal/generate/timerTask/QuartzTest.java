package com.fr.bi.cal.generate.timerTask;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kary on 2016/6/29.
 */

public class QuartzTest {

    /** *//**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date d = new Date();
        String returnstr = DateFormat.format(d);

        TestJob job = new TestJob();
        String job_name ="11";
        try {
            System.out.println(returnstr+ "【系统启动】");
            QuartzManager.addJob(job_name,job,"0/2 * * * * ?"); //每2秒钟执行一次

            Thread.sleep(10000);
            System.out.println("【修改时间】");
            QuartzManager.modifyJobTime(job_name,"0/10 * * * * ?");
            Thread.sleep(20000);
            System.out.println("【移除定时】");
            QuartzManager.removeJob(job_name);
            Thread.sleep(10000);

            System.out.println("/n【添加定时任务】");
            QuartzManager.addJob(job_name,job,"0/5 * * * * ?");

        }  catch (Exception e) {
            e.printStackTrace();
        }
    }
}
