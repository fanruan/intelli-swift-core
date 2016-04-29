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
                filter_value: this.getFilterValue()[0]
            },
            connection_name: BICst.CONNECTION.ETL_CONNECTION,
            tables: [this.getTableStructure()]
        };
    },

    isCubeGenerated: function () {
        return this.isGenerated;
    },

    populate: function (info) {
        var etlValue = info.tableInfo.etl_value;
        this.reopen = info.reopen;
        this.filter_value = info.reopen === true ? [etlValue.filter_value] : [];
        this.tables = info.tableInfo.tables;
        this.old_tables = BI.extend(info.tableInfo, {id: BI.UUID()});
        this.isGenerated = info.isGenerated;
    }
});