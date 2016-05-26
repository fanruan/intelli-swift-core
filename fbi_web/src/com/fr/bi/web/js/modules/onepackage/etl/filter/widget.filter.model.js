/**
 * @class BI.FilterDataModel
 * @extends BI.Widget
 * @author windy
 */
BI.FilterDataModel = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.FilterDataModel.superclass._defaultConfig.apply(this, arguments), {
            info: {}
        })
    },

    _init: function () {
        BI.FilterDataModel.superclass._init.apply(this, arguments);
        this.populate(this.options.info);
    },

    getDefaultTableName: function () {
        var self = this;
        if (BI.isNotNull(this.old_tables.table_name)) {
            this.tmp_operatorTableName = this.old_tables.table_name;
            return this.old_tables.table_name + "_filter";
        }
        var tables = this.tables;
        var tableName = [];

        function getDefaultName(tables) {
            //只取tables[0]
            if (BI.isNotNull(tables[0].etl_type)) {
                tableName.push("_" + tables[0].etl_type);
                getDefaultName(tables[0].tables);
            } else {
                tableName.push(tables[0].table_name);
            }
        }

        getDefaultName(tables);
        //反向遍历
        tableName.reverse();
        var tableNameString = "";
        BI.each(tableName, function (i, name) {
            tableNameString += name;
        });
        if (self.reopen === true) {
            this.tmp_operatorTableName = tableNameString;
            tableNameString += "_filter";
        } else {
            this.tmp_operatorTableName = tableNameString + "_" + self.old_tables.etl_type;
            tableNameString = tableNameString + "_" + self.old_tables.etl_type + "_filter";
        }
        return tableNameString;
    },

    getTablesDetailInfoByTables: function (callback) {
        BI.Utils.getTablesDetailInfoByTables([this.getTableStructure()], function (data) {
            callback(data);
        });
    },

    /**
     * 预览时所需数据参数
     */
    getPreTableStructure: function () {
        return this.old_tables;
    },

    getTableStructure: function () {
        var tables = {};
        if (this.reopen === true) {
            tables = this.tables[0];
        } else {
            tables = this.old_tables;
        }
        return tables;
    },

    getOperatorTableName: function () {
        return this.tmp_operatorTableName;
    },

    setFilterValue: function (filter_value) {
        this.filter_value = filter_value;
    },

    getFilterValue: function () {
        return BI.deepClone(this.filter_value);
    },

    getValue: function () {
        return {
            etl_type: "filter",
            etl_value: {
                filter_value: this.parseFilter(this.getFilterValue()[0])
            },
            connection_name: BICst.CONNECTION.ETL_CONNECTION,
            tables: [this.getTableStructure()]
        };
    },

    isCubeGenerated: function () {
        return this.isGenerated;
    },

    populate: function (info) {
        var etlValue = info.tableInfo.etl_value || {};
        var filter_value = etlValue.filter_value || {};
        this.reopen = info.reopen;
        this.filter_value = info.reopen === true ? [filter_value] : [];
        this.tables = info.tableInfo.tables;
        this.old_tables = BI.extend(info.tableInfo, {id: BI.UUID()});
        this.isGenerated = info.isGenerated;
    },

    parseFilter: function (filter) {
        var self = this;
        var filterType = filter.filter_type, filterValue = filter.filter_value;
        if (BI.isEmptyObject(filterValue)) {
            return;
        }
        if (filterType === BICst.FILTER_TYPE.AND || filterType === BICst.FILTER_TYPE.OR) {
            BI.each(filterValue, function (i, value) {
                self.parseFilter(value);
            });
        }
        if (filterType === BICst.FILTER_DATE.BELONG_DATE_RANGE || filterType === BICst.FILTER_DATE.NOT_BELONG_DATE_RANGE) {
            var start = filterValue.start, end = filterValue.end;
            if (BI.isNull(start)) {
                delete filterValue.start;
            }
            if (BI.isNull(end)) {
                delete filterValue.end;
            }
        }

        if (filterType === BICst.FILTER_DATE.EQUAL_TO || filterType === BICst.FILTER_DATE.NOT_EQUAL_TO) {
            filterValue.values = parseComplexDate(filterValue);
            filterValue.type = filterValue.type || BICst.MULTI_DATE_CALENDAR
        }
        return filter;

        function parseComplexDate(v){
            var type = v.type, value = v.value;
            var date = new Date();
            var currY = date.getFullYear(), currM = date.getMonth(), currD = date.getDate();
            var tool = new BI.MultiDateParamTrigger();
            if (BI.isNull(type) && BI.isNotNull(v.year)) {
                return new Date(v.year, v.month, v.day).getTime();
            }
            switch (type) {
                case BICst.MULTI_DATE_YEAR_PREV:
                    return new Date(currY - 1 * value, currM, currD).getTime();
                case BICst.MULTI_DATE_YEAR_AFTER:
                    return new Date(currY + 1 * value, currM, currD).getTime();
                case BICst.MULTI_DATE_YEAR_BEGIN:
                    return new Date(currY, 1, 1).getTime();
                case BICst.MULTI_DATE_YEAR_END:
                    return new Date(currY, 11, 31).getTime();
                case BICst.MULTI_DATE_MONTH_PREV:
                    return tool._getBeforeMultiMonth(value).getTime();
                case BICst.MULTI_DATE_MONTH_AFTER:
                    return tool._getAfterMultiMonth(value).getTime();
                case BICst.MULTI_DATE_MONTH_BEGIN:
                    return new Date(currY, currM, 1).getTime();
                case BICst.MULTI_DATE_MONTH_END:
                    return new Date(currY, currM, (date.getLastDateOfMonth()).getDate()).getTime();
                case BICst.MULTI_DATE_QUARTER_PREV:
                    return tool._getBeforeMulQuarter(value).getTime();
                case BICst.MULTI_DATE_QUARTER_AFTER:
                    return tool._getAfterMulQuarter(value).getTime();
                case BICst.MULTI_DATE_QUARTER_BEGIN:
                    return tool._getQuarterStartDate().getTime();
                case BICst.MULTI_DATE_QUARTER_END:
                    return tool._getQuarterEndDate().getTime();
                case BICst.MULTI_DATE_WEEK_PREV:
                    return date.getOffsetDate(-7 * value).getTime();
                case BICst.MULTI_DATE_WEEK_AFTER:
                    return date.getOffsetDate(7 * value).getTime();
                case BICst.MULTI_DATE_DAY_PREV:
                    return date.getOffsetDate(-1 * value).getTime();
                case BICst.MULTI_DATE_DAY_AFTER:
                    return date.getOffsetDate(1 * value).getTime();
                case BICst.MULTI_DATE_DAY_TODAY:
                    return date.getTime();
                case BICst.MULTI_DATE_CALENDAR:
                    return new Date(value.year, value.month, value.day).getTime();
            }
        }
    }
});