package com.fr.swift.cloud.analysis.downtime;

import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * This class created on 2019/4/28
 *
 * @author Lucifer
 * @description
 */
// TODO: 2019/5/10 by lucifer gc相关待完事
public class DowntimeResult {

    private int pid;
    private long recordTime;
    private Long[] startTimes;
    private Long[] occurTimes;

    //最近10次内存统计
    private Long[] recentTenMems;
    //最近10次CPU统计
    private Double[] recentTenCpus;
    //最近10次minor GC
    private Long[] recentTenMinorGC;
    //最近10次major GC
    private Long[] recentTenMajorGC;

    //最近10分钟内存统计
    private Long[] recentTenMinsMem;
    //最近10分钟CPU统计
    private Double[] recentTenMinsCpu;
    //最近10分钟内minor GC次数
    private Long[] recentTenMinsMinorGC;
    //最近10分钟内major GC次数
    private Long[] recentTenMinsMajorGC;

    //记录的宕机类型
    private String recordDownType;
    //预测的宕机类型
    private String predictDownType;

    public DowntimeResult(int pid, String recordDownType, long recordTime) {
        this.pid = pid;
        this.recordTime = recordTime;
        this.recordDownType = recordDownType;
    }

    public void setStartTimes(Long[] startTimes) {
        this.startTimes = startTimes;
    }

    public DowntimeResult setOccurTimes(Long[] occurTimes) {
        this.occurTimes = occurTimes;
        return this;
    }

    public DowntimeResult setRecentTimesMem(Long[] recentTenMems) {
        this.recentTenMems = recentTenMems;
        return this;
    }

    public DowntimeResult setRecentTimesCpu(Double[] recentTenCpus) {
        this.recentTenCpus = recentTenCpus;
        return this;
    }

    public DowntimeResult setRecentTimesMinorGC(Long[] recentTenMinorGC) {
        this.recentTenMinorGC = recentTenMinorGC;
        return this;
    }

    public DowntimeResult setRecentTimesMajorGC(Long[] recentTenMajorGC) {
        this.recentTenMajorGC = recentTenMajorGC;
        return this;
    }

    public DowntimeResult setRecentTimeMem(Long[] recentTenMinsMem) {
        this.recentTenMinsMem = recentTenMinsMem;
        return this;
    }

    public DowntimeResult setRecentTimeCpu(Double[] recentTenMinsCpu) {
        this.recentTenMinsCpu = recentTenMinsCpu;
        return this;
    }

    public DowntimeResult setRecentTimeMinorGC(Long[] recentTenMinsMinorGC) {
        this.recentTenMinsMinorGC = recentTenMinsMinorGC;
        return this;
    }

    public DowntimeResult setRecentTimeMajorGC(Long[] recentTenMinsMajorGC) {
        this.recentTenMinsMajorGC = recentTenMinsMajorGC;
        return this;
    }

    public int getPid() {
        return pid;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public Long[] getStartTimes() {
        return startTimes;
    }

    public Long[] getOccurTimes() {
        return occurTimes;
    }

    public Long[] getRecentTenMems() {
        return recentTenMems;
    }

    public Double[] getRecentTenCpus() {
        return recentTenCpus;
    }

    public Long[] getRecentTenMinorGC() {
        return recentTenMinorGC;
    }

    public Long[] getRecentTenMajorGC() {
        return recentTenMajorGC;
    }

    public Long[] getRecentTenMinsMem() {
        return recentTenMinsMem;
    }

    public Double[] getRecentTenMinsCpu() {
        return recentTenMinsCpu;
    }

    public Long[] getRecentTenMinsMinorGC() {
        return recentTenMinsMinorGC;
    }

    public Long[] getRecentTenMinsMajorGC() {
        return recentTenMinsMajorGC;
    }

    public String getRecordDownType() {
        return recordDownType;
    }

    public String getPredictDownType() {
        return predictDownType;
    }

    public void calcPredictDownType() {
        if (oomKiller()) {
            setPredictDownType(SignalName.OOM.name());
        } else if (cpuKiller()) {
            setPredictDownType(SignalName.XCPU.name());
        } else {
            setPredictDownType(SignalName.TERM.name());
        }
    }

    /**
     * 临时策略：
     * 取当前进程最后一条记录，往前推找最近10分钟内内存和major gc情况
     * 满足几个条件判断为OOM：
     * 1、major gc次数大于等于20
     * 2、估算近10min内内存大小的线性关系，y=ax+b，a为斜率；计算内存平均值为c；计算平均波动volatility=a/c，满足volatility>=-0.1
     * （如果计算平均波动volatility<-0.1,则认为major gc效果显著，内存下降了，否则认为频繁major gc后内存下降不明显或依然在上升）
     * 二者均满足则预测为OOM。
     * 记录越少误差越大越不准确
     *
     * @return
     */
    private boolean oomKiller() {

        int[] times = new int[recentTenMinsMem.length];
        double[] memMbs = new double[recentTenMinsMem.length];
        double totalMemMbs = 0;
        for (int i = 0; i < recentTenMinsMem.length; i++) {
            times[i] = i + 1;
            memMbs[i] = recentTenMinsMem[i] / 1024 / 1024;
            totalMemMbs += memMbs[i];
        }
        double memSlope = calcMemSlope(times.length, times, memMbs);
        double volatility = 0;
        if (!Double.isNaN(memSlope)) {
            volatility = memSlope / (totalMemMbs / times.length);
        }
        if (this.recentTenMinsMajorGC.length < 20) {
            //todo GC次数很少且内存持续很低
            //todo GC次数很少但是内存持续很高
            return false;
        }
        if (this.recentTenMinsMajorGC.length >= 20) {
            if (volatility < -0.2d) {
                //GC次数频繁但内存持续下降
                return false;
            } else {
                //GC次数频繁但内存波动不大
                return true;
            }
        }
        return true;
    }

    public double calcMemSlope(int n, int x[], double y[]) {
        int sumxx = 0, sumx = 0, sumxy = 0, sumy = 0;
        for (int i = 0; i < n; i++) {
            sumxx += x[i] * x[i]; //x平方求和
            sumx += x[i];        //x求和
            sumxy += x[i] * y[i];  //xy求和
            sumy += y[i];        //y求和
        }

        int fm = n * sumxx - sumx * sumx;
        int fz1 = n * sumxy - sumx * sumy;
        int fz2 = sumy * sumxx - sumx * sumxy;
        System.out.println(String.format("%.3f", 1.0 * fz1 / fm));
        System.out.println(String.format("%.3f", 1.0 * fz2 / fm));
        return 1.0 * fz1 / fm;
    }

    private boolean cpuKiller() {
        if (this.recentTenCpus.length == 0) {
            return false;
        }
        //判断不为OOM，且CPU持续很高
        int overCpuTimes = 0;
        for (Double cpuPercent : this.recentTenCpus) {
            if (cpuPercent > 0.95d) {
                overCpuTimes++;
            }
        }
        if ((overCpuTimes / this.recentTenCpus.length) > 0.8d) {
            return true;
        }
        return false;
    }

    public DowntimeResult setPredictDownType(String predictDownType) {
        this.predictDownType = predictDownType;
        return this;
    }

    @Override
    public String toString() {
        return "DowntimeResult{" +
                "pid=" + pid +
                ", recordTime=" + toDateString(recordTime) +
                ", startTimes=" + toDateString(startTimes) +
                ", occurTimes=" + toDateString(occurTimes) +
                ", recentTimesMem=" + Arrays.toString(recentTenMems) +
                ", recentTimesCpu=" + Arrays.toString(recentTenCpus) +
                ", recentTimesMinorGC=" + toDateString(recentTenMinorGC) +
                ", recentTimesMajorGC=" + toDateString(recentTenMajorGC) +
                ", recentTimeMem=" + Arrays.toString(recentTenMinsMem) +
                ", recentTimeCpu=" + Arrays.toString(recentTenMinsCpu) +
                ", recentTimeMinorGC=" + toDateString(recentTenMinsMinorGC) +
                ", recentTimeMajorGC=" + toDateString(recentTenMinsMajorGC) +
                ", recordDownType='" + recordDownType + '\'' +
                ", predictDownType='" + predictDownType + '\'' +
                '}';
    }

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static String toDateString(Long... timeStamps) {
        if (timeStamps == null)
            return "null";

        int iMax = timeStamps.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(format.format(timeStamps[i]));
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    /**
     * 暂时swift只判断和输出TERM、CPU、OOM这三种
     */
    private enum SignalName {
        //挂断信号，例如XShell超时导致Tomcat停止
        HUP,
        //外部中断，例如CTRL + C停止Tomcat
        INT,
        //异常终止
        ABRT,
        //用户终止
        TERM,
        //进程停止，从终端读数据时
        TTIN,
        //进程停止，从终端写数据时
        TTIO,
        //CPU超时
        XCPU,
        //虚拟时钟超时
        VTALRM,
        //profile时钟超时
        PROF,
        //（自定义信号量）报表服务器内部执行System.exit(0)方法
        EXIT,
        //（自定义信号量）进程被OOM Killer杀死
        OOM;
    }
}
