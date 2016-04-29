package com.fr.bi.cal.generate.relation.inuserelation;

import com.fr.base.FRContext;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.loader.CubeGeneratingTableIndexLoader;
import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.cal.stable.index.utils.BIVersionUtils;
import com.fr.bi.cal.stable.relation.uselinkindex.LinkColumnUseIndexLoader;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.db.DBField;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;
import com.fr.bi.stable.index.CubeGenerator;
import com.fr.bi.stable.io.newio.NIOUtils;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.relation.utils.BIRelationUtils;
import com.fr.bi.stable.utils.CubeBaseUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.file.BIPathUtils;
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
        Set<BIField> fields =  new UseFieldGetter(user).getUsedField();
        Map<BICore, Set<String>> usedFiledsMap = new HashMap<BICore, Set<String>>();
        for (BIField field : fields){
            BICore md5 = BIConfigureManagerCenter.getDataSourceManager().getCoreByTableID(field.getTableID(), user);
            Set<String> fieldsSet = usedFiledsMap.get(md5);
            if (fieldsSet == null){
                fieldsSet = new HashSet<String>();
                usedFiledsMap.put(md5, fieldsSet);
            }
            fieldsSet.add(field.getFieldName());
        }
        for (Map.Entry<BICore, Set<String>> entry : usedFiledsMap.entrySet()){
            creatLoader(threadList,  new TableCubeFile(BIPathUtils.createTableTempPath(entry.getKey().getIDValue(), user.getUserId())) ,entry.getValue(), new BITable(entry.getKey().getIDValue()), new ArrayList<BITableSourceRelation>());
        }

        try {
            CubeBaseUtils.invokeCubeThreads(threadList);
        } catch (InterruptedException e) {
            FRContext.getLogger().error(e.getMessage(), e);
        }
        BILogger.getLogger().info("Inuse Relations Completed! Cost:" + DateUtils.timeCostFrom(start));
    }

    private void creatLoader(List<LinkColumnUseIndexLoader> threadList, TableCubeFile cube, Set<String> fieldNames, Table md5Table, ArrayList<BITableSourceRelation> parRelation) {
        Set<BITableSourceRelation> set = getPrimaryKeyMap().get(md5Table);
        if (set == null) {
            return;
        }
        for (BITableSourceRelation relation : set) {
            ArrayList<BITableSourceRelation> nextRelation = new ArrayList<BITableSourceRelation>(parRelation);

            Table key = relation.getForeignKey().getTableBelongTo();
            if (BIRelationUtils.isRelationRepeated(nextRelation, relation.getForeignTable()) || relation.isSelfRelation()) {
                continue;
            }
            nextRelation.add(relation);
            int relation_version = BIVersionUtils.createRelationVersionValue(getLoader(), nextRelation);
            for (String fieldName : fieldNames){
                for (BIKey fieldKey : createKeys(cube, fieldName) ){
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

    private Set<BIKey> createKeys(TableCubeFile cube, String field){
        Set<BIKey> set = new HashSet<BIKey>();
        for (DBField f :cube.getBIField()){
            if (ComparatorUtils.equals(f.getFieldName(), field)){
                if (f.getFieldType() == DBConstant.COLUMN.DATE){
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

    private Map<Table, Set<BITableSourceRelation>> getPrimaryKeyMap() {
        return BIConfigureManagerCenter.getCubeManager().getGeneratingObject(user.getUserId()).getPrimaryKeyMap();
    }
}