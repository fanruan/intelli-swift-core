package com.fr.swift.cloud.analysis.downtime;

import com.fr.swift.cloud.result.ArchiveDBManager;
import com.fr.swift.cloud.result.table.downtime.DowntimeExecutionResult;
import com.fr.swift.cloud.result.table.downtime.DowntimeResult;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.QueryRunnerProvider;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2019/5/23
 *
 * @author Lucifer
 * @description
 */
public class DowntimeAnalyser {

    private static final long ANALYSE_PERIOD = 10 * 60 * 1000L;
    private static final long FULL_GC_TIME = 3 * 10 * 1000L;
    private static final double PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final double CPU_OVERLOAD_RATE = Math.min(PROCESSORS, 8 + (PROCESSORS - 8) * 5 / 8) / PROCESSORS;
    private static final int BATCH_SIZE = 20;

    private static final String realTimeUsage = "real_time_usage";
    private static final String shutdownTable = "shutdown_record";
    private static final String gcTable = "gc_record";
    private static final String execution = "execution";
    private static final String executionSql = "execution_sql";

    public List<DowntimeResult> downtimeAnalyse(String appId, String yearMonth) throws Exception {

        SwiftLoggers.getLogger().info("Start downtime analysis task with appId: {}, yearMonth: {}", appId, yearMonth);
        FilterInfoBean filter = new AndFilterBean(
                Arrays.asList(
                        new InFilterBean("appId", appId),
                        new InFilterBean("yearMonth", yearMonth))
        );
        DetailQueryInfoBean bean1 = DetailQueryInfoBean.builder(realTimeUsage).setDimensions(RealtimeUsageElement.getDimensions()).setFilter(filter).build();
        DetailQueryInfoBean bean2 = DetailQueryInfoBean.builder(shutdownTable).setDimensions(ShutdownElement.getDimensions()).setFilter(filter).build();
        DetailQueryInfoBean bean3 = DetailQueryInfoBean.builder(gcTable).setDimensions(GCElement.getDimensions()).setFilter(filter).build();
        SwiftResultSet resultSet1 = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(bean1));
        SwiftResultSet resultSet2 = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(bean2));
        SwiftResultSet resultSet3 = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(bean3));

        List<DowntimeElement> analysisList = new ArrayList<DowntimeElement>();
        while (resultSet1.hasNext()) {
            Row row = resultSet1.getNextRow();
            DowntimeElement element = new RealtimeUsageElement(row);
            analysisList.add(element);
        }
        Map<Integer, ShutdownElement> shutdownMap = new HashMap<>();
        while (resultSet2.hasNext()) {
            Row row = resultSet2.getNextRow();
            ShutdownElement element = new ShutdownElement(row);
            shutdownMap.put(element.pid(), element);
        }
        while (resultSet3.hasNext()) {
            Row row = resultSet3.getNextRow();
            DowntimeElement element = new GCElement(row);
            analysisList.add(element);
        }
        Collections.sort(analysisList, new Comparator<DowntimeElement>() {
            @Override
            public int compare(DowntimeElement o1, DowntimeElement o2) {
                return Long.compare(o1.recordTime(), o2.recordTime());
            }
        });
        // TODO: 2019/5/24 by lucifer 不同进程  pid相同的情况
        Map<Integer, List<DowntimeElement>> pidElementsMap = new LinkedHashMap<>();
        for (DowntimeElement downtimeElement : analysisList) {
            if (!pidElementsMap.containsKey(downtimeElement.pid())) {
                pidElementsMap.put(downtimeElement.pid(), new ArrayList<>());
            }
            pidElementsMap.get(downtimeElement.pid()).add(downtimeElement);
        }
        List<DowntimeResult> downtimeResultList = new ArrayList<>();
        for (Map.Entry<Integer, List<DowntimeElement>> entry : pidElementsMap.entrySet()) {
            int pid = entry.getKey();
            List<DowntimeElement> downtimeElementList = entry.getValue();
            long lastTime = downtimeElementList.get(downtimeElementList.size() - 1).recordTime();
            removeOverTimeRecord(lastTime, downtimeElementList);

            DowntimeResult downtimeResult = new DowntimeResult(pid, lastTime, appId, yearMonth);
            analyseDowntimeType(downtimeElementList, downtimeResult, shutdownMap);

            List<DowntimeExecutionResult> downtimeExecutionResultList = analyseExecutionInfo(downtimeResult, filter, downtimeElementList);
            downtimeResultList.add(downtimeResult);

            Session session = ArchiveDBManager.INSTANCE.getFactory().openSession();
            Transaction transaction = session.beginTransaction();
            int count = 1;
            try {
                session.save(downtimeResult);
                for (DowntimeExecutionResult downtimeExecutionResult : downtimeExecutionResultList) {
                    session.save(downtimeExecutionResult);
                    if (count % BATCH_SIZE == 0) {
                        session.flush();
                        session.clear();
                    }
                    count++;
                }
            } finally {
                transaction.commit();
                try {
                    session.close();
                } catch (Exception ignored) {
                }
            }
        }
        SwiftLoggers.getLogger().info("finished downtime analysis task with appId: {}, yearMonth: {}", appId, yearMonth);
        return downtimeResultList;
    }

    /**
     * 移除超过10分钟的记录
     *
     * @param lastTime
     * @param downtimeElementList
     */
    private void removeOverTimeRecord(long lastTime, List<DowntimeElement> downtimeElementList) {
        Iterator<DowntimeElement> iterator = downtimeElementList.iterator();
        while (iterator.hasNext()) {
            DowntimeElement downtimeElement = iterator.next();
            //移除超过10分钟的记录
            if ((lastTime - downtimeElement.recordTime()) > ANALYSE_PERIOD) {
                iterator.remove();
            } else {
                break;
            }
        }
    }

    /**
     * 分析宕机类型
     *
     * @param downtimeElementList
     */
    private void analyseDowntimeType(List<DowntimeElement> downtimeElementList, DowntimeResult downtimeResult, Map<Integer, ShutdownElement> shutdownMap) {
        long totalFullGcTime = 0;
        int totalCpuTimes = 0;
        int overloadCpuTimes = 0;
        for (DowntimeElement downtimeElement : downtimeElementList) {
            if (downtimeElement.type() == AbstractDowntimeElement.ElementType.GC) {
                if (((GCElement) downtimeElement).getGcType() != GCElement.GcType.MINOR_GC) {
                    totalFullGcTime += ((GCElement) downtimeElement).duration();
                }
            } else if (downtimeElement.type() == AbstractDowntimeElement.ElementType.REALTIME_USAGE) {
                totalCpuTimes++;
                if (((RealtimeUsageElement) downtimeElement).cpu() >= CPU_OVERLOAD_RATE) {
                    overloadCpuTimes++;
                }

            }
            if (downtimeResult.getNode() == null) {
                downtimeResult.setNode(downtimeElement.node());
            }

        }
        downtimeResult.setFullGcTime(totalFullGcTime);
        if (totalFullGcTime >= FULL_GC_TIME) {
            downtimeResult.setPredictDownType(DowntimeResult.SignalName.OOM.name());
        } else if (((double) overloadCpuTimes / totalCpuTimes) >= 0.8) {
            downtimeResult.setPredictDownType(DowntimeResult.SignalName.XCPU.name());
        } else {
            if (oomKiller(downtimeElementList)) {
                downtimeResult.setPredictDownType(DowntimeResult.SignalName.OOM.name());
            } else {
                downtimeResult.setPredictDownType(DowntimeResult.SignalName.TERM.name());
            }
        }
        if (shutdownMap.containsKey(downtimeResult.getPid())) {
            downtimeResult.setRecordDownType(shutdownMap.get(downtimeResult.getPid()).getSignalName());
        }
    }

    /**
     * @param downtimeElementList
     * @return
     * @description gc次数和内存波动
     */
    private boolean oomKiller(List<DowntimeElement> downtimeElementList) {
        int gcTimes = 0;
        List<Long> mems = new ArrayList<>();
        for (DowntimeElement downtimeElement : downtimeElementList) {
            if (downtimeElement.type() == AbstractDowntimeElement.ElementType.GC) {
                gcTimes++;
            } else if (downtimeElement.type() == AbstractDowntimeElement.ElementType.REALTIME_USAGE) {
                mems.add(((RealtimeUsageElement) downtimeElement).memory());
            }
        }
        int[] times = new int[mems.size()];
        double[] memMbs = new double[mems.size()];
        double totalMemMbs = 0;
        for (int i = 0; i < mems.size(); i++) {
            times[i] = i + 1;
            memMbs[i] = mems.get(i) / 1024 / 1024;
            totalMemMbs += memMbs[i];
        }
        double memSlope = calcMemSlope(times.length, times, memMbs);
        double volatility = 0;
        if (!Double.isNaN(memSlope)) {
            volatility = memSlope / (totalMemMbs / times.length);
        }
        if (gcTimes < 10) {
            //todo GC次数很少且内存持续很低
            //todo GC次数很少但是内存持续很高
            return false;
        }
        if (gcTimes > 20) {
            //GC次数频繁但内存持续下降
            //GC次数频繁但内存波动不大
            return !(volatility < -0.2d);
        }
        return true;
    }

    public double calcMemSlope(int n, int[] x, double[] y) {
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
        return 1.0 * fz1 / fm;
    }

    /**
     * @param downtimeResult
     * @param baseFilter
     * @param downtimeElementList
     * @return
     * @throws Exception
     * @description 宕机时间点前模版访问情况
     */
    private List<DowntimeExecutionResult> analyseExecutionInfo(DowntimeResult downtimeResult, FilterInfoBean baseFilter, List<DowntimeElement> downtimeElementList) throws Exception {
        if (downtimeElementList.size() < 2) {
            return Collections.EMPTY_LIST;
        }
        long startTime = downtimeElementList.get(0).recordTime();
        long endTime = downtimeElementList.get(downtimeElementList.size() - 1).recordTime();
        if ((endTime - startTime) > ANALYSE_PERIOD) {
            startTime = endTime - ANALYSE_PERIOD;
        }

        NumberInRangeFilterBean executionFilterBean = NumberInRangeFilterBean.builder("time").setStart(String.valueOf(startTime), true).setEnd(String.valueOf(endTime), true).build();

        FilterInfoBean executionFilter = new AndFilterBean(
                Arrays.asList(
                        baseFilter,
                        executionFilterBean));
        DetailQueryInfoBean executionBean = DetailQueryInfoBean.builder(execution).setDimensions(DowntimeExecutionResult.getDimensions()).setFilter(executionFilter).build();
        SwiftResultSet executionResultSet = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(executionBean));
        List<DowntimeExecutionResult> rowList = new ArrayList<>();
        while (executionResultSet.hasNext()) {
            Row executionRow = executionResultSet.getNextRow();
            DowntimeExecutionResult downtimeExecutionResult = new DowntimeExecutionResult(executionRow, downtimeResult.getId(), downtimeResult.getAppId(), downtimeResult.getYearMonth());

            FilterInfoBean executionSqlFilter = new AndFilterBean(
                    Arrays.asList(
                            baseFilter,
                            new InFilterBean("executionId", downtimeExecutionResult.getId())));
            DetailQueryInfoBean bean = DetailQueryInfoBean.builder(executionSql).setDimensions(ExecutionSqlData.getDimensions()).setFilter(executionSqlFilter).build();
            SwiftResultSet resultSet = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(bean));
            long data = 0;
            while (resultSet.hasNext()) {
                Row row = resultSet.getNextRow();
                ExecutionSqlData executionSqlData = new ExecutionSqlData(row);
                data += executionSqlData.getDatas();
            }
            downtimeExecutionResult.setData(data);
            rowList.add(downtimeExecutionResult);
        }
        return rowList;
    }
}