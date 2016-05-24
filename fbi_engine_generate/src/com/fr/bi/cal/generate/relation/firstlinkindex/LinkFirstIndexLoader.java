package com.fr.bi.cal.generate.relation.firstlinkindex;


import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.index.utils.BIVersionUtils;
import com.fr.bi.cal.stable.relation.LinkIndexLoader;
import com.fr.bi.conf.log.BIRecord;
import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.exception.CubeGenerateException;
import com.fr.bi.stable.file.IndexFile;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.code.BIPrintUtils;
import com.fr.general.DateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 小灰灰 on 14-1-23.
 */
public class LinkFirstIndexLoader implements LinkIndexLoader, java.util.concurrent.Callable<Object> {

    private BITableSourceRelation relation;

    private IndexFile indexFile;

    private ICubeDataLoader loader;

    private BIRecord log;

    public LinkFirstIndexLoader(BITableSourceRelation relation, IndexFile indexFile, ICubeDataLoader loader) {
        this.relation = relation;
        this.indexFile = indexFile;
        this.loader = loader;
    }

    /**
     * 不解释
     *
     * @return 不解释
     * @throws Exception
     */
    @Override
    public Object call() throws Exception {
        try {
            generateCube();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            if (log != null) {
                List<BITableSourceRelation> relations = new ArrayList<BITableSourceRelation>();
                relations.add(relation);
                final RelationColumnKey ck = new RelationColumnKey(relation.getPrimaryKey(), relations);
                log.errorRelation(ck, e.getClass().getName() + ":" + e.getMessage() + ":" + Arrays.toString(e.getStackTrace()));
            }
        }
        return null;
    }


    private RelationColumnKey createRelationColumnKey() {
        List<BITableSourceRelation> relations = new ArrayList<BITableSourceRelation>();
        relations.add(relation);
        return new RelationColumnKey(relation.getPrimaryKey(), relations);
    }

    /**
     * 生成cube
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void generateCube() {
        final RelationColumnKey ck = createRelationColumnKey();
        if (log != null) {
            log.infoRelation(ck, 0, 0);
        }
        final ICubeTableService sti = loader.getTableIndex(relation.getPrimaryKey());
        final ICubeTableService eti = loader.getTableIndex(relation.getForeignKey());
        final String message = "generate first relation from table :" + relation.getPrimaryKey().toString().toString() + "to table:" + relation.getForeignKey().toString();
        BILogger.getLogger().info(message + " start ");
        final BIKey si = sti.getColumnIndex(relation.getPrimaryKey().getFieldName());
        final BIKey ei = eti.getColumnIndex(relation.getForeignKey().getFieldName());
        final ICubeColumnIndexReader targetGroupMap = eti.loadGroup(ei);
        if (targetGroupMap == null) {
            throw new CubeGenerateException("ERROR : first relation from table :" + relation.getPrimaryKey().toString() + "to table: " + relation.getForeignKey().getFieldName() + "generate failed");
        }
        final int rowCount = sti.getRowCount();
        final int finalRowLength = eti.getRowCount();
        final NIOWriter<byte[]> indexWriter = indexFile.createIndexWriter();
        final NIOWriter<byte[]> nullWriter = indexFile.createNullWriter();
        final GroupValueIndex nullIndex = GVIFactory.createAllEmptyIndexGVI();
        final long t = System.currentTimeMillis();
        sti.getAllShowIndex().Traversal(new SingleRowTraversalAction() {
            @SuppressWarnings("unchecked")
            @Override
            public void actionPerformed(int rowIndices) {
                Object[] showKeys = targetGroupMap.createKey(1);
                showKeys[0] = sti.getRow(si, rowIndices);
                GroupValueIndex gvi = targetGroupMap.getGroupIndex(showKeys)[0];
                if (gvi == null) {
                    gvi = GVIFactory.createAllEmptyIndexGVI();
                }
                nullIndex.or(gvi);
                indexWriter.add(rowIndices, gvi.getBytes());
                if (((rowIndices + 1) & RELATION_LOG_ROW) == 0) {
                    BIPrintUtils.writeIndexLog(message, rowIndices + 1, sti.getRowCount(), t);
                    if (log != null) {
                        log.infoRelation(ck, System.currentTimeMillis() - t, Math.round((float) (rowIndices + 1) / rowCount * RELATION_PERCENT_ONE_HUNDRED));
                    }
                }
            }
        });
        nullWriter.add(0, nullIndex.NOT(finalRowLength).getBytes());
        indexFile.writeGroupCount(finalRowLength);
        indexFile.releaseGroupValueIndexCreator();
        indexFile.writeVersion(BIVersionUtils.createRelationVersionValue(loader, ck.getRelations()));
        BILogger.getLogger().info("first relation from table:" + relation.getPrimaryKey().toString() + "to table : " + relation.getForeignKey().toString()
                + " generated successfully! cost:" + DateUtils.timeCostFrom(t));
        if (log != null) {
            log.infoRelation(ck, System.currentTimeMillis() - t);
        }
    }

    public void setLog(BIRecord log) {
        this.log = log;
    }
}