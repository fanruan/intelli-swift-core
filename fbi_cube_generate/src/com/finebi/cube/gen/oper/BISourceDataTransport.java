package com.finebi.cube.gen.oper;

import com.finebi.cube.impl.pubsub.BIProcessor;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.structure.CubeTableEntityService;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.general.ComparatorUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class created on 2016/4/5.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BISourceDataTransport extends BIProcessor {
    protected CubeTableSource tableSource;
    protected Set<CubeTableSource> allSources;
    protected CubeTableEntityService tableEntityService;
    protected Cube cube;
    protected List<ITableKey> parents = new ArrayList<ITableKey>();
    protected long version = 0;

    public BISourceDataTransport(Cube cube, CubeTableSource tableSource, Set<CubeTableSource> allSources, Set<CubeTableSource> parentTableSource, long version) {
        this.tableSource = tableSource;
        this.allSources = allSources;
        this.cube = cube;
        tableEntityService = cube.getCubeTableWriter(BITableKeyUtils.convert(tableSource));
        this.version = version;
        initialParents(parentTableSource);
    }


    private void initialParents(Set<CubeTableSource> parentTableSource) {
        if (parentTableSource != null) {
            for (CubeTableSource tableSource : parentTableSource) {
                parents.add(new BITableKey(tableSource));
            }
        }
    }

    

    @Override
    public void release() {
        tableEntityService.clear();
    }

    protected void recordTableInfo() {
        ICubeFieldSource[] columns = getFieldsArray();
        List<ICubeFieldSource> columnList = new ArrayList<ICubeFieldSource>();
        for (ICubeFieldSource col : columns) {
            columnList.add(convert(col));
        }
        tableEntityService.recordTableStructure(columnList);
        if (!tableSource.isIndependent()) {
            tableEntityService.recordParentsTable(parents);
            tableEntityService.recordFieldNamesFromParent(getParentFieldNames());
        }
    }

    private Set<String> getParentFieldNames() {
        Set<ICubeFieldSource> parentFields = tableSource.getParentFields(allSources);
        Set<ICubeFieldSource> facetFields = tableSource.getFacetFields(allSources);
        Set<ICubeFieldSource> selfFields = tableSource.getSelfFields(allSources);
        Set<String> fieldNames = new HashSet<String>();
        for (ICubeFieldSource field : parentFields) {
            if (!containSameName(selfFields, field.getFieldName()) && containSameName(facetFields, field.getFieldName())) {
                fieldNames.add(field.getFieldName());
            }
        }
        return fieldNames;
    }

    private boolean containSameName(Set<ICubeFieldSource> set, String fieldName) {
        for (ICubeFieldSource field : set) {
            if (ComparatorUtils.equals(field.getFieldName(), fieldName)) {
                return true;
            }
        }
        return false;
    }

    

    private ICubeFieldSource convert(ICubeFieldSource column) {
        return new BICubeFieldSource(tableSource, column.getFieldName(), column.getClassType(), column.getFieldSize());
    }

    private ICubeFieldSource[] getFieldsArray() {
        return tableSource.getFieldsArray(allSources);
    }

}
