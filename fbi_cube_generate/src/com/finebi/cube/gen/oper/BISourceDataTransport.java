package com.finebi.cube.gen.oper;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.utils.BILogHelper;
import com.finebi.cube.exception.BICubeResourceAbsentException;
import com.finebi.cube.impl.pubsub.BIProcessor;
import com.finebi.cube.impl.pubsub.BIProcessorThreadManager;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.meta.BILocationInfo;
import com.finebi.cube.location.meta.BILocationPool;
import com.finebi.cube.location.provider.BILocationProvider;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.structure.CubeTableEntityService;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.conf.data.source.BIOccupiedCubeTableSource;
import com.fr.bi.conf.data.source.ETLTableSource;
import com.fr.bi.manager.PerformancePlugManager;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    /**
     * @param tempRetrieval
     * @param advancedRetrieval
     */
    private void copyWithFileUtils(BICubeResourceRetrieval tempRetrieval, BICubeResourceRetrieval advancedRetrieval) {
        try {
            ICubeResourceLocation from = advancedRetrieval.retrieveResource(new BITableKey(tableSource.getSourceID()));
            from = from.buildChildLocation("version");
            ICubeResourceLocation to = tempRetrieval.retrieveResource(new BITableKey(tableSource.getSourceID()));
            to = to.buildChildLocation("version");
            BIFileUtils.copyFolder(new File(from.getRealLocation().getAbsolutePath()).getParentFile(), new File(to.getRealLocation().getAbsolutePath()).getParentFile());
        } catch (IOException e) {
            BILoggerFactory.getLogger(BISourceDataTransport.class).error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            BILoggerFactory.getLogger(BISourceDataTransport.class).error(e.getMessage(), e);
        } catch (BICubeResourceAbsentException e) {
            BILoggerFactory.getLogger(BISourceDataTransport.class).error(e.getMessage(), e);
        }
        updateCubeLocationManager();
    }

    /**
     * copy的uri在当前cube的locationManager中注册
     * 避免替换cube路径时无法更新新路径（nevertansport）
     */
    private void updateCubeLocationManager() {
        ICubeConfiguration advancedConf = BICubeConfiguration.getConf(String.valueOf(UserControl.getInstance().getSuperManagerID()));
        ArrayList<String> searchParas = new ArrayList<String>();
        searchParas.add(String.valueOf(UserControl.getInstance().getSuperManagerID()));
        searchParas.add(tableSource.getSourceID());
//        table对应的所有文件资源
        BILocationPool srcLocations = advancedConf.getLocationProvider().getAccessLocationPool(searchParas, new ArrayList<String>());
//      目标cube的folder
        BILocationProvider cubeLocationManager = cube.getCubeResourceRetrievalService().getCubeConfiguration().getLocationProvider();
        for (BILocationInfo info : srcLocations.getAllItems()) {
            try {
//                将copy路径注册进当前cube的路径管理器中
                cubeLocationManager.getRealLocation(info.getLogicFolder(), info.getChild());
            } catch (URISyntaxException e) {
                BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(),e);
            }
        }
    }

    /**
     *
     */
    private void copyWithFineIO() {
        ICubeConfiguration advancedConf = BICubeConfiguration.getConf(String.valueOf(UserControl.getInstance().getSuperManagerID()));
        ArrayList<String> searchParas = new ArrayList<String>();
        searchParas.add(String.valueOf(UserControl.getInstance().getSuperManagerID()));
        searchParas.add(tableSource.getSourceID());
//        table对应的所有文件资源
        BILocationPool srcLocations = advancedConf.getLocationProvider().getAccessLocationPool(searchParas, new ArrayList<String>());
        updateCubeLocationManager();
        BILocationPool dstLocations = cube.getCubeResourceRetrievalService().getCubeConfiguration().getLocationProvider().getAccessLocationPool(searchParas, new ArrayList<String>());
        for (BILocationInfo info : srcLocations.getAllItems()) {
            try {
                //          src 路径  /cubeFolder/userid/tableid/.../fileName
                URI srcURI = new URI(info.getRealPath());
                //            dst 路径
                URI dstURI = new URI(dstLocations.getResourceItem(info.getName().value()).getRealPath());
                // TODO: 2017/8/17
//                FineIO.copy(srcURI,dstURI);
            } catch (URISyntaxException e) {
                BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(),e);
            }
        }
    }

    protected void copyFromOldCubes() {
        long t = System.currentTimeMillis();
        ICubeConfiguration tempConf = cube.getCubeResourceRetrievalService().getCubeConfiguration();
        ICubeConfiguration advancedConf = BICubeConfiguration.getConf(String.valueOf(UserControl.getInstance().getSuperManagerID()));
        BICubeResourceRetrieval tempRetrieval = new BICubeResourceRetrieval(tempConf);
        BICubeResourceRetrieval advancedRetrieval = new BICubeResourceRetrieval(advancedConf);
        if (PerformancePlugManager.getInstance().isUseFineIO()) {
            copyWithFineIO();
        } else {
            copyWithFileUtils(tempRetrieval, advancedRetrieval);
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