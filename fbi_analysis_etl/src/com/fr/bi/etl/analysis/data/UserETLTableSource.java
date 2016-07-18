package com.fr.bi.etl.analysis.data;

import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.AbstractETLTableSource;
import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.db.PersistentField;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.general.ComparatorUtils;

import java.util.*;

/**
 * Created by 小灰灰 on 2015/12/24.
 */
public class UserETLTableSource extends AbstractETLTableSource<IETLOperator, UserCubeTableSource> implements UserCubeTableSource {
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

    @Override
    public int getType() {
        return Constants.TABLE_TYPE.USER_ETL;
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
    @Override
    public long getUserId() {
        return userId;
    }


    @Override
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

    @Override
    public AnalysisCubeTableSource getAnalysisCubeTableSource() {
        return parent;
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

    @Override
    public void refreshWidget() {
        for (AnalysisCubeTableSource source : getParents()){
            source.refreshWidget();
        }
    }

    @Override
    public UserCubeTableSource createUserTableSource(long userId) {
        return this;
    }

    @Override
    public List<AnalysisETLSourceField> getFieldsList() {
        return fieldList;
    }
}