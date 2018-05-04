package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.bean.dashboard.widget.control.tree.TreeOptionsBean;
import com.finebi.conf.internalimp.dashboard.widget.control.tree.TreeWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.result.control.tree.BITreeItem;
import com.finebi.conf.structure.result.control.tree.BITreeResult;
import com.fr.stable.StringUtils;
import com.fr.swift.adaptor.encrypt.SwiftEncryption;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.util.pinyin.PinyinUtils;
import edu.emory.mathcs.backport.java.util.Arrays;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Lyon on 2018/3/26.
 */
public class TreeWidgetAdaptor extends AbstractTableWidgetAdaptor{

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TreeWidgetAdaptor.class);

    public static BITreeResult calculate(TreeWidget treeWidget) {
        BITreeResult result = null;
        try {
            TreeOptionsBean bean = treeWidget.getValue().getOptions().getTreeOptions();
            FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(null, treeWidget.getFilters());
            // TODO: 2018/4/13 BITreeResult暂时不分页
            List<BITreeItem> treeItems = createTreeItemList(treeWidget.getWidgetId(), bean, filterInfo, treeWidget.getDimensionList());
            result = new TreeResult(false, treeItems);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return result;
    }

    static List<BITreeItem> createTreeItemList(String widId, TreeOptionsBean bean, FilterInfo filterInfo,
                                                      List<FineDimension> dimensions) {
        List<BITreeItem> treeItems;
        String keyWord = bean.getKeyword();
        if (StringUtils.isEmpty(keyWord)) {
            treeItems = createTreeItemListWithoutSearch(widId, bean, filterInfo, dimensions);
        } else if (StringUtils.isNotEmpty(keyWord) && StringUtils.isNotEmpty(bean.getId())) {
            // 因为功能要求子节点展开全部子节点，所以不传搜索的keyWord了
            treeItems = createChildNoSearchItemList(widId, "", bean, filterInfo, dimensions);
        } else {
            treeItems = createSearchItemList(0, widId, keyWord, "0", bean.getSelectedValues(),
                    new String[0], filterInfo, dimensions);
        }
        return treeItems;
    }

    private static List<BITreeItem> createChildNoSearchItemList(String widgetId, String keyWord, TreeOptionsBean bean,
                                                                FilterInfo filter, List<FineDimension> dimensions) {
        List<String> parents = bean.getParentValues();
        assert parents.size() == 1;
        Map selectedValues = bean.getSelectedValues();
        selectedValues = selectedValues == null ? new LinkedHashMap() : (Map) selectedValues.get(parents.get(0));
        return createSearchItemList(0, widgetId, keyWord, bean.getId(), selectedValues, new String[0],
                filter, dimensions.subList(parents.size(), dimensions.size()));
    }

    private static List<BITreeItem> createSearchItemList(int dimensionIndex, String widgetId, String keyWord, String pId,
                                                 Map selectedValues, String[] parentArray,
                                                 FilterInfo filterInfo, List<FineDimension> dimensions) {
        List<BITreeItem> items = new ArrayList<BITreeItem>();
        List<String> parents = Arrays.asList(Arrays.copyOf(parentArray, parentArray.length));
        selectedValues = selectedValues == null ? new LinkedHashMap() : selectedValues;
        List values = getValues(widgetId, dimensions.get(dimensionIndex), filterInfo,
                selectedValues2FilterInfo(dimensionIndex, dimensions, selectedValues), parents, dimensions);
        for (int j = 0; j < values.size(); j++) {
            String value = values.get(j).toString();
            // 计算id的结构，前端依据id把列表转为树结构
            String id = pId.equals("0") ? j + 1 + "" : pId + "_" + (j + 1);
            boolean isContained = PinyinUtils.isMatch(keyWord, value);
            if (!isContained) {
                List<BITreeItem> childrenItems = new ArrayList<BITreeItem>();
                if (dimensionIndex < dimensions.size() - 1) {
                    // 当前value不匹配keyWord，继续查找子节点
                    List<String> copy = new ArrayList<String>(Arrays.asList(Arrays.copyOf(parentArray, parentArray.length)));
                    copy.add(value);
                    String[] copyArray = copy.toArray(new String[copy.size()]);
                    childrenItems = createSearchItemList(dimensionIndex + 1, widgetId,
                            keyWord, id, selectedValues, copyArray, filterInfo, dimensions);
                }
                if (childrenItems.isEmpty()) {
                    // 因为子节点里面没有找到符合keyWord的item，所以当前value不添加展示了
                    continue;
                }
                items.addAll(childrenItems);
            }
            boolean isParent = parentArray.length < dimensions.size() - 1;
            boolean isChildrenChecked = isChildrenChecked(parents, selectedValues);
            // 不包含且是父节点才open节点
            items.add(createItem(isParent, isChildrenChecked, isParent & !isContained, value, id, pId, getSelectedChildren(parents, selectedValues)));
        }
        return items;
    }

    private static List getValues(String widgetId, FineDimension dimension, FilterInfo filter, FilterInfo selectedValues,
                                  List<String> parents, List<FineDimension> dimensions) {
        // 树控件这边的维度字段的过滤只作用于当前维度，不同于分组表那边作用于指标的明细
        FilterInfo currentDimensionFilter = FilterInfoFactory.transformDimensionFineFilter(dimension);
        FilterInfo filterInfo = parents2FilterInfo(filter, currentDimensionFilter, parents, dimensions);
        List<FilterInfo> or = new ArrayList<FilterInfo>();
        or.add(filterInfo);
        or.add(selectedValues);
        filterInfo = new GeneralFilterInfo(or, GeneralFilterInfo.OR);
        List values = QueryUtils.getOneDimensionFilterValues(dimension, filterInfo, widgetId);
        return values;
    }

    private static List<BITreeItem> createTreeItemListWithoutSearch(String widgetId, TreeOptionsBean bean, FilterInfo filter,
                                               List<FineDimension> dimensions) {
        List<String> parents = bean.getParentValues();
        parents = parents == null ? new ArrayList<String>() : parents;
        Map selectedValues = bean.getSelectedValues();
        selectedValues = selectedValues == null ? new LinkedHashMap() : selectedValues;
        // 根据有多少个父节点来判断要加载哪个维度的子节点
        FineDimension dimension = dimensions.get(parents.size());
        List values = getValues(widgetId, dimension, filter,
                selectedValues2FilterInfo(parents.size(), dimensions, selectedValues), parents, dimensions);
        boolean isParent = parents.size() < bean.getFloors() - 1;
        // 父节点是否被选中，如果被选中，则有的子节点可能被选中，否则所有子节点没有被选中
        // 父节点被选中同时selectedChildren.size() == 0，说明子节点被全选
        boolean isChildrenChecked = isChildrenChecked(parents, selectedValues);
        Map selectedChildren = getSelectedChildren(parents, selectedValues);
        return createTreeItemListFromValues(isParent, isChildrenChecked, "0", values, selectedChildren);
    }

    /**
     * selectedValues的结构为LinkedHashMap<String, LinkedHashMap>
     *
     * @param dimensionIndex
     * @param dimensions
     * @param selectedValues
     * @return
     */
    private static FilterInfo selectedValues2FilterInfo(int dimensionIndex, List<FineDimension> dimensions,
                                                        Map selectedValues) {
        List<Map> maps = new ArrayList<Map>();
        maps.add(selectedValues);
        Set<String> values = new LinkedHashSet<String>();
        for (int i = 0; i < dimensionIndex + 1; i++) {
            List<Map> subMaps = new ArrayList<Map>();
            for (Map map : maps) {
                values.addAll(map.keySet());
                subMaps.addAll(map.values());
            }
            maps = subMaps;
        }
        String fieldName = SwiftEncryption.decryptFieldId(dimensions.get(dimensionIndex).getFieldId())[1];
        return new SwiftDetailFilterInfo<Set<String>>(new ColumnKey(fieldName), values, SwiftDetailFilterType.STRING_IN);
    }

    private static Map getSelectedChildren(List<String> parents, Map selectedValues) {
        Map tmpMap = selectedValues;
        for (int i = 0; i < parents.size(); i++) {
            if (!tmpMap.containsKey(parents.get(i))) {
                // 说明父节点没有被选中
                return new LinkedHashMap();
            }
            tmpMap = (Map) tmpMap.get(parents.get(i));
        }
        return tmpMap;
    }

    private static boolean isChildrenChecked(List<String> parents, Map selectedValues) {
        if (parents.isEmpty()) {
            return false;
        }
        Map tmpMap = selectedValues;
        for (int i = 0; i < parents.size(); i++) {
            if (!tmpMap.containsKey(parents.get(i))) {
                // 说明父节点没有被选中
                return false;
            }
            tmpMap = (Map) tmpMap.get(parents.get(i));
        }
        return tmpMap.isEmpty();
    }

    private static FilterInfo parents2FilterInfo(FilterInfo filterInfo, FilterInfo currentDimensionFilter, List<String> parents, List<FineDimension> dimensions) {
        List<FilterInfo> infoList = new ArrayList<FilterInfo>();
        infoList.add(filterInfo);
        infoList.add(currentDimensionFilter);
        for (int i = 0; i < parents.size(); i++) {
            FineDimension dimension = dimensions.get(i);
            String fieldName = getColumnName(dimension.getFieldId());
            final String data = parents.get(i);
            infoList.add(new SwiftDetailFilterInfo<Set<String>>(new ColumnKey(fieldName),
                    new HashSet<String>() {{ add(data); }}, SwiftDetailFilterType.STRING_IN));
        }
        return new GeneralFilterInfo(infoList, GeneralFilterInfo.AND);
    }

    private static List<BITreeItem> createTreeItemListFromValues(boolean isParent, boolean isChildrenChecked, String pId,
                                                    List values, Map selectedChildren) {
        List<BITreeItem> items = new ArrayList<BITreeItem>();
        for (int i = 0; i < values.size(); i++) {
            items.add(createItem(isParent, isChildrenChecked, values.get(i).toString(),
                    pId + "_" + i, pId, selectedChildren));
        }
        return items;
    }

    private static BITreeItem createItem(boolean isParent, boolean isChildrenChecked,
                                         String value, String id, String pId, Map selectedChildren) {
        return createItem(isParent, isChildrenChecked, false, value, id, pId, selectedChildren);
    }

    private static BITreeItem createItem(boolean isParent, boolean isChildrenChecked, boolean isOpen,
                                         String value, String id, String pId, Map selectedChildren) {
        BITreeItem item = new BITreeItem();
        item.setId(id);
        item.setpId(pId);
        item.setParent(isParent);
        item.setOpen(isOpen);
        item.setValue(value);
        item.setText(value);
        item.setTitle(value);
        item.setTimes(1);
        /**
         * 如果父节点是选中状态且selectedValues.isEmpty，则items为选中状态
         * 1、如果selectedChildren.size() == 0，则所有item都是不选状态
         * 2、如果selectedChildren.size() == k，则设置该节点为halfChecked或者checked状态
         */
        if (isChildrenChecked) {
            item.setChecked(true);
        } else {
            Map mapOfChild = (Map) selectedChildren.get(value);
            if (mapOfChild == null) {
                item.setChecked(false);
            } else if (mapOfChild.isEmpty()) {
                // item被选了 && item没有子节点被选中，这种情况定item为选中，如果展开item的子节点，那么所有子节点选中
                item.setChecked(true);
            } else {
                // item被选了 && item有子节点被选中了，这种情况item有可能是全选或者半选状态，这个状态前端能判断的
                item.setHalfCheck(true);
            }
        }
        return item;
    }

    private static class TreeResult implements BITreeResult {
        // 分页
        private boolean hasNext;
        private List<BITreeItem> items;

        public TreeResult(boolean hasNext, List<BITreeItem> items) {
            this.hasNext = hasNext;
            this.items = items;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public List<BITreeItem> getItems() {
            return items;
        }

        @Override
        public ResultType getResultType() {
            return ResultType.TREE;
        }
    }
}
