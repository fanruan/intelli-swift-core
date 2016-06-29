package com.fr.bi.cal.generate.timerTask.quartz;

import junit.framework.TestCase;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wuk on 16/6/30.
 */
public class QuartzManagerTest extends TestCase{
    public void testBasic(){
        // TODO Auto-generated method stub
        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date d = new Date();
        String returnstr = DateFormat.format(d);

        TestJob job = new TestJob();
        String job_name ="11";
        try {
            assertTrue(QuartzManager.getSf().getAllSchedulers().size()==0);
            System.out.println(returnstr+ "【系统启动】");
            System.out.println("/n【添加定时任务】");
            QuartzManager.addJob(job_name,job,"0/2 * * * * ?"); //每2秒钟执行一次
assertTrue(QuartzManager.getSf().getAllSchedulers().size()==1);
            Thread.sleep(10000);
            System.out.println("【修改时间】");
            QuartzManager.modifyJobTime(job_name,"0/10 * * * * ?");
            Thread.sleep(20000);
            assertTrue(QuartzManager.getSf().getAllSchedulers().size()==1);
            System.out.println("【移除定时】");
            QuartzManager.removeJob(job_name);
            assertTrue(QuartzManager.getSf().getAllSchedulers().size()==1);
            Thread.sleep(10000);

            System.out.println("/n【添加定时任务】");

        }  catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void testAddJob() throws Exception {
    }
    
    @Test
    public void testModifyJobTime() throws Exception {

    }
    
    @Test
    public void testRemoveJob() throws Exception {

    }

}
