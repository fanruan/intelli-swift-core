package com.finebi.cube.gen.oper;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.utils.BILogHelper;
import com.finebi.cube.impl.pubsub.BIProcessor;
import com.finebi.cube.impl.pubsub.BIProcessorThreadManager;
import com.finebi.cube.location.BICubeLocation;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.structure.CubeTableEntityService;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.conf.data.source.BIOccupiedCubeTableSource;
import com.fr.bi.conf.data.source.ETLTableSource;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

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
    protected CubeChooser cubeChooser;
    protected List<ITableKey> parents = null;
    protected long version = 0;

    public BISourceDataTransport(Cube cube, CubeTableSource tableSource, Set<CubeTableSource> allSources, Set<CubeTableSource> parentTableSource, long version) {
        this.tableSource = tableSource;
        this.allSources = allSources;
        this.cube = cube;
        tableEntityService = cube.getCubeTableWriter(BITableKeyUtils.convert(tableSource));
        this.version = version;
        initThreadPool();
    }

    public BISourceDataTransport(Cube cube, Cube integrityCube, CubeTableSource tableSource, Set<CubeTableSource> allSources, Set<CubeTableSource> parentTableSource, long version, Map<String, CubeTableSource> tablesNeed2GenerateMap) {
        this.tableSource = tableSource;
        this.allSources = allSources;
        this.cube = cube;
        this.cubeChooser = new CubeChooser(cube, integrityCube, tablesNeed2GenerateMap);
        tableEntityService = cube.getCubeTableWriter(BITableKeyUtils.convert(tableSource));
        this.version = version;
        initThreadPool();
    }


    @Override
    protected void initThreadPool() {
        this.executorService = BIProcessorThreadManager.getInstance().getTransportExecutorService();
    }

    @Override
    public void release() {
        tableEntityService.forceReleaseWriter();
        tableEntityService.clear();
    }

    protected void buildTableBasicStructure() {
        tableEntityService.buildStructure();
    }

    protected void recordTableInfo() {
        fieldsCheck();
        ICubeFieldSource[] columns = getFieldsArray();
        List<ICubeFieldSource> columnList = new ArrayList<ICubeFieldSource>();
        for (ICubeFieldSource col : columns) {
            columnList.add(convert(col));
        }
        tableEntityService.recordTableStructure(columnList);
        if (!tableSource.isIndependent()) {
            tableEntityService.recordParentsTable(getParents(this.tableSource));
            tableEntityService.recordFieldNamesFromParent(getParentFieldNames());
        }
    }


    protected boolean checkFields() {
        return tableSource.hasAbsentFields();
    }

    private List<ITableKey> getParents(CubeTableSource tableSource) {
        if (parents == null) {
            parents = new ArrayList<ITableKey>();
            if (!tableSource.isIndependent()) {
                if (tableSource instanceof ETLTableSource) {
                    ETLTableSource etlTableSource = (ETLTableSource) tableSource;
                    for (CubeTableSource parent : etlTableSource.getParents()) {
                        parents.add(new BITableKey(parent));
                    }
                } else if (tableSource instanceof BIOccupiedCubeTableSource) {
                    BIOccupiedCubeTableSource ocTableSource = (BIOccupiedCubeTableSource) tableSource;
                    for (CubeTableSource parent : ocTableSource.getParents()) {
                        parents.add(new BITableKey(parent));
                    }
                }
            }
        }
        return parents;
    }

    protected void copyFromOldCubes() {
        long t = System.currentTimeMillis();
        ICubeConfiguration tempConf = BICubeConfiguration.getTempConf(String.valueOf(UserControl.getInstance().getSuperManagerID()));
        ICubeConfiguration advancedConf = BICubeConfiguration.getConf(String.valueOf(UserControl.getInstance().getSuperManagerID()));
        try {
            ICubeResourceLocation from = new BICubeLocation(advancedConf.getRootURI().getPath().toString(), tableSource.getSourceID(), advancedConf.getLocationProvider());
            ICubeResourceLocation to = new BICubeLocation(tempConf.getRootURI().getPath().toString(), tableSource.getSourceID(), advancedConf.getLocationProvider());
            BIFileUtils.copyFolder(new File(from.getRealLocation().getAbsolutePath()), new File(to.getRealLocation().getAbsolutePath()));
        } catch (IOException e) {
            BILoggerFactory.getLogger(BISourceDataTransport.class).error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            BILoggerFactory.getLogger(BISourceDataTransport.class).error(e.getMessage(), e);
        }
        BILoggerFactory.getLogger().info("table name: " + tableSource.getTableName() + " update copy files cost time:" + DateUtils.timeCostFrom(t));
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

    private boolean fieldsCheck() {
        boolean flag = tableSource.hasAbsentFields();
        if (flag) {
            BILoggerFactory.getLogger(this.getClass()).warn(BIStringUtils.append("the table: ", tableSource.getTableName(), "tableId: ", tableSource.getSourceID(), "may has some absent fields"));
        }
        return flag;
    }

    protected String fetchTableInfo() {
        return BILogHelper.logTableSource(tableSource, " ");
    }

    @Override
    public void handleMessage(IMessage receiveMessage) {

    }
}