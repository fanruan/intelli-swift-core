package com.fr.bi.cal.generate.relation.basiclinkindex;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.base.FRContext;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.loader.CubeGeneratingTableIndexLoader;
import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.cal.stable.index.utils.BIVersionUtils;
import com.fr.bi.cal.stable.relation.LinkIndexLoader;
import com.fr.bi.conf.log.BIRecord;
import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.file.IndexFile;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.structure.array.ArrayKey;
import com.fr.bi.stable.utils.BIRelationUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.code.BIPrintUtils;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;

import java.util.*;

/**
 * 生成主键字段的关联
 *
 * @author Daniel
 */
public class LinkBasicIndexManagerAndLoader implements LinkIndexLoader, java.util.concurrent.Callable<Object> {

    private CubeTableSource start;
    private TableCubeFile oldCube;
    private TableCubeFile currentCube;
    protected BIUser biUser;
    private BIRecord log;

    public LinkBasicIndexManagerAndLoader(CubeTableSource start, long userId) {
        biUser = new BIUser(userId);
        this.start = start;
        CubeTableSource cubeTableSource = null;
        cubeTableSource = start;

        oldCube = new TableCubeFile(BIPathUtils.createTablePath(cubeTableSource.getSourceID(), userId));
        currentCube = new TableCubeFile(BIPathUtils.createTableTempPath(cubeTableSource.getSourceID(), userId));

    }

    /**
     * 执行
     *
     * @return object对象
     * @throws Exception
     */
    @Override
    public Object call() throws Exception {
        try {
            generateCube();
        } catch (Throwable e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void generateCube() {
        ICubeDataLoader loader = getLoader();
        ICubeTableService ti = loader.getTableIndex(start);
        if (ti != null) {
            generateIndex(start, new ArrayList<BITableSourceRelation>());
        }
    }

    private ICubeDataLoader getLoader() {
        return CubeGeneratingTableIndexLoader.getInstance(biUser.getUserId());
    }

    protected void generateIndex(CubeTableSource primaryKey, final ArrayList<BITableSourceRelation> parRelation) {

        Set<BITableSourceRelation> set = getPrimaryKeyMap().get(primaryKey);
        if (set == null) {
            return;
        }
        for (BITableSourceRelation relation : set) {
            ArrayList<BITableSourceRelation> nextRelation = new ArrayList<BITableSourceRelation>(parRelation);

            CubeTableSource key = relation.getForeignTable();
            nextRelation.add(relation);
            if (BIRelationUtils.isRelationRepeated(nextRelation, key)) {
                if (log != null) {
                    log.loopRelation(generateLinkList(nextRelation, relation));
                }
                //这里判断死循环
                continue;
            }
            if (relation.isSelfRelation()) {
                //自循环
                continue;
            }
            int relation_version = BIVersionUtils.createRelationVersionValue(getLoader(), nextRelation);
            boolean check = oldCube.checkRelationVersion(nextRelation, relation_version);
            if (!check) {
                try {
                    generateLinkIndex(nextRelation);
                } catch (Throwable e) {
                    FRContext.getLogger().error(e.getMessage(), e);
                    if (log != null) {
                        final RelationColumnKey ck = new RelationColumnKey(nextRelation.get(0).getPrimaryKey(), nextRelation);
                        log.errorRelation(ck, e.getClass().getName() + ":" + e.getMessage() + ":" + Arrays.toString(e.getStackTrace()));
                    }
                }
            }
            generateIndex(relation.getForeignField().getTableBelongTo(), nextRelation);
        }

    }

    /**
     * 生成死循环连接
     *
     * @param relations 连接数组
     * @param rel       添加的最后一条造成死循环的连接
     * @return map对象 ，用于区分哪些死循环已经存在了
     */
    private Set<ArrayKey<BITableSourceRelation>> generateLinkList(ArrayList<BITableSourceRelation> relations, BITableSourceRelation rel) {
        Set<ArrayKey<BITableSourceRelation>> set = new HashSet<ArrayKey<BITableSourceRelation>>();
        ArrayList<BITableSourceRelation> listRelation = new ArrayList<BITableSourceRelation>();
        CubeTableSource key = rel.getForeignKey().getTableBelongTo();
        Iterator it = relations.iterator();
        boolean found = false;
        while (it.hasNext()) {
            BITableSourceRelation relation = (BITableSourceRelation) it.next();
            if (found) {
                listRelation.add(relation);
                continue;
            }
            if (ComparatorUtils.equals(relation.getForeignKey().getTableBelongTo(), key)) {
                found = true;
                continue;
            }
            if (ComparatorUtils.equals(relation.getPrimaryKey().getTableBelongTo(), key)) {
                found = true;
                listRelation.add(relation);
            }
        }
        if (found) {
            listRelation.add(rel);
        }

        if (ifLoop(listRelation)) {
            set.add(new ArrayKey<BITableSourceRelation>(listRelation.toArray(new BITableSourceRelation[listRelation.size()])));
        }
        return set;
    }

    private boolean ifLoop(ArrayList<BITableSourceRelation> listRelation) {
        boolean flag = false;
        Map<String, ArrayList<String>> judge = new HashMap<String, ArrayList<String>>();
        for (BITableSourceRelation aListRelation : listRelation) {
            String tableName = aListRelation.getPrimaryKey().getTableBelongTo().getTableName();
            String fieldName = aListRelation.getPrimaryKey().getFieldName();
            putIf(judge, tableName, fieldName);

            tableName = aListRelation.getForeignKey().getTableBelongTo().getTableName();
            fieldName = aListRelation.getForeignKey().getFieldName();
            putIf(judge, tableName, fieldName);
        }
        if (judge.size() == 1) {
            return false;
        }
        for (Map.Entry<String, ArrayList<String>> entry : judge.entrySet()) {
            ArrayList<String> list = entry.getValue();
            if (list.size() != 2) {
                break;
            }
            if (!ComparatorUtils.equals(list.get(0), list.get(1))) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private void putIf(Map<String, ArrayList<String>> judge, String tableName, String fieldName) {
        if (judge.containsKey(tableName)) {
            judge.get(tableName).add(fieldName);
        } else {
            ArrayList<String> t = new ArrayList<String>();
            t.add(fieldName);
            judge.put(tableName, t);
        }
    }


    protected Map<CubeTableSource, Set<BITableSourceRelation>> getPrimaryKeyMap() {
        return BICubeConfigureCenter.getCubeManager().getGeneratingObject(biUser.getUserId()).getPrimaryKeyMap();
    }

    private ICubeTableService getTableIndex(CubeTableSource table) {
        return getLoader().getTableIndex(table);
    }

    protected void generateLinkIndex(List<BITableSourceRelation> relations) {
        if (relations.size() < 2) {
            return;
        }
        IndexFile indexFile = currentCube.getLinkIndexFile(relations);
        final NIOWriter<byte[]> indexWriter = indexFile.createIndexWriter();
        final NIOWriter<byte[]> nullWriter = indexFile.createNullWriter();
        CubeTableSource end = relations.get(relations.size() - 1).getForeignKey().getTableBelongTo();
        BILogger.getLogger().info("generate basic relation from table :" + start.toString() + "to table :" + end.toString());
        List<BITableSourceRelation> pRelation = new ArrayList<BITableSourceRelation>();
        for (int i = 0; i < relations.size() - 1; i++) {
            pRelation.add(relations.get(i));
        }
        ICubeTableService sti = getTableIndex(start);
        ICubeTableService eti = getTableIndex(end);
        indexFile.writeGroupCount(eti.getRowCount());
        final ICubeTableIndexReader pReader = sti.ensureBasicIndex(pRelation);
        CubeTableSource startTable = relations.get(relations.size() - 1).getPrimaryKey().getTableBelongTo();
        List<BITableSourceRelation> linkR = new ArrayList<BITableSourceRelation>();
        linkR.add(relations.get(relations.size() - 1));
        final ICubeTableIndexReader sReader = getTableIndex(startTable).ensureBasicIndex(linkR);
        final int rowCount = sti.getRowCount();
        final int endRowCount = eti.getRowCount();
        final GroupValueIndex nullIndex = GVIFactory.createAllEmptyIndexGVI();
        final long t = System.currentTimeMillis();
        final String indexLog = "basic relation from table :" + relations.toString() + "to table:" + end.toString() + " finished ";
        final RelationColumnKey ck = new RelationColumnKey(relations.get(0).getPrimaryKey(), relations);
        if (log != null) {
            log.infoRelation(ck, 0, 0);
        }
        methodWrapper(relations, indexFile, indexWriter, nullWriter, end, sti, pReader, sReader, rowCount, endRowCount, nullIndex, t, indexLog, ck);
    }

    /**
     * Connery：代码过长抽取成两部分的
     *
     * @param relations
     * @param indexFile
     * @param indexWriter
     * @param nullWriter
     * @param end
     * @param sti
     * @param pReader
     * @param sReader
     * @param rowCount
     * @param endRowCount
     * @param nullIndex
     * @param t
     * @param indexLog
     * @param ck
     */
    private void methodWrapper(List<BITableSourceRelation> relations, IndexFile indexFile, final NIOWriter<byte[]> indexWriter, NIOWriter<byte[]> nullWriter, CubeTableSource end, ICubeTableService sti, final ICubeTableIndexReader pReader, final ICubeTableIndexReader sReader, final int rowCount, int endRowCount, final GroupValueIndex nullIndex, final long t, final String indexLog, final RelationColumnKey ck) {
        sti.getAllShowIndex().Traversal(new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int rowIndices) {
                GroupValueIndex pGvi = pReader.get(rowIndices);
                GroupValueIndex gvi = GVIUtils.getTableLinkedOrGVI(pGvi, sReader);
                if (gvi == null) {
                    gvi = GVIFactory.createAllEmptyIndexGVI();
                }
                nullIndex.or(gvi);
                indexWriter.add(rowIndices, gvi.getBytes());
                if (((rowIndices + 1) & RELATION_LOG_ROW) == 0) {
                    BIPrintUtils.writeIndexLog(indexLog, rowIndices + 1, rowCount, t);
                    if (log != null) {
                        log.infoRelation(ck, System.currentTimeMillis() - t, Math.round((float) (rowIndices + 1) / rowCount * RELATION_PERCENT_ONE_HUNDRED));
                    }
                }
            }
        });
        nullWriter.add(0, nullIndex.NOT(endRowCount).getBytes());
        indexFile.releaseGroupValueIndexCreator();
        indexFile.writeVersion(BIVersionUtils.createRelationVersionValue(getLoader(), relations));
        BILogger.getLogger().info("basic relation from table :" + start.toString() + "to table : " + end.toString() + "generated sucessfully, cost :" + DateUtils.timeCostFrom(t));
        if (log != null) {
            log.infoRelation(ck, System.currentTimeMillis() - t);
        }
    }

    public void setLog(BIRecord log) {
        this.log = log;
    }


}