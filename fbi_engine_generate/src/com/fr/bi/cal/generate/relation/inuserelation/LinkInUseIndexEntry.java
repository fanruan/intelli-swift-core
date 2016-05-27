package com.fr.bi.cal.generate.relation.inuserelation;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.base.FRContext;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.loader.CubeGeneratingTableIndexLoader;
import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.cal.stable.index.utils.BIVersionUtils;
import com.fr.bi.cal.stable.relation.uselinkindex.LinkColumnUseIndexLoader;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.exception.BIKeyAbsentException;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.CubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;
import com.fr.bi.stable.index.CubeGenerator;
import com.fr.bi.stable.io.newio.NIOUtils;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.utils.BIRelationUtils;
import com.fr.bi.stable.utils.CubeBaseUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;

import java.util.*;

/**
 * Created by 小灰灰 on 2016/2/15.
 */
public class LinkInUseIndexEntry implements CubeGenerator {
    private BIUser user;

    public LinkInUseIndexEntry(BIUser user) {
        this.user = user;
    }

    @Override
    public void generateCube() {
        BILogger.getLogger().info("Prepare InUse Relations");
        long start = System.currentTimeMillis();
        List<LinkColumnUseIndexLoader> threadList = new ArrayList<LinkColumnUseIndexLoader>();
        Set<BusinessField> fields = new UseFieldGetter(user).getUsedField();
        Map<CubeTableSource, Set<String>> usedFiledsMap = new HashMap<CubeTableSource, Set<String>>();
        for (BusinessField field : fields) {
            CubeTableSource cubeTableSource = null;
            try {
                if (field.getTableBelongTo() != null) {
                    cubeTableSource = BICubeConfigureCenter.getDataSourceManager().getTableSource(field.getTableBelongTo());
                } else {
                    continue;
                }
            } catch (BIKeyAbsentException e) {
                throw BINonValueUtils.beyondControl(e);
            }
            Set<String> fieldsSet = usedFiledsMap.get(cubeTableSource);
            if (fieldsSet == null) {
                fieldsSet = new HashSet<String>();
                usedFiledsMap.put(cubeTableSource, fieldsSet);
            }
            fieldsSet.add(field.getFieldName());
        }
        for (Map.Entry<CubeTableSource, Set<String>> entry : usedFiledsMap.entrySet()) {
            creatLoader(threadList, new TableCubeFile(BIPathUtils.createTableTempPath(entry.getKey().getSourceID(),
                    user.getUserId())), entry.getValue(),
                    /**
                     * TODO
                     * Connery：标记一下，这个TableSource
                     * 肯定不对
                     */
                    new DBTableSource(),
                    new ArrayList<BITableSourceRelation>());
        }

        try {
            CubeBaseUtils.invokeCubeThreads(threadList);
        } catch (InterruptedException e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }
        BILogger.getLogger().info("Inuse Relations Completed! Cost:" + DateUtils.timeCostFrom(start));
    }

    private void creatLoader(List<LinkColumnUseIndexLoader> threadList, TableCubeFile cube, Set<String> fieldNames, CubeTableSource md5Table, ArrayList<BITableSourceRelation> parRelation) {
        Set<BITableSourceRelation> set = getPrimaryKeyMap().get(md5Table);
        if (set == null) {
            return;
        }
        for (BITableSourceRelation relation : set) {
            ArrayList<BITableSourceRelation> nextRelation = new ArrayList<BITableSourceRelation>(parRelation);

            CubeTableSource key = relation.getForeignKey().getTableBelongTo();
            if (BIRelationUtils.isRelationRepeated(nextRelation, relation.getForeignTable()) || relation.isSelfRelation()) {
                continue;
            }
            nextRelation.add(relation);
            int relation_version = BIVersionUtils.createRelationVersionValue(getLoader(), nextRelation);
            for (String fieldName : fieldNames) {
                for (BIKey fieldKey : createKeys(cube, fieldName)) {
                    boolean check = cube.checkRelationVersion(fieldKey, nextRelation, relation_version);
                    if (!check) {
                        LinkColumnUseIndexLoader loader = new LinkColumnUseIndexLoader(cube, fieldKey, nextRelation, getNIOManager());
                        threadList.add(loader);
                    }
                }
            }
            creatLoader(threadList, cube, fieldNames, key, nextRelation);
        }

    }

    private Set<BIKey> createKeys(TableCubeFile cube, String field) {
        Set<BIKey> set = new HashSet<BIKey>();
        for (CubeFieldSource f : cube.getBIField()) {
            if (ComparatorUtils.equals(f.getFieldName(), field)) {
                if (f.getFieldType() == DBConstant.COLUMN.DATE) {
                    set.add(new IndexTypeKey(field, BIReportConstant.GROUP.Y));
                    set.add(new IndexTypeKey(field, BIReportConstant.GROUP.M));
                    set.add(new IndexTypeKey(field, BIReportConstant.GROUP.S));
                    set.add(new IndexTypeKey(field, BIReportConstant.GROUP.YMD));
                    set.add(new IndexTypeKey(field, BIReportConstant.GROUP.W));
                }
                set.add(new IndexKey(field));
            }
        }
        return set;
    }

    private ICubeDataLoader getLoader() {
        return CubeGeneratingTableIndexLoader.getInstance(user.getUserId());
    }

    private SingleUserNIOReadManager getNIOManager() {
        return NIOUtils.getGeneratingManager(user.getUserId());
    }

    private Map<CubeTableSource, Set<BITableSourceRelation>> getPrimaryKeyMap() {
        return BICubeConfigureCenter.getCubeManager().getGeneratingObject(user.getUserId()).getPrimaryKeyMap();
    }
}