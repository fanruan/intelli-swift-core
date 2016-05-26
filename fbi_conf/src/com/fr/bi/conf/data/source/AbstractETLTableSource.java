package com.fr.bi.conf.data.source;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.BIBasicCore;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.conf.data.source.operator.create.TableFilterOperator;
import com.fr.bi.conf.data.source.operator.create.UsePartOperator;
import com.fr.bi.stable.data.db.*;
import com.fr.bi.stable.data.source.AbstractCubeTableSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.data.source.SourceFile;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;

import java.util.*;

/**
 * Created by 小灰灰 on 2015/12/23.
 */
public abstract class AbstractETLTableSource<O extends IETLOperator, S extends CubeTableSource> extends AbstractCubeTableSource {

    public static final String XML_TAG = "ETLTableSource";

    /**
     * 所有的source,包括parents
     *
     * @return
     */
    @Override
    public Map<BICore, CubeTableSource> createSourceMap() {
        Map<BICore, CubeTableSource> map = super.createSourceMap();
        for (S parent : getParents()) {
            map.putAll(parent.createSourceMap());
        }
        return map;
    }

    /**
     *
     */
    private static final long serialVersionUID = -4709748792691267870L;
    @BICoreField
    protected List<O> oprators = new ArrayList<O>();
    @BICoreField
    protected List<S> parents = new ArrayList<S>();

    public AbstractETLTableSource() {
        super();
    }

    public AbstractETLTableSource(List<O> operators, List<S> parents) {
        this.oprators = operators;
        this.parents = parents;
    }

    public void setOperators(List<O> operators) {
        this.oprators = operators;
    }

    public O getETLOperator(BICore core) {
        for (int i = 0; i < oprators.size(); i++) {
            if (ComparatorUtils.equals(oprators.get(i).fetchObjectCore(), core)) {
                return oprators.get(i);
            }
        }
        return null;
    }

    public List<O> getETLOperators() {
        return oprators;
    }


    public void setParents(List<S> parents) {
        this.parents = parents;
    }

    @Override
    public long read4Part(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader, int start, int end) {
        int startCol = 0;
        if (isAllAddColumnOperator()) {
            for (CubeTableSource p : getParents()) {
                ICubeTableService ti = loader.getTableIndex(p, start, end);
                List<PersistentField> fields = p.getPersistentTable().getFieldList();
                for (int i = 0; i < ti.getRowCount(); i++) {
                    for (int j = 0; j < fields.size(); j++) {
                        travel.actionPerformed(new BIDataValue(i, j, ti.getRow(new IndexKey(fields.get(j).getFieldName()), i)));
                    }
                }
                startCol += p.getPersistentTable().getFieldSize();
            }
        }
        Iterator<O> it = oprators.iterator();
        long index = 0;
        while (it.hasNext()) {
            IETLOperator op = it.next();
            index = op.writePartIndex(travel, parents, loader, startCol, start, end);
            startCol++;
        }
        return index;
    }

    @Override
    public Set<ICubeFieldSource> getFacetFields(Set<CubeTableSource> sources) {
        Set<ICubeFieldSource> result = new HashSet<ICubeFieldSource>();
        Iterator<PersistentField> it = getPersistentTable().getFieldList().iterator();
        while (it.hasNext()) {
            PersistentField column = it.next();
            result.add(column.toDBField(this));
        }
        return result;
    }

    @Override
    public Set<ICubeFieldSource> getParentFields(Set<CubeTableSource> sources) {
        Set<ICubeFieldSource> result = new HashSet<ICubeFieldSource>();
        for (CubeTableSource tableSource : parents) {
            result.addAll(tableSource.getFacetFields(sources));
        }
        return result;
    }

    @Override
    public BICore fetchObjectCore() {
        try {
            return new BICoreGenerator(this).fetchObjectCore();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return BIBasicCore.EMPTY_CORE;
    }


    @Override
    public Map<Integer, Set<CubeTableSource>> createGenerateTablesMap() {
        Map<Integer, Set<CubeTableSource>> generateTable = new HashMap<Integer, Set<CubeTableSource>>();
        generateTable.put(getLevel(), createSourceSet());
        Iterator<S> it = parents.iterator();
        while (it.hasNext()) {
            BICollectionUtils.mergeSetValueMap(generateTable, it.next().createGenerateTablesMap());
        }
        return generateTable;
    }

    @Override
    public boolean isIndependent() {
        return !(hasTableFilterOperator() || isAllAddColumnOperator());
    }

    @Override
    public List<Set<CubeTableSource>> createGenerateTablesList() {
        List<Set<CubeTableSource>> generateTable = new ArrayList<Set<CubeTableSource>>();
        Set<CubeTableSource> operators = createSourceSet();
        generateTable.add(operators);
        if (operators.size() > 1) {
            Set<CubeTableSource> self = new HashSet<CubeTableSource>();
            BIOccupiedCubeTableSource tableSource = new BIOccupiedCubeTableSource(this.getSourceID());
            self.add(tableSource);
            generateTable.add(self);
        }
        Iterator<S> it = parents.iterator();
        while (it.hasNext()) {
            List<Set<CubeTableSource>> parent = it.next().createGenerateTablesList();
            if (!parent.isEmpty()) {
                for (int i = 0; i < parent.size(); i++) {
                    generateTable.add(i, parent.get(i));
                }
            }
        }
        return generateTable;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public long read(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader) {
        return 0;
    }

    protected Set<CubeTableSource> createSourceSet() {
        Set<CubeTableSource> set = new HashSet<CubeTableSource>();
        set.add(this);
        return set;
    }

    @Override
    public int getLevel() {
        int r = -1;
        Iterator<S> it = parents.iterator();
        while (it.hasNext()) {
            r = Math.max(r, it.next().getLevel());
        }
        return r + 1;
    }

    @Override
    public IPersistentTable getPersistentTable() {
        if (dbTable == null) {
            dbTable = createBITable();

            if (isAllAddColumnOperator()) {
                for (S source : parents) {
                    IPersistentTable p = source.getPersistentTable();
                    for (int i = 0; i < p.getFieldSize(); i++) {
                        dbTable.addColumn(p.getField(i));
                    }
                }
            }
            IPersistentTable[] ptables = new IPersistentTable[parents.size()];
            for (int i = 0; i < ptables.length; i++) {
                ptables[i] = parents.get(i).getPersistentTable();
            }
            for (int i = 0; i < oprators.size(); i++) {
                IPersistentTable ctable = oprators.get(i).getBITable(ptables);
                Iterator<PersistentField> it = ctable.getFieldList().iterator();
                while (it.hasNext()) {
                    PersistentField column = it.next();
                    dbTable.addColumn(column);
                }
            }
        }
        return dbTable;
    }

    @Override
    public ICubeFieldSource[] getFieldsArray(Set<CubeTableSource> sources) {
        if (hasTableFilterOperator()) {
            return new BICubeFieldSource[0];
        } else {
            return super.getFieldsArray(sources);
        }
    }

    @Override
    public Set<String> getUsedFields(CubeTableSource source) {
        Set<String> useableFields = new HashSet<String>();
        boolean contains = false;
        for (CubeTableSource source1 : parents) {
            if (ComparatorUtils.equals(source1, source)) {
                contains = true;
            }
            useableFields.addAll(source1.getUsedFields(source));
        }
        if (contains) {
            if (hasTableFilterOperator()) {
                IPersistentTable[] ptables = new IPersistentTable[parents.size()];
                for (int i = 0; i < ptables.length; i++) {
                    ptables[i] = parents.get(i).getPersistentTable();
                }
                for (IETLOperator operator : getETLOperators()) {
                    if (ComparatorUtils.equals(operator.xmlTag(), TableFilterOperator.XML_TAG)) {
                        IPersistentTable table = operator.getBITable(ptables);
                        for (int j = 0; j < table.getFieldSize(); j++) {
                            useableFields.add(table.getField(j).getFieldName());
                        }
                    }
                }
            } else {
                IPersistentTable table = source.getPersistentTable();
                for (int j = 0; j < table.getFieldSize(); j++) {
                    useableFields.add(table.getField(j).getFieldName());
                }
            }
        }
        return useableFields;
    }


    @Override
    public boolean needGenerateIndex() {
        return !hasTableFilterOperator();
    }

    @Override
    public SourceFile getSourceFile() {
        SourceFile sourceFile = new SourceFile(fetchObjectCore().getID().getIdentityValue());
        if (oprators.isEmpty() || hasTableFilterOperator()) {
            return getParentsSourceFile();
        }
        if (!isAllAddColumnOperator()) {
            return sourceFile;
        }
        sourceFile.addChild(getParentsSourceFile());
        for (O operator : oprators) {
            try {
                BICore core = new SingleOperatorETLTableSource((List<CubeTableSource>) getParents(), operator).fetchObjectCore();
                sourceFile.addChild(new SourceFile(core.getIDValue()));
            } catch (Exception ignore) {
                BILogger.getLogger().error(ignore.getMessage(), ignore);
            }
        }
        return sourceFile;
    }

    public boolean hasTableFilterOperator() {
        for (O operator : getETLOperators()) {
            if (ComparatorUtils.equals(operator.xmlTag(), TableFilterOperator.XML_TAG) || ComparatorUtils.equals(operator.xmlTag(), UsePartOperator.XML_TAG)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isAllAddColumnOperator() {
        for (O operator : getETLOperators()) {
            if (!operator.isAddColumnOprator()) {
                return false;
            }
        }
        return true;
    }

    private SourceFile getParentsSourceFile() {
        if (parents.size() == 1) {
            return parents.get(0).getSourceFile();
        } else {
            SourceFile sourceFile = new SourceFile(fetchObjectCore().getID().getIdentityValue());
            for (S source : parents) {
                sourceFile.addChild(source.getSourceFile());
            }
            return sourceFile;
        }
    }

    private BICore getParentsCore() {
        if (parents.size() == 1) {
            return parents.get(0).fetchObjectCore();
        }
        BICore biCore = fetchObjectCore();
        biCore.clearCore();
        for (S source : parents) {
            try {
                biCore.registerAttribute(source.fetchObjectCore());
            } catch (Exception ignore) {
                BILogger.getLogger().error(ignore.getMessage(), ignore);
            }
        }
        return biCore;
    }

    public List<S> getParents() {
        return parents;
    }
}