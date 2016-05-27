package com.fr.bi.cal.stable.relation.uselinkindex;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.base.FRContext;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.relation.LinkIndexLoader;
import com.fr.bi.conf.log.BIRecord;
import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.bi.stable.data.db.CubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.BITableCubeFile;
import com.fr.bi.stable.file.IndexFile;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;
import com.fr.bi.stable.index.CubeGenerator;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.code.BIPrintUtils;
import com.fr.general.DateUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

/**
 * Created by GUY on 2015/4/3.
 */
public class LinkColumnUseIndexLoader implements LinkIndexLoader, CubeGenerator, Callable {

    /**
     *
     */
    private List<BITableSourceRelation> relations;
    private BIKey key;
    private BITableCubeFile cube;
    private BIRecord log;
    private String reportInfo;
    private SingleUserNIOReadManager manager;

    public LinkColumnUseIndexLoader(BITableCubeFile cube, BIKey key, List<BITableSourceRelation> relations, SingleUserNIOReadManager manager) {
        this.key = key;
        this.relations = relations;
        this.cube = cube;
        this.manager = manager;
    }

    private void generate() {
        if (relations != null && (!relations.isEmpty())) {
            final RelationColumnKey rck = new RelationColumnKey(relations.get(0).getPrimaryKey(), relations);
            long start = System.currentTimeMillis();
            CubeFieldSource startKey = relations.get(0).getPrimaryKey();
            CubeTableSource endTable = relations.get(relations.size() - 1).getForeignKey().getTableBelongTo();
            String message = "generate inuse relation from table :" + startKey.toString() + "to table : " + endTable.toString();
            BILogger.getLogger().info(message + " start ");
            final ICubeTableIndexReader reader = cube.getBasicGroupValueIndexArrayReader(relations, manager);
            final ICubeTableIndexReader indexes = cube.getGroupValueIndexArrayReader(key, manager);
            if (indexes == null) {
                BILogger.getLogger().info("Error : inuse Relation from table :" + relations.get(0).getPrimaryKey().toString() + "to table : " + endTable.toString() + " generate failed");
                return;
            }
            IndexFile ifile = cube.getLinkIndexFile(key, relations);
            NIOWriter<byte[]> IndexWriter = ifile.createIndexWriter();
            NIOWriter<byte[]> nullWriter = ifile.createNullWriter();
            long t = System.currentTimeMillis();
            ICubeColumnIndexReader getter = this.cube.createGroupByType(key, new ArrayList<BITableSourceRelation>(), manager);
            long nonPrecisionSize = getter.nonPrecisionSize();
            Iterator<Entry<?, GroupValueIndex>> iter = getter.iterator();
            GroupValueIndex nullIndex = GVIFactory.createAllEmptyIndexGVI();
            int k = 0;
            while (iter.hasNext()) {
                Entry<?, GroupValueIndex> entry = iter.next();
                GroupValueIndex pGvi = entry.getValue();
                GroupValueIndex gvi = GVIUtils.getTableLinkedOrGVI(pGvi, reader);
                gvi = gvi == null ? GVIFactory.createAllEmptyIndexGVI() : gvi;
                IndexWriter.add(k, gvi.getBytes());
                nullIndex.or(gvi);
                if (((k + 1) & RELATION_LOG_ROW) == 0) {
                    BIPrintUtils.writeIndexLog(message, k + 1, this.cube.getRowCount(), t);
                    if (log != null) {
                        log.infoRelation(rck, System.currentTimeMillis() - start,
                                Math.round((float) (k + 1) / nonPrecisionSize * RELATION_PERCENT_ONE_HUNDRED));
                    }
                }
                k++;
            }

            nullWriter.add(0, nullIndex.NOT((int) cube.getLinkIndexFile(relations).getGroupCount(BIKey.DEFAULT)).getBytes());
            ifile.releaseGroupValueIndexCreator();
            ifile.writeVersion(cube.getLinkIndexFile(relations).getVersion());
            BILogger.getLogger().info("inuse relation from table : " + startKey.toString() + "to table : " + endTable.toString() + "sucess generated  ! cost :" + DateUtils.timeCostFrom(start));
            if (log != null) {
                log.infoRelation(rck, System.currentTimeMillis() - start);
            }
        }
    }


    @Override
    public void generateCube() {
        try {
            generate();
        } catch (Throwable e) {
            FRContext.getLogger().error(e.getMessage(), e);
            if (log != null) {
                RelationColumnKey rck = new RelationColumnKey(relations.get(0).getPrimaryKey(), relations);
                log.errorRelation(rck, " " + getReportInfo() + " " + e.getClass().getName() + ":" + e.getMessage());
            }
        }

    }

    private String getReportInfo() {
        return reportInfo == null ? "" : reportInfo;
    }

    public void setReportInfo(String reportInfo) {
        this.reportInfo = reportInfo;
    }

    public void setLog(BIRecord log) {
        this.log = log;
    }

    @Override
    public Object call() throws Exception {
        generate();
        return null;
    }
}