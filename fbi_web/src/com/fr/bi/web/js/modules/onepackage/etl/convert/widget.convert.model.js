/**
 * @class BI.ConvertModel
 * @extends BI.Widget
 * @author windy
 */
BI.ConvertModel = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ConvertModel.superclass._defaultConfig.apply(this, arguments), {
            info: {}
        })
    },

    _init: function () {
        BI.ConvertModel.superclass._init.apply(this, arguments);
        this.populate(this.options.info);
    },

    getDefaultTableName: function () {
        var self = this;
        if (BI.isNotNull(this.old_tables.table_name)) {
            return this.old_tables.table_name + "_convert";
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
            tableNameString += "_convert";
        } else {
            tableNameString = tableNameString + "_" + self.old_tables.etl_type + "_convert";
        }
        return tableNameString;
    },

    /**
     * 预览时所需数据参数
     */
    getPreTableStructure: function () {
        return this.old_tables;
    },

    getId: function () {
        return this.id;
    },

    getTablesDetailInfoByTables: function (callback) {
        BI.Utils.getTablesDetailInfoByTables([this.getTableStructure()], function (data) {
            callback(data);
        });
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

    setLCValue: function (value) {
        this.lc_values = value;
    },

    getLCValue: function () {
        return BI.clone(this.lc_values);
    },

    setGroupName: function (value) {
        this.group_name = value;
    },

    getGroupName: function () {
        return this.group_name;
    },

    getAllFields: function () {
        return BI.deepClone(this.old_tables.fields);
    },

    setColumns: function (value) {
        this.columns = value;
    },

    getColumns: function () {
        return BI.clone(this.columns);
    },

    setLCName: function (value) {
        this.lc_name = value;
    },

    getLCName: function () {
        return this.lc_name;
    },

    getValue: function () {
        var self = this;
        var tId = this.id, translations = this.getTranslations();

        var transText = [], text = [];
        var assertArray = function (array) {
            if (BI.isEmpty(array[1])) {
                array[1] = array[0];
            }
            return array;
        };

        BI.each(this.getLCValue(), function (idx, lc) {
            lc = assertArray(lc);
            BI.each(self.getColumns(), function (id, co) {
                co = assertArray(co);
                transText.push(lc[1] + "-" + co[1]);
                text.push(lc[0] + "-" + co[0]);
            });
        });

        BI.each(transText, function (idx, name) {
            if (!BI.contains(text, name)) {
                translations[tId + text[idx]] = name;
            }
        });

        return {
            translations: translations,
            etl_type: "convert",
            etl_value: {
                lc_values: this.lc_values,
                group_name: this.group_name,
                columns: this.columns,
                lc_name: this.lc_name
            },
            connection_name: BICst.CONNECTION.ETL_CONNECTION,
            tables: [this.getTableStructure()]
        };
    },

    getTranslations: function () {
        return BI.deepClone(this.translations);
    },

    isCubeGenerated: function () {
        return this.isGenerated;
    },

    populate: function (info) {
        var etlValue = info.tableInfo.etl_value;
        this.id = info.id;
        this.reopen = info.reopen;
        this.lc_values = info.reopen === true ? etlValue.lc_values : [];
        this.group_name = info.reopen === true ? etlValue.group_name : "";
        this.columns = info.reopen === true ? etlValue.columns : [];
        this.lc_name = info.reopen === true ? etlValue.lc_name : "";
        this.tables = info.tableInfo.tables;
        this.translations = info.translations;
        this.old_tables = BI.extend(info.tableInfo, {id: BI.UUID()});
        this.isGenerated = info.isGenerated;
    }
});