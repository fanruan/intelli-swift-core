package com.fr.bi.cal.analyze.cal.store;

import com.fr.bi.cal.analyze.cal.sssecret.CrossCalculator;
import com.fr.bi.cal.analyze.cal.sssecret.FilterSingleDimensionGroup;
import com.fr.bi.cal.analyze.cal.sssecret.NoneDimensionGroup;
import com.fr.bi.cal.analyze.cal.sssecret.SingleDimensionGroup;
import com.fr.bi.cal.stable.engine.TempCubeTask;
import com.fr.bi.conf.report.widget.field.dimension.filter.ResultFilter;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.structure.collection.map.lru.LRUWithKHashMap;
import com.fr.bi.common.inter.ValueCreator;

import com.fr.bi.stable.utils.BIUserUtils;
import com.fr.fs.control.UserControl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO数据更新之后应当重新计算分组内容 而不是清空map
 *
 * @author Daniel
 */
public class GroupManager {
    /**
     * TODO这里需要可以在后台设置缓存队列的大小
     */
    private static final int CACHE_SIZE = 64;
    private static Map<Long, GroupManager> userMap = new ConcurrentHashMap<Long, GroupManager>();
    private static Map<TempCubeTask, GroupManager> groupMap = new ConcurrentHashMap<TempCubeTask, GroupManager>();
    private long userId;
    private volatile LRUWithKHashMap<GroupKey, SingleDimensionGroup> singleDimensionGroup = new LRUWithKHashMap<GroupKey, SingleDimensionGroup>(CACHE_SIZE);
    private volatile LRUWithKHashMap<FilterGroupKey, FilterSingleDimensionGroup> filterSingleDimensionGroup = new LRUWithKHashMap<FilterGroupKey, FilterSingleDimensionGroup>(CACHE_SIZE);
    private volatile LRUWithKHashMap<GroupKey, NoneDimensionGroup> noneDimensionGroup = new LRUWithKHashMap<GroupKey, NoneDimensionGroup>(CACHE_SIZE << 4);
    /**
     * Map<String[],Group>
     */

    private volatile LRUWithKHashMap<CrossCalculatorKey, CrossCalculator> crossCalculators = new LRUWithKHashMap<CrossCalculatorKey, CrossCalculator>(CACHE_SIZE);
    private volatile LRUWithKHashMap<PageGroupKey, PageProvider> groupPages = new LRUWithKHashMap<PageGroupKey, PageProvider>(CACHE_SIZE);
    private volatile LRUWithKHashMap<ArrayList<PageGroupKey>, PageProvider> complexGroupPages = new LRUWithKHashMap<ArrayList<PageGroupKey>, PageProvider>(CACHE_SIZE);

    private GroupManager(long userId) {
        this.userId = userId;
    }

    public GroupManager() {
    }

    public static GroupManager getGroupManager(long userId) {
        synchronized (GroupManager.class) {
            Long key = userId;
            boolean useAdministrtor = BIUserUtils.isAdministrator(userId);
            if (useAdministrtor) {
                key = UserControl.getInstance().getSuperManagerID();
            }
            GroupManager manager = userMap.get(key);
            if (manager == null) {
                manager = new GroupManager();
                userMap.put(key, manager);
            }
            return manager;
        }
    }

    public static GroupManager getGroupManager(TempCubeTask task) {
        synchronized (GroupManager.class) {
            Long key = task.getUserId();
            boolean useAdministrtor = BIUserUtils.isAdministrator(key);
            if (useAdministrtor) {
                key = UserControl.getInstance().getSuperManagerID();
            }
            task.setUserId(key);
            GroupManager manager = groupMap.get(task);
            if (manager == null) {
                manager = new GroupManager();
                groupMap.put(task, manager);
            }
            return manager;
        }
    }

    public final static void release(TempCubeTask task) {
        if (task != null && groupMap.containsKey(task)) {
            groupMap.remove(task);
        }
    }

    public static void releaseAll() {
        synchronized (GroupManager.class) {
            Iterator<Entry<Long, GroupManager>> iter = userMap.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<Long, GroupManager> entry = iter.next();
                GroupManager loader = entry.getValue();
                if (loader != null) {
                    loader.release();
                }
            }
            userMap.clear();
        }
    }

    /**
     * FIXME 当前的原理无法进行数据更新之后的recalculate操作
     * 因为保存的是当前过滤条件产生的结果的行数，所以需要在cube更新之后首先更新这个行数的值
     * TODO 待 fix 先暂时取消这个功能
     */
    public void reCalculateGroups() {
        GroupManager.getGroupManager(userId).release();
    }

    public SingleDimensionGroup getSingleDimensionGroup(GroupKey key, ValueCreator<SingleDimensionGroup> creater) {
        return singleDimensionGroup.get(key, creater);
    }

    public FilterSingleDimensionGroup getFilterSingleDimensionGroup(Table key, DimensionCalculator[] columnKey, ResultFilter resultFilter, Map<String, TargetGettingKey> targetGettingKeyMap, ValueCreator<FilterSingleDimensionGroup> creater) {
        FilterGroupKey filterGroupKey = new FilterGroupKey(key, columnKey);
        filterGroupKey.setResultFilter(resultFilter);
        filterGroupKey.setTargetsMap(targetGettingKeyMap);

        return filterSingleDimensionGroup.get(filterGroupKey, creater);
    }

    public NoneDimensionGroup getNoneDimensionGroup(Table key, DimensionCalculator[] columnKey, ValueCreator<NoneDimensionGroup> creater) {
        return noneDimensionGroup.get(new GroupKey(key, columnKey), creater);
    }

    public PageProvider getGroupPages(PageGroupKey pageKey, ValueCreator<PageProvider> creater, boolean update) {
        if (update) {
            return groupPages.update(pageKey, creater);
        } else {
            return groupPages.get(pageKey, creater);
        }

    }

    /**
     * 释放内存
     */
    public void release() {
        crossCalculators.clear();
        groupPages.clear();
        complexGroupPages.clear();
        singleDimensionGroup.clear();
        noneDimensionGroup.clear();
        System.gc();
    }

}