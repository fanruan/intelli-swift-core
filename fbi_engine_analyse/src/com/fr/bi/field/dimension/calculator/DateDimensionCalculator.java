package com.fr.bi.field.dimension.calculator;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.io.newio.NIOConstant;
import com.fr.bi.stable.operation.sort.comp.ComparatorFacotry;
import com.fr.bi.stable.structure.collection.map.CubeTreeMap;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.bi.stable.utils.DateUtils;
import com.fr.bi.stable.utils.time.BIDateUtils;
import com.fr.general.GeneralUtils;
import com.fr.stable.StringUtils;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by 小灰灰 on 2015/6/30.
 */
public class DateDimensionCalculator extends AbstractDimensionCalculator {

    private static final long serialVersionUID = -1201531041684245593L;

    /**
     * 是否需要时间补全
     */
    private boolean isNeedComplete = false;

    /**
     * 维度最小时间
     */
    private Object minTime;

    /**
     * 维度最大时间
     */
    private Object maxTime;

    public DateDimensionCalculator(BIDimension dimension, BusinessField column, List<BITableSourceRelation> relations) {

        super(dimension, column, relations);
    }

    public DateDimensionCalculator(BIDimension dimension, BusinessField field, List<BITableSourceRelation> relations, List<BITableSourceRelation> directToDimensionRelations) {

        super(dimension, field, relations, directToDimensionRelations);
    }

    @Override
    public Iterator createValueMapIterator(BusinessTable table, ICubeDataLoader loader, boolean useRealData, int groupLimit, GroupValueIndex filterGvi) {

        ICubeColumnIndexReader getter = loader.getTableIndex(field.getTableBelongTo().getTableSource()).loadGroup(dimension.createKey(field), getRelationList(), useRealData, groupLimit);
        if (!useRealData) {
            applyFilterForNotRealData(getter, filterGvi);
        }
        Comparator comparator;
        //        if(getGroupDate() == BIReportConstant.GROUP.M){
        //            comparator = ComparatorFacotry.getComparator(BIReportConstant.SORT.NUMBER_ASC);
        //        }else{
        //            comparator = getComparator();
        //        }
        comparator = ComparatorFacotry.getComparator(BIReportConstant.SORT.NUMBER_ASC);
        CubeTreeMap treeMap = new CubeTreeMap(comparator);
        Iterator it = getter.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            treeMap.put(entry.getKey(), entry.getValue());
        }
        return getSortType() != BIReportConstant.SORT.NUMBER_DESC ? treeMap.iterator() : treeMap.previousIterator();
    }

    public int getGroupDate() {

        return getGroup().getType();
    }

    @Override
    public Comparator getComparator() {

        if (getSortType() == BIReportConstant.SORT.NUMBER_DESC) {
            return BIBaseConstant.COMPARATOR.COMPARABLE.DESC;
        } else {
            return BIBaseConstant.COMPARATOR.COMPARABLE.ASC;
        }
    }


    @Override
    public Object convertToOriginValue(String stringValue) {
        // 前端传过来的数据进行转换为后台的原始底层数据，主要是空值的处理
        // 处理的真是够xx的，只有一个YMD是long类型的
        // TODO 现在新增的分组中似乎不仅仅是YMD是long类型的,所以这边对于时间类型的各种分组仍然需要进一步的进行测试??
        if (StringUtils.isEmpty(stringValue)) {
            if (DateUtils.isCubeLongTimeGroup(getGroup().getType())) {
                return NIOConstant.LONG.NULL_VALUE;
            }
            return NIOConstant.INTEGER.NULL_VALUE;
        }
        if (DateUtils.isCubeLongTimeGroup(getGroup().getType())) {
            return Long.parseLong(stringValue);
        }
        return Integer.parseInt(stringValue);
    }

    /**
     * 初始化时间补全参数,主要是最大值和最小值
     *
     * @param table
     * @param loader
     */
    public void intCompleteTimeParameter(BusinessTable table, ICubeDataLoader loader) {

        ICubeTableService dateTableService = loader.getTableIndex(this.getField().getTableBelongTo().getTableSource());
        String fieldName = getField().getFieldName();
        ICubeColumnIndexReader yearMap = dateTableService.loadGroup(new IndexTypeKey(fieldName, BIReportConstant.GROUP.Y), getRelationList());
        ICubeColumnIndexReader monthMap = dateTableService.loadGroup(new IndexTypeKey(fieldName, BIReportConstant.GROUP.M), getRelationList());
        ICubeColumnIndexReader dayMapMap = dateTableService.loadGroup(new IndexTypeKey(fieldName, BIReportConstant.GROUP.YMD), getRelationList());
        int groupType = getGroup().getType();
        Integer fy = (Integer) BIDateUtils.firstDate(yearMap);
        Integer ly = (Integer) BIDateUtils.lastDate(yearMap);
        if (groupType == BIReportConstant.GROUP.Y) { // 年
            minTime = fy;
            maxTime = ly;
        } else if (groupType == BIReportConstant.GROUP.S) { // 季度
            // 季度,如果年份跨度超过一年,需要补全1~4季度
            if (fy != null && !fy.equals(ly)) {
                if (fy.intValue() < ly.intValue()) {
                    minTime = 1;
                    maxTime = 4;
                }
            } else {
                initFirstNeedJudge(groupType, dateTableService, fieldName);
            }
        } else if (groupType == BIReportConstant.GROUP.M) { // 月
            // 月份,如果年份跨度超过一年,需要补全1~12月份
            if (fy != null && !fy.equals(ly)) {
                if (fy.intValue() < ly.intValue()) {
                    minTime = 1;
                    maxTime = 12;
                }
            } else {
                initFirstNeedJudge(groupType, dateTableService, fieldName);
            }
        } else if (groupType == BIReportConstant.GROUP.W) { // 星期
            // 星期,如果年月日时间跨度大于7需要补全周一到周日
            Long f = (Long) BIDateUtils.firstDate(dayMapMap);
            Long l = (Long) BIDateUtils.lastDate(dayMapMap);
            int d = DateUtils.differentDaysByMillisecond(l, f);
            if (d >= 6) {
                minTime = 1;
                maxTime = 7;
            } else {
                initFirstNeedJudge(groupType, dateTableService, fieldName);
            }
        } else if (groupType == BIReportConstant.GROUP.D) { // 天
            // 一个月中的第几天
            Integer fm = (Integer) BIDateUtils.firstDate(monthMap);
            Integer lm = (Integer) BIDateUtils.lastDate(monthMap);
            // 如果跨度大于或月份之间跨度大于1则补全1~31天(TODO 有没有可能一个月..)
            if ((fy != null && !fy.equals(ly)) || (fm != null && !lm.equals(ly))) {
                minTime = 1;
                maxTime = 31;
            } else {
                initFirstNeedJudge(groupType, dateTableService, fieldName);
            }
        } else if (groupType == BIReportConstant.GROUP.WEEK_COUNT) {   // 周数
            // 周数,如果年份跨度超过一年则直接赋值为52周
            if (fy != null && !fy.equals(ly)) {
                if (fy.intValue() < ly.intValue()) {
                    minTime = 1;
                    maxTime = 52;
                }
            } else {
                initFirstNeedJudge(groupType, dateTableService, fieldName);
            }
        } else if (groupType == BIReportConstant.GROUP.HOUR) {  // 时
            // 如果年月日日期超过一天,则需补全1~24小时
            Long f = (Long) BIDateUtils.firstDate(dayMapMap);
            Long l = (Long) BIDateUtils.lastDate(dayMapMap);
            int d = DateUtils.differentDaysByMillisecond(l, f);
            if (d >= 1) {
                // 从0时算起...
                minTime = 0;
                maxTime = 23;
            } else {
                initFirstNeedJudge(groupType, dateTableService, fieldName);
            }
        } else if (groupType == BIReportConstant.GROUP.MINUTE
                || groupType == BIReportConstant.GROUP.SECOND
                || DateUtils.isCubeLongTimeGroup(groupType)) {
            // 日 直接进行最小最大的补全
            initFirstNeedJudge(groupType, dateTableService, fieldName);
        }
        return;
    }


    private void initFirstNeedJudge(int groupType, ICubeTableService dateTableService, String fieldName) {

        ICubeColumnIndexReader dateMap = dateTableService.loadGroup(new IndexTypeKey(fieldName, groupType), getRelationList());
        minTime = BIDateUtils.firstDate(dateMap);
        maxTime = BIDateUtils.lastDate(dateMap);
    }


    public boolean isNeedComplete() {

        return isNeedComplete;
    }

    public Object getMinTime() {

        return minTime;
    }

    public Object getMaxTime() {

        return maxTime;
    }

    public void setMinTime(long minTime) {

        this.minTime = minTime;
    }

    public void setMaxTime(long maxTime) {

        this.maxTime = maxTime;
    }

    public void setNeedComplete(boolean needComplete) {

        isNeedComplete = needComplete;
    }

    /**
     * 获取下一个补全的对象,根据不同的分组进行返回不同的对象
     *
     * @param org  当前对象
     * @param last 上一个对象
     * @return
     */
    public Object getCompleteObject(Object org, Object last, int index) {
        // 需要进行补全 或者上一个日期为空的时候 直接返回当前日期
        if (!isNeedComplete || BICollectionUtils.isCubeNullKey(org)) {
            return org;
        }
        int groupType = getGroup().getType();
        int sortType = getSortType();
        // 如果为第一行
        if (index == 0 || BICollectionUtils.isCubeNullKey(last)) {
            if (sortType == BIReportConstant.SORT.NUMBER_DESC) {
                last = getLastData(maxTime, sortType, groupType);
            } else {
                last = getLastData(minTime, sortType, groupType);
            }
        }
        Object n = org;

        if (DateUtils.isCubeIntegerTimeGroup(groupType)) {
            // 年|月|季度|星期
            if (sortType == BIReportConstant.SORT.NUMBER_DESC) {
                // 如果降序的情况下是上一个年份的上一年
                if (GeneralUtils.objectToNumber(org).intValue() == GeneralUtils.objectToNumber(last).intValue() - 1) {
                    n = org;
                } else {
                    // 要不然就返回上一年
                    n = GeneralUtils.objectToNumber(last).intValue() - 1;
                }
            } else {
                // 如果是生序的情况下是上一个年份的下一年
                if (GeneralUtils.objectToNumber(org).intValue() == GeneralUtils.objectToNumber(last).intValue() + 1) {
                    n = org;
                } else {
                    // 下一年
                    n = GeneralUtils.objectToNumber(last).intValue() + 1;
                }
            }
        } else if (groupType == BIReportConstant.GROUP.YMD) {
            // 降序
            if (sortType == BIReportConstant.SORT.NUMBER_DESC) {
                if (DateUtils.sameYearMonthDay(DateUtils.lastYearMonthDay((Long) last).getTime(), (Long) org) || DateUtils.afterYearMonthDay((Long) org, (Long) last)) {
                    n = org;
                } else {
                    n = DateUtils.lastYearMonthDay((Long) last).getTime();
                }
            } else {
                if (DateUtils.sameYearMonthDay(DateUtils.nextYearMonthDay((Long) last).getTime(), (Long) org) || DateUtils.afterYearMonthDay((Long) last, (Long) org)) {
                    n = org;
                } else {
                    n = DateUtils.nextYearMonthDay((Long) last).getTime();
                }
            }
        } else if (groupType == BIReportConstant.GROUP.YS) {
            // 降序
            if (sortType == BIReportConstant.SORT.NUMBER_DESC) {
                // 当前年季度是上一年季度的下一季度
                if (DateUtils.sameYearSeason(DateUtils.lastYearSeason((Long) last).getTime(), (Long) org) || DateUtils.afterYearSeason((Long) org, (Long) last)) {
                    // 2015-04(last)-->2015-03(org)
                    n = org;
                } else {
                    // 上一个年季度的 2015-04(last),2015-02(org) -->2015-03(n)
                    n = DateUtils.lastYearSeason((Long) last).getTime();
                }
            } else {
                // 当前年季度是上一年季度的下一季度
                if (DateUtils.sameYearSeason(DateUtils.nextYearSeason((Long) last).getTime(), (Long) org) || DateUtils.afterYearSeason((Long) last, (Long) org)) {
                    // 2015-03(last),2015-04(org) || 2015-03(org),2015-04(last)
                    n = org;
                } else {
                    // 下一个年季度的 2015-02(last),2015-04(org) -->2015-03(n)
                    n = DateUtils.nextYearSeason((Long) last).getTime();
                }
            }
        } else if (groupType == BIReportConstant.GROUP.YM) {
            // 年月
            if (sortType == BIReportConstant.SORT.NUMBER_DESC) {
                if (DateUtils.atSameYearMonth(DateUtils.lastYearMonth((Long) last).getTime(), (Long) org) || DateUtils.afterYearMonth((Long) org, (Long) last)) {
                    // 2015-04(last)-->2015-03(org)
                    n = org;
                } else {
                    n = DateUtils.lastYearMonth((Long) last).getTime();
                }
            } else {
                if (DateUtils.atSameYearMonth(DateUtils.nextYearMonth((Long) last).getTime(), (Long) org) || DateUtils.afterYearMonth((Long) last, (Long) org)) {
                    // 2015-03(last),2015-04(org) || 2015-03(org),2015-04(last)
                    n = org;
                } else {
                    n = DateUtils.nextYearMonth((Long) last).getTime();
                }
            }
        } else if (groupType == BIReportConstant.GROUP.YW) {
            if (sortType == BIReportConstant.SORT.NUMBER_DESC) {
                if (DateUtils.atSameYearWeek(DateUtils.lastYearWeek((Long) last).getTime(), (Long) org) || DateUtils.afterYearWeek((Long) org, (Long) last)) {
                    n = org;
                } else {
                    n = DateUtils.lastYearMonth((Long) last).getTime();
                }
            } else {
                if (DateUtils.atSameYearWeek(DateUtils.nextYearMonth((Long) last).getTime(), (Long) org) || DateUtils.afterYearWeek((Long) last, (Long) org)) {
                    n = org;
                } else {
                    n = DateUtils.nextYearWeek((Long) last).getTime();
                }
            }
        } else if (groupType == BIReportConstant.GROUP.YMDH) {
            if (sortType == BIReportConstant.SORT.NUMBER_DESC) {
                if (DateUtils.sameYearMonthDayHour(DateUtils.lastYearMonthDayHour((Long) last).getTime(), (Long) org) || DateUtils.afterYearMonthDayHour((Long) org, (Long) last)) {
                    n = org;
                } else {
                    n = DateUtils.lastYearMonthDayHour((Long) last).getTime();
                }
            } else {
                if (DateUtils.sameYearMonthDayHour(DateUtils.nextYearMonthDayHour((Long) last).getTime(), (Long) org) || DateUtils.afterYearMonthDayHour((Long) last, (Long) org)) {
                    n = org;
                } else {
                    n = DateUtils.nextYearMonthDayHour((Long) last).getTime();
                }
            }
        } else if (groupType == BIReportConstant.GROUP.YMDHM) {
            if (sortType == BIReportConstant.SORT.NUMBER_DESC) {
                if (DateUtils.sameYearMonthDayHourMinute(DateUtils.lastYearMonthDayHourMinute((Long) last).getTime(), (Long) org) || DateUtils.afterYearMonthDayHourMinute((Long) org, (Long) last)) {
                    n = org;
                } else {
                    n = DateUtils.lastYearMonthDayHourMinute((Long) last).getTime();
                }
            } else {
                if (DateUtils.sameYearMonthDayHourMinute(DateUtils.nextYearMonthDayHourMinute((Long) last).getTime(), (Long) org) || DateUtils.afterYearMonthDayHourMinute((Long) last, (Long) org)) {
                    n = org;
                } else {
                    n = DateUtils.nextYearMonthDayHourMinute((Long) last).getTime();
                }
            }
        } else if (groupType == BIReportConstant.GROUP.YMDHMS) {
            if (sortType == BIReportConstant.SORT.NUMBER_DESC) {
                if (DateUtils.sameYearMonthDayHourMinuteSecond(DateUtils.lastYearMonthDayHourMinuteSecond((Long) last).getTime(), (Long) org) || DateUtils.afterYearMonthDayHourMinuteSecond((Long) org, (Long) last)) {
                    n = org;
                } else {
                    n = DateUtils.lastYearMonthDayHourMinuteSecond((Long) last).getTime();
                }
            } else {
                if (DateUtils.sameYearMonthDayHourMinuteSecond(DateUtils.nextYearMonthDayHourMinuteSecond((Long) last).getTime(), (Long) org) || DateUtils.afterYearMonthDayHourMinuteSecond((Long) last, (Long) org)) {
                    n = org;
                } else {
                    n = DateUtils.nextYearMonthDayHourMinuteSecond((Long) last).getTime();
                }
            }
        }
        return n;
    }

    /**
     * 获取上一个日期
     *
     * @param n
     * @param sortType
     * @param groupType
     * @return
     */
    private Object getLastData(Object n, int sortType, int groupType) {

        Object r = null;
        if (DateUtils.isCubeIntegerTimeGroup(groupType)) {

            if (sortType == BIReportConstant.SORT.NUMBER_DESC) {
                return GeneralUtils.objectToNumber(n).intValue() + 1;
            } else {
                return GeneralUtils.objectToNumber(n).intValue() - 1;
            }
        } else if (groupType == BIReportConstant.GROUP.YMD) {
            // 年月日类型的...
            if (sortType == BIReportConstant.SORT.NUMBER_DESC) {
                // 下一天
                return DateUtils.nextYearMonthDay((Long) n).getTime();
            } else {
                // 上一天
                return DateUtils.lastYearMonthDay((Long) n).getTime();
            }
        } else if (groupType == BIReportConstant.GROUP.YS) {
            // 年季度
            if (sortType == BIReportConstant.SORT.NUMBER_DESC) {
                // 下一季度
                return DateUtils.nextYearSeason((Long) n).getTime();
            } else {
                // 上一季度
                return DateUtils.lastYearSeason((Long) n).getTime();
            }
        } else if (groupType == BIReportConstant.GROUP.YM) {
            // 年月
            if (sortType == BIReportConstant.SORT.NUMBER_DESC) {
                // 下一个月份
                return DateUtils.nextYearMonth((Long) n).getTime();
            } else {
                // 上一个月份
                return DateUtils.lastYearMonth((Long) n).getTime();
            }
        } else if (groupType == BIReportConstant.GROUP.YW) {
            if (sortType == BIReportConstant.SORT.NUMBER_DESC) {
                return DateUtils.nextYearWeek((Long) n).getTime();
            } else {
                return DateUtils.lastYearWeek((Long) n).getTime();
            }
        } else if (groupType == BIReportConstant.GROUP.YMDH) {
            if (sortType == BIReportConstant.SORT.NUMBER_DESC) {
                return DateUtils.nextYearMonthDayHour((Long) n).getTime();
            } else {
                return DateUtils.lastYearMonthDayHour((Long) n).getTime();
            }
        } else if (groupType == BIReportConstant.GROUP.YMDHM) {
            if (sortType == BIReportConstant.SORT.NUMBER_DESC) {
                return DateUtils.nextYearMonthDayHourMinute((Long) n).getTime();
            } else {
                return DateUtils.lastYearMonthDayHourMinute((Long) n).getTime();
            }
        } else if (groupType == BIReportConstant.GROUP.YMDHMS) {
            if (sortType == BIReportConstant.SORT.NUMBER_DESC) {
                return DateUtils.nextYearMonthDayHourMinuteSecond((Long) n).getTime();
            } else {
                return DateUtils.lastYearMonthDayHourMinuteSecond((Long) n).getTime();
            }
        }
        return r;
    }
}