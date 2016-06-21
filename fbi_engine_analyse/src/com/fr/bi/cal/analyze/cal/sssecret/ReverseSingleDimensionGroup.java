package com.fr.bi.cal.analyze.cal.sssecret;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cal.analyze.cal.result.NewDiskBaseRootNodeChild;
import com.fr.bi.cal.analyze.cal.result.NewRootNodeChild;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.exception.TerminateExecutorException;
import com.fr.bi.field.dimension.calculator.CombinationDateDimensionCalculator;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.stable.StringUtils;


/**
 * Created by Connery on 2015/1/18.
 */
public class ReverseSingleDimensionGroup extends SingleDimensionGroup {
    private long childsSize = 0;
    private Boolean generatedResult = false;

    protected ReverseSingleDimensionGroup(BusinessTable tableKey, DimensionCalculator[] pcolumns, int[] pckindex, DimensionCalculator column, Object[] data, int ckIndex, GroupValueIndex gvi, ICubeDataLoader loader, boolean useRealData) {
        super(tableKey, pcolumns, pckindex, column, data, ckIndex, gvi, loader, useRealData, BIBaseConstant.PART_DATA_GROUP_LIMIT);
    }

    public static SingleDimensionGroup createDimensionGroup(final BusinessTable tableKey, final DimensionCalculator[] pcolumns, final int[] pckindex, final DimensionCalculator column, final Object[] data, final int ckIndex, final GroupValueIndex gvi, final ICubeDataLoader loader , final boolean useRealData) {
        return new ReverseSingleDimensionGroup(tableKey, pcolumns, pckindex, column, data, ckIndex, gvi, loader, useRealData);
    }

    @Override
    protected NewDiskBaseRootNodeChild createEmptyChild() {
        ComparableLinkedHashSet set = new ComparableLinkedHashSet();
        set.add(StringUtils.EMPTY);
        NewDiskBaseRootNodeChild child = new NewDiskBaseRootNodeChild(column, set);
        child.setShowValue(StringUtils.EMPTY);
        child.setGroupValueIndex(root.getGroupValueIndex());
        return child;
    }

    @Override
    public NoneDimensionGroup getChildDimensionGroup(int row) {
        NewDiskBaseRootNodeChild node = getChildByWait(row);
        return NoneDimensionGroup.createDimensionGroup(tableKey, node.getGroupValueIndex(), getLoader());
    }

    @Override
    public Object getChildData(int row) {
        NewDiskBaseRootNodeChild node = getChildByWait(row);
        return node.getData();
    }

    @Override
    public String getChildShowName(int row) {
        NewDiskBaseRootNodeChild node = getChildByWait(row);
        return node.getShowValue();
    }

    @Override
    public Node getChildNode(int row) {
        try {
            return getChildByWait(row);
        } catch (GroupOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    protected NewDiskBaseRootNodeChild getChildByWait(int row) {
        generateReverseResult();
        NewDiskBaseRootNodeChild child;
        if (!root.getPageNodes().isEmpty() && row == 0) {
            return root.getPageNodes().get(0);
        } else if (root.getPageNodes().isEmpty() && row == 0) {
            ComparableLinkedHashSet set = new ComparableLinkedHashSet();
            set.add(StringUtils.EMPTY);
            child = new NewDiskBaseRootNodeChild(column, set);
            child.setShowValue(StringUtils.EMPTY);
            child.setGroupValueIndex(root.getGroupValueIndex());
            root.getPageNodes().add(child);
            return child;
        } else {
            throw GroupOutOfBoundsException.create(row);
        }

    }

    private void generateReverseResult() {
        synchronized (generatedResult) {
            if (generatedResult) {
                return;
            }
            int row = 0;
            while (true) {
                waitExecutor(row);

                if (row > getCurrentTotalRow() - 1) {
                    break;
                } else {
                    row++;
                }
            }
            generatedResult = true;
        }
    }


    @Override
    public void addNodeChild(NewRootNodeChild childNode) {
        if (childsSize != 0) {
            NewRootNodeChild newRootNodeChild = root.getPageNodes().get(0);
            String showName;
            Object data = new ComparableLinkedHashSet();
            if (column instanceof CombinationDateDimensionCalculator) {
                showName = getCombinationShowName(childNode, newRootNodeChild, (ComparableLinkedHashSet) data);
            } else {
                Object d = newRootNodeChild.getData();
                if (d instanceof ComparableLinkedHashSet) {
                    ((ComparableLinkedHashSet) data).addAll((ComparableLinkedHashSet) d);
                } else {
                    ((ComparableLinkedHashSet) data).add(d.toString());
                }
                ((ComparableLinkedHashSet) data).add(childNode.getData().toString());
                showName = newRootNodeChild.getShowValue() + "," + childNode.getData().toString();
            }
            newRootNodeChild.setData(data);
            GroupValueIndex gvi = newRootNodeChild.getGroupValueIndex().OR(childNode.getGroupValueIndex());
            newRootNodeChild.setGroupValueIndex(gvi);
            newRootNodeChild.setShowValue(showName);
            root.getPageNodes().clear();
            root.addChild((NewDiskBaseRootNodeChild) newRootNodeChild);
        } else {
            String value = childNode.getData().toString();
            childNode.setShowValue(value);
            ComparableLinkedHashSet set = new ComparableLinkedHashSet();
            set.add(value);
            childNode.setData(set);
            childNode.setComparator(BIBaseConstant.COMPARATOR.COMPARABLE.ASC);
            root.addChild((NewDiskBaseRootNodeChild) childNode);
        }
        childsSize++;
    }

    private String getCombinationShowName(NewRootNodeChild childNode, NewRootNodeChild newRootNodeChild, ComparableLinkedHashSet data) {
        String showName;
        Object d = newRootNodeChild.getData();
        if (d instanceof ComparableLinkedHashSet) {
            data.addAll((ComparableLinkedHashSet) d);
        } else {
            data.add((newRootNodeChild.getData()).toString());
        }
        data.add(childNode.getData().toString());
        showName = newRootNodeChild.getShowValue() + "," + childNode.getData().toString();
        return showName;
    }

    @Override
    public void endCheck() throws TerminateExecutorException {

    }

    @Override
    public int getCurrentTotalRow() {
        return (int) childsSize;
    }
}