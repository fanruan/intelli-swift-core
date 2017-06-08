package com.fr.bi.etl.analysis.data;

import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.AbstractETLTableSource;
import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.etl.analysis.monitor.SimpleTable;
import com.fr.bi.etl.analysis.monitor.TableRelationTree;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.general.ComparatorUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/24.
 */
public class UserETLTableSource extends AbstractETLTableSource<IETLOperator, UserCubeTableSource> implements UserCubeTableSource {
    private static final long serialVersionUID = -5120502339337403130L;
    private long userId;
    @BICoreField
    private List<AnalysisETLSourceField> fieldList;
    private AnalysisETLTableSource parent;

    public UserETLTableSource(AnalysisETLTableSource parent,  List<UserCubeTableSource> parents, long userId) {
        super(parent.getETLOperators(), parents);
        this.parent = parent;
        this.userId = userId;
        this.fieldList = parent.getFieldsList();
    }

    public void getParentAnalysisBaseTableIds(Set<SimpleTable> set) {
        if(parent != null){
            parent.getParentAnalysisBaseTableIds(set);
        }
    }

    public TableRelationTree getAllProcessAnalysisTablesWithRelation() {
        if(parent != null){
            return parent.getAllProcessAnalysisTablesWithRelation();
        }
        return TableRelationTree.EMPTY;
    }

    @Override
    public void resetTargetsMap() {

    }

    @Override
    public int getType() {
        return BIBaseConstant.TABLE_TYPE.USER_ETL;
    }

    /**
     * 写简单索引
     *
     * @param travel
     * @param field
     * @param loader
     * @return
     */
    @Override
    public long read(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader) {
        int startCol = 0;
        if (isAllAddColumnOperator()) {
            for (CubeTableSource p : getParents()) {
                ICubeTableService ti = loader.getTableIndex(p);
                List<PersistentField> fields = p.getPersistentTable().getFieldList();
                List<ICubeColumnDetailGetter> getters = new ArrayList<ICubeColumnDetailGetter>();
                for (PersistentField f : fields){
                    getters.add(ti.getColumnDetailReader(new IndexKey(f.getFieldName())));
                }
                for (int i = 0; i < ti.getRowCount(); i++) {
                    for (int j = 0; j < getters.size(); j++) {
                        travel.actionPerformed(new BIDataValue(i, j, getters.get(j).getValue(i)));
                    }
                }
                startCol += p.getPersistentTable().getFieldSize();
            }
        }
        Iterator<IETLOperator> it = oprators.iterator();
        long index = 0;
        while (it.hasNext()) {
            IETLOperator op = it.next();
            index = op.writeIndexWithParents(travel, parents, loader, startCol);
            startCol++;
        }
        return index;
    }

    @Override
    public Map<Integer, Set<CubeTableSource>> createGenerateTablesMap() {
        Map<Integer, Set<CubeTableSource>> generateTable = new HashMap<Integer, Set<CubeTableSource>>();
        generateTable.put(getLevel(), createSourceSet());
        return generateTable;
    }

    /**
     * @return
     */
    public long getUserId() {
        return userId;
    }


    public boolean containsIDParentsWithMD5(String md5, long userId) {
        for (UserCubeTableSource source : getParents()){
            if (ComparatorUtils.equals(md5, source.fetchObjectCore().getID().getIdentityValue())){
                return true;
            }
            if (source.containsIDParentsWithMD5(md5, userId)){
                return true;
            }
        }
        return ComparatorUtils.equals(md5, fetchObjectCore().getIDValue());
    }

    public AnalysisCubeTableSource getAnalysisCubeTableSource() {
        return parent;
    }

    public boolean isParentAvailable() {
        List<UserCubeTableSource>  parents = getParents();
        for(UserCubeTableSource source : parents) {
            if(!BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().isAvailable(source, new BIUser(userId))) {
                return  false;
            }
        }
        return true;
    }


    public Set<CubeTableSource> getParentSource() {
        Set<CubeTableSource> set = new HashSet<CubeTableSource>();
        List<UserCubeTableSource>  parents = getParents();
        for(UserCubeTableSource source : parents) {
            set.add(source);
        }
        return set;
    }

    @Override
    public Set<CubeTableSource> getSourceUsedBaseSource(Set<CubeTableSource> set, Set<CubeTableSource> helper) {
        if(helper.contains(parent)){
            return set;
        }
        helper.add(parent);
        set.add(parent);
        for (CubeTableSource source : getParents()){
            source.getSourceUsedBaseSource(set, helper);
        }
        return set;
    }

    @Override
    public String getModuleName() {
        return BIBaseConstant.MODULE_NAME.ANALYSIS_ETL_MODULE;
    }

    public void getSourceUsedAnalysisETLSource(Set<AnalysisCubeTableSource> set) {
        if(set.contains(this)){
            return;
        }
        for (UserCubeTableSource source : getParents()){
            source.getSourceUsedAnalysisETLSource(set);
            set.add(source);
        }
        set.add(this);
    }

    public void getSourceNeedCheckSource(Set<AnalysisCubeTableSource> set){
        if(set.contains(this)){
            return;
        }
        for (UserCubeTableSource source : getParents()){
            source.getSourceNeedCheckSource(set);
        }
        set.add(this);
    }

    public void refreshWidget() {
        for (AnalysisCubeTableSource source : getParents()){
            source.refreshWidget();
        }
    }

    public Set<BIWidget> getWidgets() {
        return new HashSet<BIWidget>();
    }
    public void reSetWidgetDetailGetter() {
        for (AnalysisCubeTableSource source : getParents()){
            source.reSetWidgetDetailGetter();
        }
    }

    public UserCubeTableSource createUserTableSource(long userId) {
        return this;
    }

    public List<AnalysisETLSourceField> getFieldsList() {
        return fieldList;
    }
}