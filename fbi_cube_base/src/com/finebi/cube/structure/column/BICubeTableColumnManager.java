package com.finebi.cube.structure.column;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.BICubeFieldRelationManager;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.structure.column.date.*;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class created on 2016/3/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeTableColumnManager implements ICubeTableColumnManagerService {

    private Map<BIColumnKey, ICubeColumnEntityService> columnKey2ColumnMap;
    private ICubeResourceRetrievalService resourceRetrievalService;
    private ICubeResourceDiscovery discovery;
    private ITableKey tableKey;
    private ITableKey owner;

    public BICubeTableColumnManager(ITableKey tableKey, ICubeResourceRetrievalService resourceRetrievalService, List<ICubeFieldSource> fieldList, ICubeResourceDiscovery discovery) {
        columnKey2ColumnMap = new HashMap<BIColumnKey, ICubeColumnEntityService>();
        this.resourceRetrievalService = resourceRetrievalService;
        this.tableKey = tableKey;
        this.discovery = discovery;
        initialColumn(fieldList, tableKey);
    }

    @Override
    public void setOwner(ITableKey owner) {
        this.owner = owner;
        for (ICubeColumnEntityService columnEntityService : columnKey2ColumnMap.values()) {
            columnEntityService.setOwner(owner);
        }
    }

    @Override
    public ICubeColumnEntityService getColumn(BIColumnKey columnKey) throws BICubeColumnAbsentException {
        if (columnKey2ColumnMap.containsKey(columnKey)) {
            return columnKey2ColumnMap.get(columnKey);
        } else {
            throw new BICubeColumnAbsentException("Please register FineIndex Column Service firstly");
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
        columnEntityService.setRelationManagerService(new BICubeFieldRelationManager(resourceRetrievalService, tableKey, currentFieldKey, discovery));
        return columnEntityService;
    }


    private void initialColumn(List<ICubeFieldSource> fieldSet, ITableKey tableKey) {
        for (ICubeFieldSource field : fieldSet) {
            try {
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
                BILoggerFactory.getLogger().error(e.getMessage(), e);
                continue;

            }
        }

    }

    private void initialDataColumn(ICubeFieldSource field, ITableKey tableKey) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation fieldLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                BICubeDateColumn dateColumn = new BICubeDateColumn(discovery, fieldLocation);
                BIColumnKey columnKey = new BIColumnKey(field.getFieldName(), BIColumnKey.DATA_COLUMN_TYPE, BIColumnKey.EMPTY_SUB_TYPE);
                this.registerColumn(insertFieldRelationManager(dateColumn, columnKey),
                        columnKey);
                initialYearColumn(field, tableKey, dateColumn);
                initialSeasonColumn(field, tableKey, dateColumn);
                initialMonthColumn(field, tableKey, dateColumn);
                initialWeekNumberColumn(field, tableKey, dateColumn);
                initialWeekColumn(field, tableKey, dateColumn);
                initialDayColumn(field, tableKey, dateColumn);

                initialHourColumn(field, tableKey, dateColumn);
                initialMinuteColumn(field, tableKey, dateColumn);
                initialSecondColumn(field, tableKey, dateColumn);

                initialYearSeasonColumn(field, tableKey, dateColumn);
                initialYearMonthColumn(field, tableKey, dateColumn);
                initialYearWeekNumberColumn(field, tableKey, dateColumn);
                initialYearMonthDayColumn(field, tableKey, dateColumn);
                initialYearMonthDayHourColumn(field, tableKey, dateColumn);
                initialYearMonthDayHourMinuteColumn(field, tableKey, dateColumn);
                initialYearMonthDayHourMinuteSecondColumn(field, tableKey, dateColumn);

            } catch (Exception e) {
                throw BINonValueUtils.beyondControl(e.getMessage(), e);
            }
        }
    }

    private ITableKey convert(CubeTableSource tableSource) {
        return new BITableKey(tableSource);
    }

    private void initialLongColumn(ICubeFieldSource field, ITableKey tableKey) {
        if (field.getFieldType() == DBConstant.COLUMN.NUMBER) {
            try {
                BIColumnKey columnKey = new BIColumnKey(field.getFieldName(), BIColumnKey.LONG_COLUMN_TYPE, BIColumnKey.EMPTY_SUB_TYPE);
                ICubeResourceLocation longLocation = resourceRetrievalService.retrieveResource(tableKey, columnKey
                );
                this.registerColumn(insertFieldRelationManager(new BICubeLongColumn(discovery, longLocation), columnKey),
                        columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialStringColumn(ICubeFieldSource field, ITableKey tableKey) {
        if (field.getFieldType() == DBConstant.COLUMN.STRING) {
            try {
                BIColumnKey columnKey = new BIColumnKey(field.getFieldName(), BIColumnKey.STRING_COLUMN_TYPE, BIColumnKey.EMPTY_SUB_TYPE);
                ICubeResourceLocation stringLocation = resourceRetrievalService.retrieveResource(tableKey, columnKey
                );
                this.registerColumn(insertFieldRelationManager(new BICubeStringColumn(discovery, stringLocation), columnKey),
                        columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialDoubleColumn(ICubeFieldSource field, ITableKey tableKey) {
        if (field.getFieldType() == DBConstant.COLUMN.NUMBER) {
            try {
                BIColumnKey columnKey = new BIColumnKey(field.getFieldName(), BIColumnKey.DOUBLE_COLUMN_TYPE, BIColumnKey.EMPTY_SUB_TYPE);
                ICubeResourceLocation doubleLocation = resourceRetrievalService.retrieveResource(tableKey, columnKey
                );
                this.registerColumn(insertFieldRelationManager(new BICubeDoubleColumn(discovery, doubleLocation), columnKey),
                        columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialYearColumn(ICubeFieldSource field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation yearLocation = BIDateLocationTool.createYear(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateYear(field);
                BICubeYearColumn yearColumn = new BICubeYearColumn(discovery, yearLocation, hostDataColumn);
                hostDataColumn.addSubColumns(yearColumn);
                this.registerColumn(insertFieldRelationManager(yearColumn, columnKey), columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialHourColumn(ICubeFieldSource field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation hourLocation = BIDateLocationTool.createHour(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateHour(field);
                BICubeHourColumn hourColumn = new BICubeHourColumn(discovery, hourLocation, hostDataColumn);
                hostDataColumn.addSubColumns(hourColumn);
                this.registerColumn(insertFieldRelationManager(hourColumn, columnKey), columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialMinuteColumn(ICubeFieldSource field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation minuteLocation = BIDateLocationTool.createMinute(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateMinute(field);
                BICubeMinuteColumn minuteColumn = new BICubeMinuteColumn(discovery, minuteLocation, hostDataColumn);
                hostDataColumn.addSubColumns(minuteColumn);
                this.registerColumn(insertFieldRelationManager(minuteColumn, columnKey), columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialSecondColumn(ICubeFieldSource field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation secondLocation = BIDateLocationTool.createSecond(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateSecond(field);
                BICubeSecondColumn secondColumn = new BICubeSecondColumn(discovery, secondLocation, hostDataColumn);
                hostDataColumn.addSubColumns(secondColumn);
                this.registerColumn(insertFieldRelationManager(secondColumn, columnKey), columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialMonthColumn(ICubeFieldSource field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation monthLocation = BIDateLocationTool.createMonth(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateMonth(field);
                BICubeMonthColumn monthColumn = new BICubeMonthColumn(discovery, monthLocation, hostDataColumn);
                hostDataColumn.addSubColumns(monthColumn);
                this.registerColumn(insertFieldRelationManager(monthColumn, columnKey),
                        columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialSeasonColumn(ICubeFieldSource field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation seasonLocation = BIDateLocationTool.createSeason(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateSeason(field);
                BICubeSeasonColumn seasonColumn = new BICubeSeasonColumn(discovery, seasonLocation, hostDataColumn);
                hostDataColumn.addSubColumns(seasonColumn);
                this.registerColumn(insertFieldRelationManager(seasonColumn, columnKey),
                        columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialWeekColumn(ICubeFieldSource field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation weekLocation = BIDateLocationTool.createWeek(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateWeek(field);
                BICubeWeekColumn weekColumn = new BICubeWeekColumn(discovery, weekLocation, hostDataColumn);
                hostDataColumn.addSubColumns(weekColumn);
                this.registerColumn(insertFieldRelationManager(weekColumn, columnKey),
                        columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialWeekNumberColumn(ICubeFieldSource field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation weekNumberLocation = BIDateLocationTool.createWeekNumber(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateWeekNumber(field);
                BICubeWeekNumberColumn weekNumberColumn = new BICubeWeekNumberColumn(discovery, weekNumberLocation, hostDataColumn);
                hostDataColumn.addSubColumns(weekNumberColumn);
                this.registerColumn(insertFieldRelationManager(weekNumberColumn, columnKey), columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialDayColumn(ICubeFieldSource field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation dayLocation = BIDateLocationTool.createDay(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateDay(field);
                BICubeDayColumn dayColumn = new BICubeDayColumn(discovery, dayLocation, hostDataColumn);
                hostDataColumn.addSubColumns(dayColumn);
                this.registerColumn(insertFieldRelationManager(dayColumn, columnKey),
                        columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialYearMonthDayHourColumn(ICubeFieldSource field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation yearMonthDayHourLocation = BIDateLocationTool.createYearMonthDayHour(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateYearMonthDayHour(field);
                BICubeYearMonthDayHourColumn yearMonthDayHourColumn = new BICubeYearMonthDayHourColumn(discovery, yearMonthDayHourLocation, hostDataColumn);
                hostDataColumn.addSubColumns(yearMonthDayHourColumn);
                this.registerColumn(insertFieldRelationManager(yearMonthDayHourColumn, columnKey), columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialYearMonthDayColumn(ICubeFieldSource field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation yearMonthDayLocation = BIDateLocationTool.createYearMonthDay(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateYearMonthDay(field);
                BICubeYearMonthDayColumn yearMonthDayColumn = new BICubeYearMonthDayColumn(discovery, yearMonthDayLocation, hostDataColumn);
                hostDataColumn.addSubColumns(yearMonthDayColumn);
                this.registerColumn(insertFieldRelationManager(yearMonthDayColumn, columnKey)
                        , columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialYearMonthColumn(ICubeFieldSource field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation yearMonthLocation = BIDateLocationTool.createYearMonth(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateYearMonth(field);
                BICubeYearMonthColumn yearMonthColumn = new BICubeYearMonthColumn(discovery, yearMonthLocation, hostDataColumn);
                hostDataColumn.addSubColumns(yearMonthColumn);
                this.registerColumn(insertFieldRelationManager(yearMonthColumn, columnKey), columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialYearMonthDayHourMinuteColumn(ICubeFieldSource field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation yearMonthDayHourMinuteLocation = BIDateLocationTool.createYearMonthDayHourMinute(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateYearMonthDayHourMinute(field);
                BICubeYearMonthDayHourMinuteColumn yearMonthDayHourMinuteColumn = new BICubeYearMonthDayHourMinuteColumn(discovery, yearMonthDayHourMinuteLocation, hostDataColumn);
                hostDataColumn.addSubColumns(yearMonthDayHourMinuteColumn);
                this.registerColumn(insertFieldRelationManager(yearMonthDayHourMinuteColumn, columnKey), columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialYearMonthDayHourMinuteSecondColumn(ICubeFieldSource field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation yearMonthDayHourMinuteSecondLocation = BIDateLocationTool.createYearMonthDayHourMinuteSecond(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateYearMonthDayHourMinuteSecond(field);
                BICubeYearMonthDayHourMinuteSecondColumn yearMonthDayHourMinuteSecondColumn = new BICubeYearMonthDayHourMinuteSecondColumn(discovery, yearMonthDayHourMinuteSecondLocation, hostDataColumn);
                hostDataColumn.addSubColumns(yearMonthDayHourMinuteSecondColumn);
                this.registerColumn(insertFieldRelationManager(yearMonthDayHourMinuteSecondColumn, columnKey), columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialYearSeasonColumn(ICubeFieldSource field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation yearMonthLocation = BIDateLocationTool.createYearSeacon(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateYearSeason(field);
                BICubeYearSeasonColumn yearSeasonColumn = new BICubeYearSeasonColumn(discovery, yearMonthLocation, hostDataColumn);
                hostDataColumn.addSubColumns(yearSeasonColumn);
                this.registerColumn(insertFieldRelationManager(yearSeasonColumn, columnKey), columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    private void initialYearWeekNumberColumn(ICubeFieldSource field, ITableKey tableKey, BICubeDateColumn hostDataColumn) {
        if (field.getFieldType() == DBConstant.COLUMN.DATE) {
            try {
                ICubeResourceLocation baseDataLocation = resourceRetrievalService.retrieveResource(tableKey, BIColumnKey.covertColumnKey(field));
                ICubeResourceLocation yearMonthLocation = BIDateLocationTool.createYearWeekNumber(baseDataLocation);
                BIColumnKey columnKey = BIDateColumnTool.generateYearWeekNumber(field);
                BICubeYearWeekNumberColumn yearWeekNumberColumn = new BICubeYearWeekNumberColumn(discovery, yearMonthLocation, hostDataColumn);
                hostDataColumn.addSubColumns(yearWeekNumberColumn);
                this.registerColumn(insertFieldRelationManager(yearWeekNumberColumn, columnKey), columnKey);
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl();
            }
        }
    }

    @Override
    public void clear() {
        for (ICubeColumnEntityService iCubeColumnEntityService : columnKey2ColumnMap.values()) {
            iCubeColumnEntityService.clear();
        }
    }

    public void buildStructure() {
        for (ICubeColumnEntityService columnEntityService : columnKey2ColumnMap.values()) {
            columnEntityService.buildStructure();
        }
    }

    @Override
    public void forceReleaseWriter() {
        for (ICubeColumnEntityService iCubeColumnEntityService : columnKey2ColumnMap.values()) {
            iCubeColumnEntityService.forceReleaseWriter();
        }
        columnKey2ColumnMap.clear();
    }

    @Override
    public void forceReleaseReader() {
        for (ICubeColumnEntityService iCubeColumnEntityService : columnKey2ColumnMap.values()) {
            iCubeColumnEntityService.forceReleaseReader();
        }
    }
}
