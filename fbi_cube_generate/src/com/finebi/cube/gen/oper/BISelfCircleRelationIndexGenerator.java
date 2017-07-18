package com.finebi.cube.gen.oper;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.exception.BICubeIndexException;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.structure.BICubeRelation;
import com.finebi.cube.structure.BICubeRelationEntity;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.structure.CubeTableEntityGetterService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnEntityService;
import com.finebi.cube.utils.BIRelationHelper;
import com.fr.bi.conf.data.source.TableSourceUtils;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.List;
import java.util.Map;

/**
 * Created by Kary on 2017/3/15.
 */
public class BISelfCircleRelationIndexGenerator extends BIRelationIndexGenerator {
    public BISelfCircleRelationIndexGenerator(Cube cube, Cube integrityCube, BICubeRelation relation, Map<String, CubeTableSource> tablesNeed2GenerateMap) {
        super(cube, integrityCube, relation, tablesNeed2GenerateMap);
//        super.setCubeChooser(new CubeCalculatorChooser(cube, integrityCube));
    }


    private List<ICubeFieldSource> getSelfCircleParentFields() throws Exception {
        BITableSourceRelation relation = BIRelationHelper.getTableRelation(this.relation);
        List<ICubeFieldSource> fieldsList = TableSourceUtils.createCircleFieldsList(relation.getForeignField());
        return fieldsList;
    }

    /**
     * 分组值索引
     *
     * @param foreignColumn
     * @param position
     * @return
     * @throws Exception
     */
    @Override
    protected GroupValueIndex getForeignBitmapIndex(ICubeColumnEntityService foreignColumn, int position) throws BICubeIndexException {
        GroupValueIndex selfCircleGVI = foreignColumn.getBitmapIndex(position);
        final CubeTableEntityGetterService foreignTable = cubeChooser.getCubeTable(relation.getForeignTable());
        if (TableSourceUtils.isSelfCirCleSource(BIRelationHelper.getTableRelation(this.relation).getForeignTable())) {
            try {
                for (ICubeFieldSource fieldSource : getSelfCircleParentFields()) {
                    BIColumnKey indexKey = BIColumnKey.covertColumnKey(fieldSource);
                    ICubeColumnEntityService currentColumn = (ICubeColumnEntityService) foreignTable.getColumnDataGetter(indexKey);
                    GroupValueIndex gvi = currentColumn.getIndexByGroupValue(foreignColumn.getGroupObjectValue(position));
                    selfCircleGVI = selfCircleGVI.or(gvi);
                    //没必要继续往下走了
                    if (selfCircleGVI.getRowsCountWithData() == foreignTable.getRowCount()) {
                        break;
                    }
                }
            } catch (Exception e) {
                BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
            }
        }
        return selfCircleGVI;
    }

    protected void buildNullIndex(BICubeRelationEntity tableRelation, GroupValueIndex nullIndex) {
        nullIndex = GVIFactory.createAllEmptyIndexGVI();
        tableRelation.addRelationNULLIndex(0, nullIndex);
    }
}