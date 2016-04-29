/**
 * Created by roy on 16/3/17.
 */
BI.AddFormulaFieldModel = BI.inherit(FR.OB, {
    _init: function () {
        BI.AddFormulaFieldModel.superclass._init.apply(this, arguments);
        var o = this.options;
        var info = o.info;
        this.id = info.id;
        this.tableInfo = info.tableInfo;
        this.reopen = info.reopen;
        this.isGenerated = info.isGenerated;
    },

    initData: function (callback) {
        var self = this;
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Loading")
        });

        //第一次打开用原始表,其他的用上次elt操作之后的table
        BI.Utils.getTablesDetailInfoByTables(this.isReopen() ? [(this.getTableInfo().tables)[0]] : [this.getTableInfo()], function (data) {
            self.fields = data[0].fields;
            if (self.isReopen()) {
                self.formulaData = {};
                var etl_value = self.getTableInfo().etl_value;
                self.formulas = etl_value.formulas;
                if (BI.isNotNull(self.formulas)) {
                    BI.each(self.formulas, function (i, formulaObj) {
                        var id = BI.UUID();
                        self.formulaData[id] = {};
                        self.formulaData[id].formula = formulaObj;
                    });
                }
            } else {
                self.formulaData = {};
            }
            callback();
            mask.destroy();
        });


    },

    isReopen: function () {
        return BI.deepClone(this.reopen);
    },

    isCubeGenerated: function () {
        return this.isGenerated;
    },

    setFormulaData: function (data) {
        this.formulaData = data;
    },

    setGenerated: function (v) {
        this.isGenerated = v;
    },


    getTableInfo: function () {
        return BI.deepClone(this.tableInfo);
    },

    getFields: function () {
        return BI.deepClone(this.fields);
    },

    getAllTables: function () {
        var tables = [this.getTableInfo()];
        if (this.isReopen() === true) {
            tables = this.getTableInfo().tables;
        }
        return tables;
    },

    getFormulaData: function () {
        return BI.deepClone(this.formulaData);
    },

    checkSaveEnable: function () {
        var formulaData = this.getFormulaData();
        return BI.size(formulaData) > 0;
    },

    getDefaultTableName: function () {
        var self = this;
        var reopen = this.isReopen();
        var tableInfo = this.getTableInfo();
        if (BI.isNotNull(tableInfo.table_name)) {
            return tableInfo.table_name + "_formula";
        }
        var tables = tableInfo.tables;
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
        if (reopen === true) {
            tableNameString += "_formula";
        } else {
            tableNameString = tableNameString + "_" + tableInfo.etl_type + "_formula";
        }
        return tableNameString;
    }
});