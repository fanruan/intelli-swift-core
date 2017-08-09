/**
 *
 */
package com.fr.bi.etl.analysis.manager;

import com.finebi.common.name.Name;
import com.finebi.common.name.NameImp;
import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.gen.oper.BIFieldIndexGenerator;
import com.finebi.cube.location.BICubeLocation;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.manager.BILocationProvider;
import com.finebi.cube.location.manager.ILocationConverter;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.CubeTableEntityService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.stable.loader.CubeReadingTableIndexLoader;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.etl.analysis.data.UserCubeTableSource;
import com.fr.bi.module.UserETLCubeTILoader;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.structure.queue.AV;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.util.BIConfigurePathUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.core.UUID;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @author Daniel
 */
public class UserETLUpdateTask implements CubeTask, AV {
    private static BILogger logger = BILoggerFactory.getLogger(BICube.class);

    /**
     *
     */
    private static final long serialVersionUID = 8163408533936978327L;
    private String path = UUID.randomUUID().toString();

    private UserCubeTableSource source;

    private Date start;
    private Date end;
    //  螺旋分析B基于A创建，A生成完毕后应强制B重新生成
    private boolean isRebuild = false;
    private BIUser biUser;
    @BIIgnoreField
    protected BICube cube;

    public boolean isRebuild() {
        return isRebuild;
    }

    public void setRebuild(boolean rebuild) {
        isRebuild = rebuild;
    }

    public UserETLUpdateTask(UserCubeTableSource source) {
        this.source = source;
        this.biUser = new BIUser(source.getUserId());
        this.cube = new BICube(new BICubeResourceRetrieval(new ICubeConfiguration() {
            @Override
            public URI getRootURI() {
                try {
                    File file = new File(new BICubeLocation(BIConfigurePathUtils.createUserETLTableBasePath(UserETLUpdateTask.this.source.fetchObjectCore().getID().getIdentityValue()),
                            path, new ILocationConverter() {
                        @Override
                        public ICubeResourceLocation getRealLocation(String path, String child) throws URISyntaxException {
                            return new BICubeLocation(path, child, this);
                        }
                    }).getAbsolutePath());
                    return URI.create(file.toURI().getRawPath());
                } catch (URISyntaxException e) {
                    throw BINonValueUtils.beyondControl(e);
                }
            }

            @Override
            public BILocationProvider getLocationProvider() {
                return null;
            }
        }), BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
    }

    @Override
    public void run() {
        final CubeTableEntityService tableEntityService = cube.getCubeTableWriter(BITableKeyUtils.convert(this.source));
        ICubeFieldSource[] columns = this.source.getFieldsArray(new HashSet<CubeTableSource>());
        List<ICubeFieldSource> columnList = new ArrayList<ICubeFieldSource>();
        for (ICubeFieldSource col : columns) {
            columnList.add(new BICubeFieldSource(this.source, col.getFieldName(), col.getClassType(), col.getFieldSize()));
        }
        tableEntityService.recordTableStructure(columnList);
        List<ICubeFieldSource> fieldList = tableEntityService.getFieldInfo();
        ICubeFieldSource[] cubeFieldSources = new ICubeFieldSource[fieldList.size()];
        for (int i = 0; i < fieldList.size(); i++) {
            cubeFieldSources[i] = fieldList.get(i);
        }
        tableEntityService.recordRowCount(this.source.read(new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue v) {
                try {
                    tableEntityService.addDataValue(v);
                } catch (BICubeColumnAbsentException e) {
                    logger.warn(e.getMessage(),e);
                }
            }
        }, cubeFieldSources, UserETLCubeTILoader.getInstance(biUser.getUserId())));
        tableEntityService.addVersion(getTableVersion());
        tableEntityService.forceReleaseWriter();
        ICubeFieldSource[] fields = source.getFieldsArray(new HashSet<CubeTableSource>());
        for (int i = 0; i < fields.length; i++) {
            ICubeFieldSource field = fields[i];
            Iterator<BIColumnKey> columnKeyIterator = BIColumnKey.generateColumnKey(field).iterator();
            while (columnKeyIterator.hasNext()) {
                BIColumnKey targetColumnKey = columnKeyIterator.next();
                new BIFieldIndexGenerator(cube, source, field, targetColumnKey).mainTask(null);
            }
        }
        tableEntityService.forceReleaseWriter();
    }


    private long getBaseSourceVersion(CubeTableSource source) {
        if (source instanceof AnalysisCubeTableSource) {
            if (!BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().isAvailable((AnalysisCubeTableSource) source, biUser)) {
                return -1L;
            } else {
                return BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().getTableVersion((AnalysisCubeTableSource) source, biUser);
            }
        }
        ICubeTableService service = CubeReadingTableIndexLoader.getInstance(biUser.getUserId()).getTableIndex(source);
        return service == null ? -1l : service.getTableVersion(new IndexKey(StringUtils.EMPTY));
    }

    public String getPath() {
        return path;
    }


    @Override
    public void start() {
        start = new Date();
        BILoggerFactory.getLogger().info("started in file path:" + path);
    }


    @Override
    public void end() {
        UserETLCubeManagerProvider manager = BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider();
        manager.setCubePath(source.fetchObjectCore().getID().getIdentityValue(), getPath());
        end = new Date();
        manager.releaseCurrentThread(source.fetchObjectCore().getIDValue());
    }


    /* (non-Javadoc)
     * @see com.fr.json.JSONCreator#createJSON()
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        if (start != null) {
            jo.put("start", start.getTime());
        }
        if (end != null) {
            jo.put("end", end.getTime());
        }
        return jo;
    }


    /* (non-Javadoc)
     * @see com.fr.bi.stable.engine.CubeTask#getCubeTaskId()
     */
    @Override
    public String getTaskId() {
        return getPath();
    }


    /* (non-Javadoc)
     * @see com.fr.bi.stable.engine.CubeTask#getTaskType()
     */
    @Override
    public CubeTaskType getTaskType() {
        return CubeTaskType.CHECK;
    }


    /* (non-Javadoc)
     * @see com.fr.bi.stable.engine.CubeTask#getUserId()
     */
    @Override
    public long getUserId() {
        return source.getUserId();
    }

    @Override
    public Set<String> getTaskTableSourceIds() {
        return new HashSet<String>();
    }


    /**
     * @return
     */
    public boolean check(long oldVersion) {
        UserETLCubeManagerProvider manager = BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider();
        return manager.getCubePath(source.fetchObjectCore().getID().getIdentityValue()) != null && checkSourceVersion(oldVersion);
    }

    /**
     * @return
     */
    private boolean checkSourceVersion(long oldVersion) {
        return oldVersion == getTableVersion();
    }

    private long getTableVersion() {

        TreeMap<String, CubeTableSource> tm = new TreeMap<String, CubeTableSource>();
        for (CubeTableSource s : source.getParentSource()) {
            tm.put(s.fetchObjectCore().getIDValue(), s);
        }
        tm.remove(source.getAnalysisCubeTableSource().fetchObjectCore().getIDValue());
        List versionList = new ArrayList();
        Iterator<Map.Entry<String, CubeTableSource>> iter = tm.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, CubeTableSource> entry = iter.next();
            versionList.add(entry.getKey());
            versionList.add(getBaseSourceVersion(entry.getValue()));
        }
        return Arrays.hashCode(versionList.toArray());
    }

    public boolean isAvailable() {
        return source.isParentAvailable();
    }

    public BICore getKey() {
        return source.fetchObjectCore();
    }

    public void rollback() {
        if (this.cube != null) {
            try {
                this.cube.clear();
            } catch (Throwable e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
            }
        }
        BIFileUtils.delete(new File(this.path).getParentFile());
    }
}