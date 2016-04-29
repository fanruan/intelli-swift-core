package com.fr.bi.field.dimension.calculator;

import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.BICore;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.BICoreService;
import com.fr.bi.common.BICoreWrapper;
import com.fr.bi.conf.report.widget.BIDataColumn;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.operation.group.IGroup;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 小灰灰 on 2015/6/24.
 */
public abstract class AbstractDimensionCalculator implements DimensionCalculator, BICoreService {
    protected List<BITableSourceRelation> relations;
    protected BIDimension dimension;
    protected BIDataColumn field;

    public AbstractDimensionCalculator(BIDimension dimension, BIDataColumn field, List<BITableSourceRelation> relations) {
        this.dimension = dimension;
        this.field = field == null ? dimension.getStatisticElement() : field;
        this.relations = relations;
    }

    public AbstractDimensionCalculator() {

    }

    @Override
	public BIDataColumn getField() {
        return field;
    }


    @Override
    public BICore fetchObjectCore() {
        try {
            return new BIDimensionCore().fetchObjectCore();

        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return BIBasicCore.EMPTY_CORE;
    }

    /**
     * 获取维度到维度/指标的分组索引
     *
     * @param target
     * @param loader
     * @return
     */
    @Override
    public ICubeColumnIndexReader createNoneSortGroupValueMapGetter(Table target, ICubeDataLoader loader) {
        ICubeColumnIndexReader getter = loader.getTableIndex(field).loadGroup(createKey(), relations);
        return dimension.getGroup().createGroupedMap(getter);
    }

    /**
     * 是否为超级大分组
     *
     * @param targetTable 指标表
     * @param loader      注释
     * @return 是否为超级大分组
     */
    @Override
    public boolean isSupperLargeGroup(Table targetTable, ICubeDataLoader loader) {
        return false;
    }

    @Override
    public boolean hasSelfGroup() {
        return dimension.getGroup().isNullGroup();
    }

    /**
     * 是否为超级大分组
     *
     * @param loader 注释
     * @return 是否为超级大分组
     */
    @Override
    public boolean isSupperLargeGroup(ICubeDataLoader loader) {
        return false;
    }

    @Override
    public List<BITableSourceRelation> getRelationList() {
        return relations;
    }

    @Override
    public BIKey createKey() {
        return dimension.createKey(field);
    }

    @Override
    public Comparator getComparator() {
        return BIBaseConstant.COMPARATOR.COMPARABLE.ASC;

    }

    @Override
    public Object createEmptyValue() {
        return BIBaseConstant.EMPTY_NODE_DATA;
    }

    @Override
    public String getSortTarget() {
        return dimension.getSortTarget();
    }

    @Override
    public int getSortType() {
        return dimension.getSortType();
    }

    @Override
    public Iterator createValueMapIterator(Table table, ICubeDataLoader loader) {
        ICubeColumnIndexReader getter = createNoneSortGroupValueMapGetter(table, loader);
        if (getGroup().getType() == BIReportConstant.GROUP.NO_GROUP){
            return getSortType() != BIReportConstant.SORT.DESC ? getter.iterator() : getter.previousIterator();
        }
        return dimension.getSort().createGroupedMap(getter).iterator();
    }

    @Override
    public Iterator createValueMapIterator(Table table, ICubeDataLoader loader, boolean useRealData, int groupLimit) {
        ICubeColumnIndexReader getter = loader.getTableIndex(field).loadGroup(dimension.createKey(field), getRelationList(), useRealData, groupLimit);
        getter = dimension.getGroup().createGroupedMap(getter);
        if (getGroup().getType() == BIReportConstant.GROUP.NO_GROUP){
            return getSortType() != BIReportConstant.SORT.DESC ? getter.iterator() : getter.previousIterator();
        }
        return dimension.getSort().createGroupedMap(getter).iterator();
    }

    @Override
    public ICubeColumnIndexReader createValueMap(Table table, ICubeDataLoader loader) {
        return createValueMap(table, loader, true, 0);
    }

    @Override
    public ICubeColumnIndexReader createValueMap(Table table, ICubeDataLoader loader, boolean useRealData, int groupLimit) {
        ICubeColumnIndexReader getter = loader.getTableIndex(field).loadGroup(dimension.createKey(field), getRelationList(), useRealData, groupLimit);
        getter = dimension.getGroup().createGroupedMap(getter);
        return dimension.getSort().createGroupedMap(getter);
    }

    @Override
    public int getBaseTableValueCount(Object value, ICubeDataLoader loader) {
        GroupValueIndex[] gvis = loader.getTableIndex(field).getIndexes(dimension.createKey(field), new Object[]{value});
        if (gvis != null) {
            if (gvis[0] != null) {
                return (int) gvis[0].getRowsCountWithData();
            }
        }
        return 0;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        StringDimensionCalculator sdc = (StringDimensionCalculator) super.clone();
        sdc.field = (BIDataColumn) this.field.clone();
        sdc.dimension = this.dimension;
        sdc.relations = this.relations;
        return sdc;

    }

    @Override
    public IGroup getGroup() {
        return dimension.getGroup();
    }

    public class BIDimensionCore extends BICoreWrapper {
        public BIDimensionCore() throws Exception {
            super(field, relations, dimension.getGroup(), dimension.getSort());
        }
    }


}