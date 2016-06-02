package com.fr.bi.etl.analysis.data;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
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
    public UserETLTableSource(List<IETLOperator> operators, List<UserCubeTableSource> parents, long userId) {
        super(operators, parents);
        this.userId = userId;
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
                for (int i = 0; i < ti.getRowCount(); i++) {
                    for (int j = 0; j < fields.size(); j++) {
                        travel.actionPerformed(new BIDataValue(i, j, ti.getRow(new IndexKey(fields.get(j).getFieldName()), i)));
                    }
                }
                startCol += p.getPersistentTable().getFieldSize();
            }
        }
        Iterator<IETLOperator> it = oprators.iterator();
        long index = 0;
        while (it.hasNext()) {
            IETLOperator op = it.next();
            index = op.writePartIndex(travel, parents, loader, startCol, 0, Integer.MAX_VALUE);
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

    /**
     * @return

     */
    @Override
    public Set<String> getSourceUsedMD5() {
        Set<String> set = new HashSet<String>();
        for (UserCubeTableSource source : getParents()){
            if (source.getType() == Constants.TABLE_TYPE.USER_BASE){
                set.addAll(source.getSourceUsedMD5());
            }
        }
        return set;
    }

    @Override
    public boolean containsIDParentsWithMD5(String md5) {
        for (UserCubeTableSource source : getParents()){
            if (ComparatorUtils.equals(md5, source.fetchObjectCore().getID().getIdentityValue())){
                return true;
            }
            if (source.containsIDParentsWithMD5(md5)){
                return true;
            }
        }
        return false;
    }
}