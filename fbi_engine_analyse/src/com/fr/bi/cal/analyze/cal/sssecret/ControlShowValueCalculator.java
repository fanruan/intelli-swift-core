package com.fr.bi.cal.analyze.cal.sssecret;


import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.FinalLong;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.analyze.exception.NoneAccessablePrivilegeException;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.stable.io.NIOReadGroupMap;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.manager.PlugManager;
import com.fr.bi.stable.connection.ConnectionRowGetter;
import com.fr.bi.stable.connection.DirectTableConnection;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.BrokenTraversalAction;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.util.BIConfUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.pinyin.PinyinHelper;

import java.util.*;
import java.util.Map.Entry;

/**
 * 控件取值计算
 *
 * @author Daniel
 */
public class ControlShowValueCalculator {

    private final static int MAX_STRING_ROW = 100;


    private static boolean isValueValid(String value, boolean isBlank, String kw, boolean isSmallGroup) {
        if (value == null) {
            return false;
        }
        boolean valueValid = isBlank || value.toUpperCase().indexOf(kw) > -1;
        if (!valueValid && PlugManager.getPerformancePlugManager().isSearchPinYin() && isSmallGroup) {
            String py = PinyinHelper.getShortPinyin(value);
            valueValid = py.toUpperCase().indexOf(kw) > -1;
        }
        return valueValid;
    }

    public static List<String> getControlShowValueBySearch(DimensionCalculator calculator, BISession session, String keyword, int page, final List selectValues, TargetFilter filter) {
        BusinessTable key = calculator.getField().getTableBelongTo();
        if (!session.hasPackageAccessiblePrivilege(key)) {
            throw new NoneAccessablePrivilegeException();
        }
        int rowCount = (int) session.getLoader().getTableIndex(key.getTableSource()).getRowCount();
        GroupValueIndex parentIndex = session.createFilterGvi(key).AND(filter.createFilterIndex(key, session.getLoader(), session.getUserId()));
        final int start = page * MAX_STRING_ROW;
        final String keyWord = keyword == null ? StringUtils.EMPTY : keyword.toUpperCase();
        if (parentIndex.isAllEmpty()) {
            return new ArrayList<String>();
        }
        boolean isAllShow = parentIndex.getRowsCountWithData() == rowCount;
        if (isAllShow) {
            return getWhenAllShow(session, selectValues, calculator, start, keyWord, !calculator.isSupperLargeGroup(session.getLoader()));
        } else if (calculator.isSupperLargeGroup(session.getLoader())) {
            return getWhenSupperLargeGroup(session, selectValues, calculator, parentIndex, start, keyWord);
        } else {
            return getStringControlShowValueBySearch0(session, selectValues, calculator, parentIndex, start, keyWord);
        }
    }

    private static List<String> getStringControlShowValueBySearch0(BISession session, final List selectValues, DimensionCalculator calculator, GroupValueIndex parentIndex, int start, String keyWord) {
        List<String> resultList = new ArrayList<String>();
        boolean isBlank = StringUtils.isBlank(keyWord);
        int count = 0;
        Iterator<Entry<String, GroupValueIndex>> iter = calculator.createValueMapIterator(calculator.getField().getTableBelongTo(), session.getLoader());
        while (iter.hasNext()) {
            Entry<String, GroupValueIndex> entry = iter.next();
            String rowValue = entry.getKey();
            if (isValueValid(rowValue, isBlank, keyWord, true)) {
                GroupValueIndex v = entry.getValue();
                if (v != null && v.hasSameValue(parentIndex)) {
                    if (selectValues.contains(rowValue) || start > count++) {
                        continue;
                    }
                    resultList.add(rowValue);
                    if (resultList.size() == MAX_STRING_ROW + 1) {
                        break;
                    }
                }
            }
        }
        return resultList;
    }

    private static List<String> getWhenSupperLargeGroup(BISession session, final List selectValues, DimensionCalculator calculator, GroupValueIndex parentIndex, final int start, final String keyWord) {
        //大分组考虑顺序意义不大 不如显示的排序
        final Set<String> set = new TreeSet<String>(BIBaseConstant.COMPARATOR.STRING.ASC_STRING_CC);
        final ICubeTableService ti = session.getLoader().getTableIndex(calculator.getField().getTableBelongTo().getTableSource());
        final BIKey index = calculator.createKey();
        final boolean isBlank = StringUtils.isBlank(keyWord);
        final FinalLong count = new FinalLong();
        List<String> resultList = new ArrayList<String>();
        final ICubeColumnDetailGetter getter = ti.getColumnDetailReader(index);
        parentIndex.BrokenableTraversal(new BrokenTraversalAction() {
            @Override
            public boolean actionPerformed(int rowIndex) {
                String rowValue = (String) getter.getValue(rowIndex);
                if (isValueValid(rowValue, isBlank, keyWord, false)) {
                    if (selectValues.contains(rowValue) || start > count.value++) {
                        return false;
                    }
                    set.add(rowValue);
                }
                return set.size() == MAX_STRING_ROW + 1;
            }
        });
        resultList.addAll(set);
        return resultList;
    }

    private static List<String> getWhenAllShow(BISession session, List selectValues, DimensionCalculator calculator, int start, String keyWord, boolean isSmallGroup) {
        List<String> resultList = new ArrayList<String>();
        boolean isBlank = StringUtils.isBlank(keyWord);
        int count = 0;
        Iterator<Entry<String, GroupValueIndex>> iter = calculator.createValueMapIterator(calculator.getField().getTableBelongTo(), session.getLoader());
        while (iter.hasNext()) {
            String rowValue = iter.next().getKey();
            if (isValueValid(rowValue, isBlank, keyWord, isSmallGroup)) {
                if (selectValues.contains(rowValue) || start > count++) {
                    continue;
                }
                resultList.add(rowValue);
                if (resultList.size() == MAX_STRING_ROW + 1) {//获取多一条数据
                    break;
                }
            }
        }
        return resultList;
    }

    /**
     * 获取分页字段
     *
     * @param currentKey
     * @param parentIndex
     * @param rank
     * @param times
     * @param resultList
     */
    private static void treeControlShowValue(BITableSourceRelation[] relation, DimensionCalculator currentKey, GroupValueIndex parentIndex, int rank, int times, final List<String> resultList, BISession sessionIDInfo) {
        final int start = (times - 1) * MAX_STRING_ROW;
        final FinalLong t = new FinalLong();
        t.value = 0L;
        if (currentKey.isSupperLargeGroup(sessionIDInfo.getLoader())) {
            treeControlShowValuWhenSupperLargeGroup(relation, currentKey, parentIndex, rank, resultList, sessionIDInfo, start, t);
        } else {
            ICubeTableService ti = sessionIDInfo.getLoader().getTableIndex(currentKey.getField().getTableBelongTo().getTableSource());
            NIOReadGroupMap<String> getter = (NIOReadGroupMap<String>) ti.loadGroup(currentKey.createKey(), Arrays.asList(relation));
            Iterator<Entry<String, GroupValueIndex>> iter = rank == BIReportConstant.SORT.ASC ? getter.iterator() : getter.previousIterator();
            while (iter.hasNext()) {
                Entry<String, GroupValueIndex> entry = iter.next();
                String rowValue = entry.getKey();
                if (rowValue != null) {
                    GroupValueIndex v = entry.getValue();
                    try {
                        if (v != null && v.hasSameValue(parentIndex)) {
                            if (start > t.value++) {
                                continue;
                            }
                            resultList.add(rowValue);
                            if (resultList.size() == MAX_STRING_ROW) {
                                break;
                            }
                        }
                    } catch (Exception e) {
                        BILogger.getLogger().error("GroupValueIndex hasSameValue error", e);
                    }
                }
            }
        }
    }

    private static void treeControlShowValuWhenSupperLargeGroup(BITableSourceRelation[] relation, DimensionCalculator currentKey, GroupValueIndex parentIndex, int rank, List<String> resultList, BISession sessionIDInfo, final int start, final FinalLong t) {
        final Set<String> set = new TreeSet<String>(rank == BIReportConstant.SORT.ASC
                ? BIBaseConstant.COMPARATOR.STRING.ASC_STRING_CC : BIBaseConstant.COMPARATOR.STRING.DESC_STRING_CC);
        final ICubeTableService ti = sessionIDInfo.getLoader().getTableIndex(currentKey.getField().getTableBelongTo().getTableSource());
        DirectTableConnection c = BIConfUtils.createDirectTableConnection(relation, sessionIDInfo.getLoader());
        final ConnectionRowGetter getter = new ConnectionRowGetter(c);
        final BIKey index = currentKey.createKey();
        final ICubeColumnDetailGetter columnDetailReader = ti.getColumnDetailReader(index);
        parentIndex.BrokenableTraversal(new BrokenTraversalAction() {
            @Override
            public boolean actionPerformed(int rowIndex) {
                int currentRow = getter.getConnectedRow(rowIndex);
                String rowValue = (String) columnDetailReader.getValue(currentRow);
                if (rowValue != null) {
                    if (start > t.value++) {
                        return false;
                    }
                    set.add(rowValue);
                }
                return set.size() == MAX_STRING_ROW;
            }

        });
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            resultList.add(it.next());
            if (resultList.size() == MAX_STRING_ROW) {
                break;
            }
        }
    }

    /**
     * 获取所有字段,无分页
     *
     * @param targetKey
     * @param currentKey
     * @param parentIndex
     * @param rank
     * @param resultList
     */
    private static void treeControlShowValue(DimensionCalculator targetKey, DimensionCalculator currentKey, GroupValueIndex parentIndex, int rank, final List<String> resultList, BISession sessionIDInfo) {

        if (currentKey.isSupperLargeGroup(sessionIDInfo.getLoader())) {
            final Set<String> set = new TreeSet<String>(rank == BIReportConstant.SORT.ASC
                    ? BIBaseConstant.COMPARATOR.STRING.ASC_STRING_CC : BIBaseConstant.COMPARATOR.STRING.DESC_STRING_CC);
            final ICubeTableService ti = sessionIDInfo.getLoader().getTableIndex(currentKey.getField().getTableBelongTo().getTableSource());
            DirectTableConnection c = BIConfUtils.createDirectTableConnection(currentKey.getRelationList(), sessionIDInfo.getLoader());
            final ConnectionRowGetter getter = new ConnectionRowGetter(c);
            final BIKey index = currentKey.createKey();
            final ICubeColumnDetailGetter columnDetailReader = ti.getColumnDetailReader(index);
            parentIndex.BrokenableTraversal(new BrokenTraversalAction() {
                @Override
                public boolean actionPerformed(int rowIndex) {
                    int currentRow = getter.getConnectedRow(rowIndex);
                    String rowValue = (String) columnDetailReader.getValue(currentRow);
                    if (rowValue != null) {
                        set.add(rowValue);
                    }
                    return false;
                }

            });
            Iterator<String> it = set.iterator();
            while (it.hasNext()) {
                resultList.add(it.next());
            }
        } else {
            NIOReadGroupMap<String> getter = (NIOReadGroupMap<String>) currentKey.createValueMap(targetKey.getField().getTableBelongTo(), sessionIDInfo.getLoader());
            Iterator<Entry<String, GroupValueIndex>> iter = rank == BIReportConstant.SORT.ASC ? getter.iterator() : getter.previousIterator();
            while (iter.hasNext()) {
                Entry<String, GroupValueIndex> entry = iter.next();
                String rowValue = entry.getKey();
                if (rowValue != null) {
                    GroupValueIndex v = entry.getValue();
                    if (v != null && v.hasSameValue(parentIndex)) {
                        resultList.add(rowValue);
                    }
                }
            }
        }
    }

    /**
     * @param columns      tree的所有字段数组,有分页
     * @param value        当前元素的父value数组
     * @param control_name 控件名字
     * @param session      session
     * @param rank         排序
     *                     &param times 第几次加载
     * @return 数据
     */
    public static List<String> getStringTreeControlShowValue(DimensionCalculator[] columns,
                                                             String[] value,
                                                             String control_name,
                                                             BISession session, int rank, int times,
                                                             BITableSourceRelation[][] relations, BusinessTable target) {
        int start = (times - 1) * MAX_STRING_ROW;
        int cnt = 0;
        final List<String> resultList = new ArrayList<String>();
        if (columns.length < value.length + 1) {
            return resultList;
        }
        for (int i = 0; i < columns.length; i++) {
            if (!session.hasPackageAccessiblePrivilege(columns[i].getField().getTableBelongTo())) {
                throw new NoneAccessablePrivilegeException();
            }
        }
        DimensionCalculator currentKey = columns[value.length];
        GroupValueIndex parentIndex = session.createFilterGvi(target);
        if (parentIndex == null) {
            parentIndex = session.getLoader().getTableIndex(target.getTableSource()).getAllShowIndex();
        }
        for (int i = 0; i < value.length; i++) {
            DimensionCalculator ck = columns[i];
            ICubeTableService ti = session.getLoader().getTableIndex(ck.getField().getTableBelongTo().getTableSource());
            NIOReadGroupMap<String> getter = (NIOReadGroupMap<String>) ti.loadGroup(ck.createKey(), Arrays.asList(relations[i]));
            GroupValueIndex gvi = getter.getGroupIndex(new String[]{value[i]})[0];
            parentIndex = parentIndex.AND(gvi);
        }
        if (parentIndex.isAllEmpty()) {
            return resultList;
        }
        int rowCount = (int) session.getLoader().getTableIndex(target.getTableSource()).getRowCount();
        boolean isAllShow = parentIndex.getRowsCountWithData() == rowCount;
        if (isAllShow) {
            dealWhenAllShow(value, session, rank, relations, start, cnt, resultList, currentKey);
        } else {
            treeControlShowValue(relations[value.length], currentKey, parentIndex, rank, times, resultList, session);
        }
        return resultList;
    }

    private static void dealWhenAllShow(String[] value, BISession session, int rank, BITableSourceRelation[][] relations, int start, int cnt, List<String> resultList, DimensionCalculator currentKey) {
        ICubeTableService ti = session.getLoader().getTableIndex(currentKey.getField().getTableBelongTo().getTableSource());
        NIOReadGroupMap<String> getter = (NIOReadGroupMap<String>) ti.loadGroup(currentKey.createKey(), Arrays.asList(relations[value.length]));
        Iterator<Entry<String, GroupValueIndex>> iter = rank == BIReportConstant.SORT.ASC ? getter.iterator() : getter.previousIterator();
        while (iter.hasNext()) {
            Entry<String, GroupValueIndex> entry = iter.next();
            String rowValue = entry.getKey();
            GroupValueIndex g = entry.getValue();
            if (rowValue != null && g != null && g.getRowsCountWithData() > 0) {
                if (start > cnt++) {
                    continue;
                }
                resultList.add(rowValue);
                if (resultList.size() == MAX_STRING_ROW) {
                    break;
                }
            }
        }
    }

    /**
     * @param columns tree的所有字段数组
     * @param value   当前元素的父value数组
     * @param session session
     * @param rank    排序
     * @return 数据
     */
    public static List<String> getStringTreeControlShowValue(DimensionCalculator[] columns,
                                                             String[] value,
                                                             BISession session, int rank) {
        int cnt = 0;
        final List<String> resultList = new ArrayList<String>();
        if (columns.length < value.length + 1) {
            return resultList;
        }
        for (int i = 0; i < columns.length; i++) {
            if (!session.hasPackageAccessiblePrivilege(columns[i].getField().getTableBelongTo())) {
                throw new NoneAccessablePrivilegeException();
            }
        }
        DimensionCalculator targetKey = columns[columns.length - 1];
        DimensionCalculator currentKey = columns[value.length];
        GroupValueIndex parentIndex = session.createFilterGvi(targetKey.getField().getTableBelongTo());
        if (parentIndex == null) {
            parentIndex = session.getLoader().getTableIndex(targetKey.getField().getTableBelongTo().getTableSource()).getAllShowIndex();
        }
        for (int i = 0; i < value.length; i++) {
            DimensionCalculator ck = columns[i];
            NIOReadGroupMap<String> getter = (NIOReadGroupMap<String>) ck.createValueMap(targetKey.getField().getTableBelongTo(), session.getLoader());
            GroupValueIndex gvi = getter.getGroupIndex(new String[]{value[i]})[0];
            parentIndex = parentIndex.AND(gvi);
        }
        if (parentIndex.isAllEmpty()) {
            return resultList;
        }
        int rowCount = (int) session.getLoader().getTableIndex(targetKey.getField().getTableBelongTo().getTableSource()).getRowCount();
        boolean isAllShow = parentIndex.getRowsCountWithData() == rowCount;
        if (isAllShow) {
            NIOReadGroupMap<String> getter = (NIOReadGroupMap<String>) currentKey.createValueMap(targetKey.getField().getTableBelongTo(), session.getLoader());
            Iterator<Entry<String, GroupValueIndex>> iter = rank == BIReportConstant.SORT.ASC ? getter.iterator() : getter.previousIterator();
            while (iter.hasNext()) {
                Entry<String, GroupValueIndex> entry = iter.next();
                String rowValue = entry.getKey();
                GroupValueIndex g = entry.getValue();
                if (rowValue != null && g != null && g.getRowsCountWithData() > 0) {
                    resultList.add(rowValue);
                }
            }
        } else {
            treeControlShowValue(targetKey, currentKey, parentIndex, rank, resultList, session);
        }
        return resultList;
    }
}