package com.finebi.cube.structure.column;

import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.BICubeFieldRelationManager;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.structure.column.date.*;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.*;

/**
 * This class created on 2016/3/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeTableColumnManager implements ICubeTableColumnManagerService {

    private Map<BIColumnKey, ICubeColumnEntityService> columnKey2ColumnMap;
    private ICubeResourceRetrievalService resourceRetrievalService;

    private ITableKey tableKey;

    public BICubeTableColumnManager(ITableKey tableKey, ICubeResourceRetrievalService resourceRetrievalService, List<DBField> fieldList) {
        columnKey2ColumnMap = new HashMap<BIColumnKey, ICubeColumnEntityService>();
        this.resourceRetrievalService = resourceRetrievalService;
        this.tableKey = tableKey;
        initialColumn(fieldList, tableKey);
    }

    @Override
    public ICubeColumnEntityService getColumn(BIColumnKey columnKey) throws BICubeColumnAbsentException {
        if (columnKey2ColumnMap.containsKey(columnKey)) {
            return columnKey2ColumnMap.get(columnKey);
        } else {
            throw new BICubeColumnAbsentException("Please register Cube Column Service firstly");
        }
    }

    public Set<BIColumnKey> getCubeColumnInfo() {
        return columnKey2ColumnMap.keySet();
    }

    public void registerColumn(ICubeColumnEntityService columnEntityService, BIColumnKey columnKey) {
        if (!columnKey2ColumnMap.containsKey(columnKey)) {
            columnKey2ColumnMap.put(columnKey, columnEntityService);
        }
    }

    /**
     * @param columnEntityService
     * @return
     */
    private ICubeColumnEntityService insertFieldRelationManager(ICubeColumnEntityService columnEntityService, BIColumnKey currentFieldKey) {
        columnEntityService.setRelationManagerService(new BICubeFieldRelationManager(resourceRetrievalService, tableKey, currentFieldKey));
        return columnEntityService;
    }


    private void initialColumn(List<DBField> fieldSet, ITableKey tableKey) {
        for (int i = 0; i < fieldSet.size(); i++) {
            try {
                DBField field = fieldSet.get(i);
                switch (field.getFieldType()) {
                    case DBConstant.COLUMN.DATE:
                        initialDataColumn(field, tableKey);
                        break;
                    case DBConstant.COLUMN.NUMBER:
                        switch (field.getClassType()) {
                            case DBConstant.CLASS.INTEGER:
                            case DBConstant.CLASS.LONG: {
                                initialLongColumn(field, tableKey);
                                break;
                            }
                            default: {
                                initialDoubleColumn(field, tableKey);
                                break;
                            }
                        }
                        break;
                    case DBConstant.COLUMN.STRING:
                        initialStringColumn(field, tableKey);
                        break;
                }
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
                continue;

            }
        }

    }

    private void initialDataColumn(DBField field, ITableKey tableKey) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation fieldLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                BICubeDateColumn dateColumn = new BICubeDateColumn(fieldLocation);
                BIColumnKey columnKey = new BIColumnKey(field.getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.EMPTY_SUB_TYPE);
                this.registerColumn(insertFieldRelationManager(dateColumn, columnKey),
                        columnKey);
                initialYearColumn(field, tableKey, dateColumn);
                initialMonthColumn(field, tableKey, dateColumn);
                initialDayColumn(field, tableKey, dateColumn);
                initialSeasonColumn(field, tableKey, dateColumn);
                initialWeekColumn(field, tableKey, dateColumn);
                initialYearMonthDayColumn(field, tableKey, dateColumn);
            } catch (Exception e) {
                BINonValueUtils.beyondControl(e.getMessage(), e);
            }
        }
    }

    private ITableKey convert(ITableSource tableSource) {
        return new BITableKey(tableSource);
    }

    private void initialLongColumn(DBField field, ITableKey tableKey) {
        if (field.getFieldType() == DBConstant.COLUMN.NUMBER) {
            try {
                BIColumnKey columnKey = new BIColumnKey(field.getFieldName(), BIColumnKey.LONG_COLUMN_TYPE, BIColumnKey.EMPTY_SUB_TYPE);
                ICubeResourceLocation longLocation = resourceRetrievalService.retrieveResource(tableKey, columnKey
                );
                this.registerColumn(insertFieldRelationManager(new BICubeLongColumn(longLocation), columnKey),
                        columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialStringColumn(DBField field, ITableKey tableKey) {
        if (field.getFieldType() == DBConstant.COLUMN.STRING) {
            try {
                BIColumnKey columnKey = new BIColumnKey(field.getFieldName(), BIColumnKey.STRING_COLUMN_TYPE, BIColumnKey.EMPTY_SUB_TYPE);
                ICubeResourceLocation stringLocation = resourceRetrievalService.retrieveResource(tableKey, columnKey
                );
                this.registerColumn(insertFieldRelationManager(new BICubeStringColumn(stringLocation), columnKey),
                        columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialDoubleColumn(DBField field, ITableKey tableKey) {
        if (field.getFieldType() == DBConstant.COLUMN.NUMBER) {
            try {
                BIColumnKey columnKey = new BIColumnKey(field.getFieldName(), BIColumnKey.DOUBLE_COLUMN_TYPE, BIColumnKey.EMPTY_SUB_TYPE);
                ICubeResourceLocation doubleLocation = resourceRetrievalService.retrieveResource(tableKey, columnKey
                );
                this.registerColumn(insertFieldRelationManager(new BICubeDoubleColumn(doubleLocation), columnKey),
                        columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialYearColumn(DBField field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation yearLocation = BIDateLocationTool.createYear(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateYear(field);
                BICubeYearColumn yearColumn = new BICubeYearColumn(yearLocation, hostDataColumn);
                hostDataColumn.addSubColumns(yearColumn);
                this.registerColumn(insertFieldRelationManager(yearColumn, columnKey),
                        columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialMonthColumn(DBField field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation monthLocation = BIDateLocationTool.createMonth(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateMonth(field);
                BICubeMonthColumn monthColumn = new BICubeMonthColumn(monthLocation, hostDataColumn);
                hostDataColumn.addSubColumns(monthColumn);
                this.registerColumn(insertFieldRelationManager(monthColumn, columnKey),
                        columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialSeasonColumn(DBField field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation seasonLocation = BIDateLocationTool.createSeason(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateSeason(field);
                BICubeSeasonColumn seasonColumn = new BICubeSeasonColumn(seasonLocation, hostDataColumn);
                hostDataColumn.addSubColumns(seasonColumn);
                this.registerColumn(insertFieldRelationManager(seasonColumn, columnKey),
                        columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialWeekColumn(DBField field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation weekLocation = BIDateLocationTool.createWeek(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateWeek(field);
                BICubeWeekColumn weekColumn = new BICubeWeekColumn(weekLocation, hostDataColumn);
                hostDataColumn.addSubColumns(weekColumn);
                this.registerColumn(insertFieldRelationManager(weekColumn, columnKey),
                        columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialDayColumn(DBField field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation dayLocation = BIDateLocationTool.createDay(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateDay(field);
                BICubeDayColumn dayColumn = new BICubeDayColumn(dayLocation, hostDataColumn);
                hostDataColumn.addSubColumns(dayColumn);
                this.registerColumn(insertFieldRelationManager(dayColumn, columnKey),
                        columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialYearMonthDayColumn(DBField field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation yearMonthDayLocation = BIDateLocationTool.createYearMonthDay(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateYearMonthDay(field);
                BICubeYearMonthDayColumn yearMonthDayColumn = new BICubeYearMonthDayColumn(yearMonthDayLocation, hostDataColumn);
                hostDataColumn.addSubColumns(yearMonthDayColumn);
                this.registerColumn(insertFieldRelationManager(yearMonthDayColumn, columnKey)
                        , columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    @Override
    public void releaseResource() {
        Iterator<ICubeColumnEntityService> it = columnKey2ColumnMap.values().iterator();
        while (it.hasNext()) {
            it.next().releaseResource();
        }
    }
}
