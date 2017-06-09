package com.finebi.cube.structure.table;/**
 * Created by roy on 2017/5/24.
 * 由于现在Cube生成的时候，不需要生成数据是不复制的，直接从advanced中读取
 * ETL表有可能出现一张表的数据，部分存在于tCube中，部分存在于advanced中
 * 所以需要提供相应的table reader支持一张表从多个数据源获取数据
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeTableAbsentException;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.ITableKey;
import com.fr.bi.stable.data.db.ICubeFieldSource;

public class CompoundCubeTableReaderFromMultiSource extends CompoundCubeTableReader{
    private final static BILogger LOGGER = BILoggerFactory.getLogger(CompoundCubeTableReaderFromMultiSource.class);
    private BICubeTableEntity integrityHostTable;

    public CompoundCubeTableReaderFromMultiSource(ITableKey tableKey, ICubeResourceRetrievalService resourceRetrievalService, ICubeResourceRetrievalService integrityResourceRetrievalService, ICubeResourceDiscovery discovery) {
        super(tableKey, resourceRetrievalService, integrityResourceRetrievalService, discovery);
        hostTable = new BICubeTableEntity(tableKey, resourceRetrievalService, discovery);
        integrityHostTable = new BICubeTableEntity(tableKey, integrityResourceRetrievalService, discovery);
        if (hostTable.getParentsTable() != null && !hostTable.getParentsTable().isEmpty()) {
            parentTable = new CompoundCubeTableReaderNodeFromMultiSource(hostTable.getParentsTable(), resourceRetrievalService, integrityResourceRetrievalService, discovery);
            parentTable.setTableOwner(tableKey);
        }
        initialFields();
    }

    @Override
    protected void initialFields() {
        if (hostTable.tableDataAvailable()) {
            if (hostTable.getFieldInfo() != null) {
                for (ICubeFieldSource field : hostTable.getFieldInfo()) {
                    if (!compoundFields.contains(field)) {
                        compoundFields.add(field);
                        fieldSource.put(field, hostTable);
                    }
                }
            } else {
                LOGGER.error(
                        "hostTable sourceId:" + hostTable.tableKey.getSourceID() + " fields is null!Please check field file in cubes!");
                throw new BICubeTableAbsentException("hostTable sourceId:" + hostTable.tableKey.getSourceID() + " fields is null!Please check field file in cubes!");
            }
        } else if (integrityHostTable.tableDataAvailable()) {
            LOGGER.info("current cube does not contain the table: " + hostTable.tableKey.getSourceID() + " try to get table from integrityCube");
            hostTable = integrityHostTable;
            if (hostTable.getFieldInfo() != null) {
                for (ICubeFieldSource field : hostTable.getFieldInfo()) {
                    if (!compoundFields.contains(field)) {
                        compoundFields.add(field);
                        fieldSource.put(field, hostTable);
                    }
                }
            } else {
                LOGGER.error(
                        "hostTable sourceId:" + hostTable.tableKey.getSourceID() + " fields is null!Please check field file in cubes!");
                throw new BICubeTableAbsentException("hostTable sourceId:" + hostTable.tableKey.getSourceID() + " fields is null!Please check field file in cubes!");
            }
        } else {
            if (null == hostTable) {
                LOGGER.error("hostTable null");
            } else {
                LOGGER.error("hostTable sourceId:" + hostTable.tableKey.getSourceID());
            }
            throw new BICubeTableAbsentException("Please generate Cube firstly ,The Table:" + hostTable.tableKey.getSourceID() + " absent");
        }
        if (isParentAvailable()) {
            for (ICubeFieldSource field : parentTable.getFieldInfo()) {
                if (!compoundFields.contains(field) && isInFacedFields(field)) {
                    compoundFields.add(field);
                    fieldSource.put(field, parentTable);
                }
            }
        }
    }
}