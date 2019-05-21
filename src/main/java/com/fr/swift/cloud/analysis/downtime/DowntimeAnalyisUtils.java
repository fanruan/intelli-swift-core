package com.fr.swift.cloud.analysis.downtime;

import au.com.bytecode.opencsv.CSVWriter;
import com.fr.swift.query.QueryRunnerProvider;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.result.SwiftResultSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2019/4/26
 *
 * @author Lucifer
 * @description
 */
// TODO: 2019/5/10 by lucifer gc demo待重构
public class DowntimeAnalyisUtils {

    private static long RECENT_TEN_MIN = 10 * 60 * 1000L;

    public static void test(String appId, String yearMonth) throws Exception {
        String memTable = "real_time_usage";
        String shutdownTable = "shutdown_record";
        String gcTable = "gc_record";

        FilterInfoBean filter = new AndFilterBean(
                Arrays.<FilterInfoBean>asList(
                        new InFilterBean("appId", appId),
                        new InFilterBean("yearMonth", yearMonth))
        );
        DetailQueryInfoBean bean1 = DetailQueryInfoBean.builder(memTable).setDimensions(new DimensionBean(DimensionType.DETAIL_ALL_COLUMN))
                .setFilter(filter).build();
        DetailQueryInfoBean bean2 = DetailQueryInfoBean.builder(shutdownTable).setDimensions(new DimensionBean(DimensionType.DETAIL_ALL_COLUMN))
                .setFilter(filter).build();
        DetailQueryInfoBean bean3 = DetailQueryInfoBean.builder(gcTable).setDimensions(new DimensionBean(DimensionType.DETAIL_ALL_COLUMN))
                .setFilter(filter).build();
        SwiftResultSet resultSet1 = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(bean1));
        SwiftResultSet resultSet2 = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(bean2));
        SwiftResultSet resultSet3 = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(bean3));

        List<DowntimeElement> analysisList = new ArrayList<DowntimeElement>();
        Map<Integer, ShutdownElement> shutdownMap = new HashMap<Integer, ShutdownElement>();
        while (resultSet1.hasNext()) {
            analysisList.add(new RealtimeUsageElement(resultSet1.getNextRow()));
        }
        while (resultSet2.hasNext()) {
            DowntimeElement downtimeElement = new ShutdownElement(resultSet2.getNextRow());
            shutdownMap.put(downtimeElement.pid(), (ShutdownElement) downtimeElement);
        }
        while (resultSet3.hasNext()) {
            analysisList.add(new GCElement(resultSet3.getNextRow()));
        }
        Collections.sort(analysisList, new Comparator<DowntimeElement>() {
            @Override
            public int compare(DowntimeElement o1, DowntimeElement o2) {
                return (int) (o1.recordTime() - o2.recordTime());
            }
        });

        int currentPid = -1;
        int pidStart = 0;
        int pidEnd = -1;
        List<int[]> pidIndexList = new ArrayList<int[]>();
        List<Integer> pidList = new ArrayList<Integer>();

        for (int i = 0; i < analysisList.size(); i++) {
            if (analysisList.get(i).pid() > 0) {
                if (currentPid != analysisList.get(i).pid()) {
                    if (currentPid == -1) {
                        currentPid = analysisList.get(i).pid();
                        continue;
                    }
                    pidEnd = i - 1;
                    if (pidEnd >= 0 && pidStart >= 0) {
                        pidList.add(currentPid);
                        pidIndexList.add(new int[]{pidStart, pidEnd});
                    }
                    currentPid = analysisList.get(i).pid();
                    pidStart = i;
                }
            }
        }
        pidList.add(currentPid);
        pidIndexList.add(new int[]{pidStart, analysisList.size() - 1});

        for (int i = 0; i < pidList.size(); i++) {
            int pid = pidList.get(i);
            int[] pidIndex = pidIndexList.get(0);
            if (shutdownMap.containsKey(pid)) {
                ShutdownElement shutdownElement = shutdownMap.get(pid);

                for (int j = pidIndex[0]; j <= pidIndex[1]; j++) {
                    DowntimeElement downtimeElement = analysisList.get(j);
                    if (downtimeElement.recordTime() <= shutdownElement.recordTime()) {
                        continue;
                    } else {
                        pidIndex[1] = j;
                    }
                }
            }
        }
        // TODO: 2019/4/28 by lucifer 移动pidindex end

        List<DowntimeResult> downtimeResults = new ArrayList<DowntimeResult>();
        for (int i = 0; i < pidList.size(); i++) {
            int pid = pidList.get(i);
            int[] pidIndex = pidIndexList.get(i);
            DowntimeElement startElement = analysisList.get(pidIndex[0]);
            DowntimeElement endElement = analysisList.get(pidIndex[1]);

            DowntimeElement lastEndElement = pidIndex[0] > 0 ? analysisList.get(pidIndex[0] - 1) : null;
            DowntimeElement nextStartElement = pidIndex[1] < analysisList.size() - 1 ? analysisList.get(pidIndex[1] + 1) : null;


            ShutdownElement shutdownElement = shutdownMap.get(pid);

            DowntimeResult downtimeResult = new DowntimeResult(pid
                    , shutdownElement != null ? shutdownElement.getSignalName() : ""
                    , shutdownElement != null ? shutdownElement.recordTime() : endElement.recordTime());

            List<Long> recentTimesMem = new ArrayList<Long>();
            List<Long> recentTimeMem = new ArrayList<Long>();

            List<Double> recentTimesCpu = new ArrayList<Double>();
            List<Double> recentTimeCpu = new ArrayList<Double>();

            List<Long> recentTimesMinorGC = new ArrayList<Long>();
            List<Long> recentTimeMinorGC = new ArrayList<Long>();

            List<Long> recentTimesMajorGC = new ArrayList<Long>();
            List<Long> recentTimeMajorGC = new ArrayList<Long>();

            for (int j = pidIndex[1]; j >= pidIndex[0]; j--) {
                DowntimeElement currentElement = analysisList.get(j);
                if (currentElement.type() == AbstractDowntimeElement.ElementType.REALTIME_USAGE) {
                    if (recentTimesMem.size() < 10) {
                        recentTimesMem.add(((RealtimeUsageElement) currentElement).getMemory());
                        recentTimesCpu.add(((RealtimeUsageElement) currentElement).getCpu());
                    }
                    if (endElement.recordTime() - currentElement.recordTime() <= RECENT_TEN_MIN) {
                        recentTimeMem.add(((RealtimeUsageElement) currentElement).getMemory());
                        recentTimeCpu.add(((RealtimeUsageElement) currentElement).getCpu());

                    }
                } else if (currentElement.type() == AbstractDowntimeElement.ElementType.GC) {
                    if (((GCElement) currentElement).getGcType().equals("minor GC")) {
                        if (recentTimesMinorGC.size() < 10) {
                            recentTimesMinorGC.add(currentElement.recordTime());
                        }
                        if (endElement.recordTime() - currentElement.recordTime() <= RECENT_TEN_MIN) {
                            recentTimeMinorGC.add(currentElement.recordTime());
                        }
                    } else if (((GCElement) currentElement).getGcType().equals("major GC")) {
                        if (recentTimesMajorGC.size() < 10) {
                            recentTimesMajorGC.add(currentElement.recordTime());
                        }
                        if (endElement.recordTime() - currentElement.recordTime() <= RECENT_TEN_MIN) {
                            recentTimeMajorGC.add(currentElement.recordTime());
                        }
                    }
                }
            }
            Collections.reverse(recentTimesMem);
            Collections.reverse(recentTimeMem);
            Collections.reverse(recentTimesCpu);
            Collections.reverse(recentTimeCpu);
            Collections.reverse(recentTimesMinorGC);
            Collections.reverse(recentTimeMinorGC);
            Collections.reverse(recentTimesMajorGC);
            Collections.reverse(recentTimeMajorGC);


            Long[] occurTimes = new Long[2];
            Long[] startTimes = new Long[2];
            if (shutdownElement == null) {
                occurTimes[0] = endElement.recordTime();
                if (nextStartElement == null) {
                    occurTimes[1] = Long.MAX_VALUE;
                } else {
                    occurTimes[1] = nextStartElement.recordTime();
                }
                if (lastEndElement == null) {
                    startTimes[0] = 0L;
                } else {
                    startTimes[0] = lastEndElement.recordTime();
                }
                startTimes[1] = startElement.recordTime();
            } else {
                long startTime = shutdownElement.getStartTime();
                long upTime = shutdownElement.getUpTime();
                startTimes[0] = startTime;
                startTimes[1] = startTime;
                if (upTime <= 0) {
                    occurTimes[0] = endElement.recordTime();
                    if (nextStartElement == null) {
                        occurTimes[1] = Long.MAX_VALUE;
                    } else {
                        occurTimes[1] = nextStartElement.recordTime();
                    }
                } else {
                    occurTimes[0] = startTime + upTime;
                    occurTimes[1] = startTime + upTime;
                }
            }
            downtimeResult.setStartTimes(startTimes);
            downtimeResult.setOccurTimes(occurTimes);

            downtimeResult.setRecentTimesMem(recentTimesMem.toArray(new Long[recentTimesMem.size()]));
            downtimeResult.setRecentTimeMem(recentTimeMem.toArray(new Long[recentTimeMem.size()]));

            downtimeResult.setRecentTimesCpu(recentTimesCpu.toArray(new Double[recentTimesCpu.size()]));
            downtimeResult.setRecentTimeCpu(recentTimeCpu.toArray(new Double[recentTimeCpu.size()]));

            downtimeResult.setRecentTimesMinorGC(recentTimesMinorGC.toArray(new Long[recentTimesMinorGC.size()]));
            downtimeResult.setRecentTimeMinorGC(recentTimeMinorGC.toArray(new Long[recentTimeMinorGC.size()]));

            downtimeResult.setRecentTimesMajorGC(recentTimesMajorGC.toArray(new Long[recentTimesMajorGC.size()]));
            downtimeResult.setRecentTimeMajorGC(recentTimeMajorGC.toArray(new Long[recentTimeMajorGC.size()]));

            downtimeResult.calcPredictDownType();
            downtimeResults.add(downtimeResult);
            System.out.println(downtimeResult);
        }
        //analysis
        File csv = new File("F:/writerTest.csv");
        if (csv.exists()) {
            csv.delete();
        }
        if (!csv.exists()) csv.createNewFile();
        List<String[]> list = new ArrayList<String[]>();
        for (int i = 0; i < downtimeResults.size(); i++) {
            DowntimeResult downtimeResult = downtimeResults.get(i);
            String[] ss = {String.valueOf(downtimeResult.getPid()), DowntimeResult.toDateString(downtimeResult.getRecordTime())
                    , DowntimeResult.toDateString(downtimeResult.getStartTimes()), DowntimeResult.toDateString(downtimeResult.getOccurTimes())
//                    , Arrays.toString(downtimeResult.getRecentTenMems()), Arrays.toString(downtimeResult.getRecentTenCpus())
//                    , DowntimeResult.toDateString(downtimeResult.getRecentTenMinorGC()), DowntimeResult.toDateString(downtimeResult.getRecentTenMajorGC())
                    , Arrays.toString(downtimeResult.getRecentTenMinsMem()), Arrays.toString(downtimeResult.getRecentTenMinsCpu())
//                    , DowntimeResult.toDateString(downtimeResult.getRecentTenMinsMinorGC())
                    , DowntimeResult.toDateString(downtimeResult.getRecentTenMinsMajorGC())
                    , downtimeResult.getRecordDownType(), downtimeResult.getPredictDownType()};
            list.add(ss);
        }

        CSVWriter writer = new CSVWriter(new OutputStreamWriter(new FileOutputStream(csv), "GBK"), CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER);
        writer.writeNext(new String[]{"pid", "recordTime", "startTimes", "shutdownTimes"
//                , "recent_ten_mem", "recent_ten_cpu", "recent_ten_minor_gc", "recent_ten_major_gc"
                , "recent_ten_mins_mem", "recent_ten_mins_cpu"
//                , "recent_ten_mins_minor_gc"
                , "recent_ten_mins_major_gc", "recordDownType", "predictDownType"});
        for (String[] strings : list) {
            writer.writeNext(strings);
        }
//        writer.writeAll(list);
        writer.flush();
        writer.close();
        return;
    }
}
