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
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.BIColumn;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.db.DBTable;
import com.fr.bi.stable.data.source.AbstractCubeTableSource;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.data.source.SourceFile;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.utils.BICollectionUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;

import java.util.*;

/**
 * Created by 小灰灰 on 2015/12/23.
 */
public abstract class AbstractETLTableSource<O extends IETLOperator, S extends ITableSource> extends AbstractCubeTableSource {

    public static final String XML_TAG = "ETLTableSource";

    /**
     * 所有的source,包括parents
     *
     * @return
     */
    @Override
    public Map<BICore, ITableSource> createSourceMap() {
        Map<BICore, ITableSource> map = super.createSourceMap();
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
    public long read4Part(Traversal<BIDataValue> travel, DBField[] field, ICubeDataLoader loader, int start, int end) {
        int startCol = 0;
        if (isAllAddColumnOperator()) {
            for (ITableSource p : getParents()) {
                ICubeTableService ti = loader.getTableIndex(p.fetchObjectCore(), start, end);
                BIColumn[] fields = p.getDbTable().getColumnArray();
                for (int i = 0; i < ti.getRowCount(); i++) {
                    for (int j = 0; j < fields.length; j++) {
                        travel.actionPerformed(new BIDataValue(i, j, ti.getRow(new IndexKey(fields[j].getFieldName()), i)));
                    }
                }
                startCol += p.getDbTable().getBIColumnLength();
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
    public Set<DBField> getFacetFields(Set<ITableSource> sources) {
        Set<DBField> result = new HashSet<DBField>();
        Iterator<BIColumn> it = getDbTable().getBIColumnIterator();
        while (it.hasNext()) {
            BIColumn column = it.next();
            result.add(column.toDBField(new BITable(this.getSourceID())));
        }
        return result;
    }

    @Override
    public Set<DBField> getParentFields(Set<ITableSource> sources) {
        Set<DBField> result = new HashSet<DBField>();
        for (ITableSource tableSource : parents) {
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
    public Set<Table> createTableKeys() {
        Set set = new HashSet();
        if (!hasTableFilterOperator()) {
            for (ITableSource source : createSourceSet()) {
                set.add(new BITable(source.fetchObjectCore().getID().getIdentityValue()));
            }
        }
        if (isAllAddColumnOperator() || hasTableFilterOperator()) {
            Iterator<S> it = parents.iterator();
            while (it.hasNext()) {
                set.addAll(it.next().createTableKeys());
            }
        }
        return set;
    }

    @Override
    public Map<Integer, Set<ITableSource>> createGenerateTablesMap() {
        Map<Integer, Set<ITableSource>> generateTable = new HashMap<Integer, Set<ITableSource>>();
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
    public List<Set<ITableSource>> createGenerateTablesList() {
        List<Set<ITableSource>> generateTable = new ArrayList<Set<ITableSource>>();
        Set<ITableSource> operators = createSourceSet();
        generateTable.add(operators);
        if (operators.size() > 1) {
            Set<ITableSource> self = new HashSet<ITableSource>();
            BIOccupiedTableSource tableSource = new BIOccupiedTableSource(this.getSourceID());
            self.add(tableSource);
            generateTable.add(self);
        }
        Iterator<S> it = parents.iterator();
        while (it.hasNext()) {
            List<Set<ITableSource>> parent = it.next().createGenerateTablesList();
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
    public long read(Traversal<BIDataValue> travel, DBField[] field, ICubeDataLoader loader) {
        return 0;
    }

    protected Set<ITableSource> createSourceSet() {
        Set<ITableSource> set = new HashSet<ITableSource>();
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
    public DBTable getDbTable() {
        if (dbTable == null) {
            dbTable = createBITable();

            if (isAllAddColumnOperator()) {
                for (S source : parents) {
                    DBTable p = source.getDbTable();
                    for (int i = 0; i < p.getBIColumnLength(); i++) {
                        dbTable.addColumn(p.getBIColumn(i));
                    }
                }
            }
            DBTable[] ptables = new DBTable[parents.size()];
            for (int i = 0; i < ptables.length; i++) {
                ptables[i] = parents.get(i).getDbTable();
            }
            for (int i = 0; i < oprators.size(); i++) {
                DBTable ctable = oprators.get(i).getBITable(ptables);
                Iterator<BIColumn> it = ctable.getBIColumnIterator();
                while (it.hasNext()) {
                    BIColumn column = it.next();
                    dbTable.addColumn(column);
                }
            }
        }
        return dbTable;
    }

    @Override
    public DBField[] getFieldsArray(Set<ITableSource> sources) {
        if (hasTableFilterOperator()) {
            return new DBField[0];
        } else {
            return super.getFieldsArray(sources);
        }
    }

    @Override
    public Set<String> getUsedFields(ITableSource source) {
        Set<String> useableFields = new HashSet<String>();
        boolean contains = false;
        for (ITableSource source1 : parents) {
            if (ComparatorUtils.equals(source1, source)) {
                contains = true;
            }
            useableFields.addAll(source1.getUsedFields(source));
        }
        if (contains) {
            if (hasTableFilterOperator()) {
                DBTable[] ptables = new DBTable[parents.size()];
                for (int i = 0; i < ptables.length; i++) {
                    ptables[i] = parents.get(i).getDbTable();
                }
                for (IETLOperator operator : getETLOperators()) {
                    if (ComparatorUtils.equals(operator.xmlTag(), TableFilterOperator.XML_TAG)) {
                        DBTable table = operator.getBITable(ptables);
                        for (int j = 0; j < table.getBIColumnLength(); j++) {
                            useableFields.add(table.getBIColumn(j).getFieldName());
                        }
                    }
                }
            } else {
                DBTable table = source.getDbTable();
                for (int j = 0; j < table.getBIColumnLength(); j++) {
                    useableFields.add(table.getBIColumn(j).getFieldName());
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
                BICore core = new SingleOpratorETLTableSource((List<ITableSource>) getParents(), operator).fetchObjectCore();
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