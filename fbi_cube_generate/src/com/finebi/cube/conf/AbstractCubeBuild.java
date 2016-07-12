package com.finebi.cube.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.impl.conf.CubePreConditionsCheckManager;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.io.File;
import java.util.*;

/**
 * Created by wuk on 16/7/11.
 */
public abstract class AbstractCubeBuild implements CubeBuild {
    private long userId;
    protected Set<CubeTableSource> sources = new HashSet<CubeTableSource>();
    private Set allBusinessTable = new HashSet<BIBusinessTable>();
    protected Map<CubeTableSource, Map<String, ICubeFieldSource>> tableDBFieldMaps = new HashMap<CubeTableSource, Map<String, ICubeFieldSource>>();
    public AbstractCubeBuild(long userId) {
        this.userId=userId;
        allBusinessTable = BICubeConfigureCenter.getPackageManager().getAllTables(userId);
        setSources();
        fullTableDBFields();
    }

    @Override
    public ICubeConfiguration getCubeConfiguration() {
        return  BICubeConfiguration.getTempConf(Long.toString(userId));
    }
    protected Set<List<Set<CubeTableSource>>> calculateTableSource(Set<CubeTableSource> tableSources) {
        Iterator<CubeTableSource> it = tableSources.iterator();
        Set<List<Set<CubeTableSource>>> depends = new HashSet<List<Set<CubeTableSource>>>();
        while (it.hasNext()) {
            CubeTableSource tableSource = it.next();
            depends.add(tableSource.createGenerateTablesList());
        }
        return depends;
    }

    @Override
    public Map<CubeTableSource, Long> getVersions() {
        Set<CubeTableSource> allTable = getAllSingleSources();
        Map<CubeTableSource, Long> result = new HashMap<CubeTableSource, Long>();
        Long version = System.currentTimeMillis();
        for (CubeTableSource table : allTable) {
            result.put(table, version);
        }
        return result;
    }

    @Override
    public boolean preConditionsCheck() {
        CubePreConditionsCheck check = new CubePreConditionsCheckManager();
        File cubeFile = new File(BIPathUtils.createBasePath());
        boolean spaceCheck = check.HDSpaceCheck(cubeFile);
        boolean connectionCheck = check.ConnectionCheck();
        return spaceCheck && connectionCheck;
    }

    @Override
    public Set<CubeTableSource> getSources() {
        return this.sources;
    }

    public static Set<CubeTableSource> set2Set(Set<List<Set<CubeTableSource>>> set) {
        Set<CubeTableSource> result = new HashSet<CubeTableSource>();
        Iterator<List<Set<CubeTableSource>>> outIterator = set.iterator();
        while (outIterator.hasNext()) {
            Iterator<Set<CubeTableSource>> middleIterator = outIterator.next().iterator();
            while (middleIterator.hasNext()) {
                result.addAll(middleIterator.next());
            }
        }
        return result;
    }

    public void setSources() {
        for (Object biBusinessTable : allBusinessTable) {
            BusinessTable table = (BusinessTable) biBusinessTable;
            sources.add(table.getTableSource());
        }
    }

    private void fullTableDBFields() {
        Iterator<CubeTableSource> tableSourceIterator = sources.iterator();
        while (tableSourceIterator.hasNext()) {
            CubeTableSource tableSource = tableSourceIterator.next();
            ICubeFieldSource[] BICubeFieldSources = tableSource.getFieldsArray(sources);
            Map<String, ICubeFieldSource> name2Field = new HashMap<String, ICubeFieldSource>();
            for (int i = 0; i < BICubeFieldSources.length; i++) {
                ICubeFieldSource field = BICubeFieldSources[i];
                name2Field.put(field.getFieldName(), field);
            }
            tableDBFieldMaps.put(tableSource, name2Field);
        }
    }

    protected BITableSourceRelation convert(BITableRelation relation) {


        CubeTableSource primaryTable;
        CubeTableSource foreignTable;
        try {
            primaryTable = BICubeConfigureCenter.getDataSourceManager().getTableSource(relation.getPrimaryField().getTableBelongTo());
            foreignTable = BICubeConfigureCenter.getDataSourceManager().getTableSource(relation.getForeignField().getTableBelongTo());
        } catch (BIKeyAbsentException e) {
            throw BINonValueUtils.beyondControl(e);
        }
        ICubeFieldSource primaryField = tableDBFieldMaps.get(primaryTable).get(relation.getPrimaryField().getFieldName());
        ICubeFieldSource foreignField = tableDBFieldMaps.get(foreignTable).get(relation.getForeignField().getFieldName());
        if (primaryField == null || foreignField == null) {
            throw new NullPointerException();
        }
        primaryField.setTableBelongTo(primaryTable);
        foreignField.setTableBelongTo(foreignTable);
        return new BITableSourceRelation(
                primaryField,
                foreignField,
                primaryTable,
                foreignTable
        );
    }
    
}
