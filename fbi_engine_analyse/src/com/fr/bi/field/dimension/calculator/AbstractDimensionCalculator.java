package com.fr.bi.field.dimension.calculator;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.BICore;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.BICoreService;
import com.fr.bi.common.BICoreWrapper;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.operation.group.IGroup;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 小灰灰 on 2015/6/24.
 */
public abstract class AbstractDimensionCalculator implements DimensionCalculator, BICoreService {
    protected List<BITableSourceRelation> relations;
    protected List<BITableSourceRelation> directToDimenRelations;
    protected BIDimension dimension;
    protected BusinessField field;

    public AbstractDimensionCalculator(BIDimension dimension, BusinessField field, List<BITableSourceRelation> relations) {
        this.dimension = dimension;
        field = field == null ? dimension.getStatisticElement() : field;
        this.field = field;
        this.relations = relations;
        this.directToDimenRelations = new ArrayList<BITableSourceRelation>();
    }

    public AbstractDimensionCalculator(BIDimension dimension, BusinessField field, List<BITableSourceRelation> relations, List<BITableSourceRelation> directToDimensionRelations) {
        this.dimension = dimension;
        field = field == null ? dimension.getStatisticElement() : field;
        this.field = field;
        this.relations = relations;
        this.directToDimenRelations = directToDimensionRelations;
    }

    public AbstractDimensionCalculator() {

    }

    @Override
    public BusinessField getField() {
        return field;
    }

    @Override
    public ICubeColumnIndexReader createNoneSortNoneGroupValueMapGetter(BusinessTable target, ICubeDataLoader loader) {
        //默认设置field本身为关联主键
        CubeTableSource usedTableSource = field.getTableBelongTo().getTableSource();
        BIKey usedColumnKey = dimension.createKey(field);
        //多对多处理,这里默认relationList的第一个关联是公共主表关联
        if (getDirectToDimensionRelationList().size() > 0) {
            ICubeFieldSource primaryField = getDirectToDimensionRelationList().get(0).getPrimaryField();
            usedTableSource = primaryField.getTableBelongTo();
            usedColumnKey = new IndexKey(primaryField.getFieldName());
        }
        return loader.getTableIndex(usedTableSource).loadGroup(usedColumnKey, relations);
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
    public ICubeColumnIndexReader createNoneSortGroupValueMapGetter(BusinessTable target, ICubeDataLoader loader) {
        return dimension.getGroup().createGroupedMap(createNoneSortNoneGroupValueMapGetter(target, loader));
    }

    @Override
    public boolean hasSelfGroup() {
        return dimension.getGroup().isNullGroup();
    }

    @Override
    public List<BITableSourceRelation> getRelationList() {
        return relations;
    }

    @Override
    public List<BITableSourceRelation> getDirectToDimensionRelationList() {
        return directToDimenRelations;
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
    public Iterator createValueMapIterator(BusinessTable table, ICubeDataLoader loader) {
        ICubeColumnIndexReader getter = createNoneSortGroupValueMapGetter(table, loader);
        if (isNoGroup()) {
            return getSortType() != BIReportConstant.SORT.DESC ? getter.iterator() : getter.previousIterator();
        }
        return dimension.getSort().createGroupedMap(getter).iterator();
    }

    @Override
    public Iterator createValueMapIterator(BusinessTable table, ICubeDataLoader loader, boolean useRealData, int groupLimit) {
        //默认设置field本身为关联主键
        CubeTableSource usedTableSource = getTableSourceFromField();
        BIKey usedColumnKey = dimension.createKey(field);
        //多对多处理,这里默认relationList的第一个关联是公共主表关联
        if (getDirectToDimensionRelationList().size() > 0) {
            ICubeFieldSource primaryField = getDirectToDimensionRelationList().get(0).getPrimaryField();
            CubeTableSource primaryTableSource = primaryField.getTableBelongTo();
            usedTableSource = primaryTableSource;
            usedColumnKey = new IndexKey(primaryField.getFieldName());
        }
        ICubeColumnIndexReader getter = loader.getTableIndex(usedTableSource).loadGroup(usedColumnKey, getRelationList(), useRealData, groupLimit);
        getter = dimension.getGroup().createGroupedMap(getter);
        if (useRealData && isNoGroup()) {
            return getSortType() != BIReportConstant.SORT.DESC ? getter.iterator() : getter.previousIterator();
        }
        return dimension.getSort().createGroupedMap(getter).iterator();
    }

    private boolean isNoGroup() {
        return getGroup().getType() == BIReportConstant.GROUP.NO_GROUP || getGroup().getType() == BIReportConstant.GROUP.ID_GROUP;
    }

    private CubeTableSource getTableSourceFromField() {
        return field.getTableBelongTo().getTableSource();
    }

    @Override
    public int getBaseTableValueCount(Object value, ICubeDataLoader loader) {
        GroupValueIndex[] gvis = loader.getTableIndex(getTableSourceFromField()).getIndexes(dimension.createKey(field), new Object[]{value});
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
        sdc.field = (BusinessField) this.field.clone();
        sdc.dimension = this.dimension;
        sdc.relations = this.relations;
        return sdc;

    }

    @Override
    public IGroup getGroup() {
        return dimension.getGroup();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractDimensionCalculator that = (AbstractDimensionCalculator) o;

        if (relations != null ? !relations.equals(that.relations) : that.relations != null) return false;
        if (directToDimenRelations != null ? !directToDimenRelations.equals(that.directToDimenRelations) : that.directToDimenRelations != null)
            return false;
        if (dimension != null ? !dimension.equals(that.dimension) : that.dimension != null) return false;
        return field != null ? field.equals(that.field) : that.field == null;

    }

    @Override
    public int hashCode() {
        int result = relations != null ? relations.hashCode() : 0;
        result = 31 * result + (directToDimenRelations != null ? directToDimenRelations.hashCode() : 0);
        result = 31 * result + (dimension != null ? dimension.hashCode() : 0);
        result = 31 * result + (field != null ? field.hashCode() : 0);
        return result;
    }

    public class BIDimensionCore extends BICoreWrapper {
        public BIDimensionCore() throws Exception {
            super(field,
                    relations,
                    dimension == null ? null : dimension.getGroup(),
                    dimension == null ? null : dimension.getSort());
        }
    }

    public BITableRelationPath getSelfToSelfRelationPath() {
        return dimension.getSelfToSelfRelationPath();
    }


}