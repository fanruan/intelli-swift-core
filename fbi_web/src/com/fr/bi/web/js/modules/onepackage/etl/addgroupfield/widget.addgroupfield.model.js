/**
 * Created by roy on 16/3/15.
 */
BI.AddGroupFieldModel = BI.inherit(FR.OB, {
    _init: function () {
        BI.AddGroupFieldModel.superclass._init.apply(this, arguments);
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
        this.newGroupsData = {};
        //第一次打开用原始表,其他的用上次elt操作之后的table
        BI.Utils.getTablesDetailInfoByTables(this.isReopen() ? [(this.getTableInfo().tables)[0]] : [this.getTableInfo()], function (data) {
            self.fields = data[0].fields;
            var newGroupsData = {};
            if (self.isReopen()) {
                var etl_value = self.getTableInfo().etl_value;
                BI.each(etl_value.new_groups, function (i, newGroupObj) {
                    var id = BI.UUID();
                    newGroupsData[id] = newGroupObj
                });
                self.newGroupsData = newGroupsData;
            }
            callback();
            mask.destroy();
        });


    },

    isCubeGenerated: function () {
        return this.isGenerated;
    },

    setGenerated: function (v) {
        this.isGenerated = v;
    },

    getFields: function () {
        return BI.deepClone(this.fields);
    },

    getTableInfo: function () {
        return BI.deepClone(this.tableInfo);
    },


    getNewGroupsData: function () {
        return BI.deepClone(this.newGroupsData);
    },

    isReopen: function () {
        return this.reopen;
    },

    getAllTables: function () {
        var tables = [this.getTableInfo()];
        if (this.isReopen() === true) {
            tables = this.getTableInfo().tables;
        }
        return tables;
    },

    checkSaveEnable: function () {
        var groupsData = this.getNewGroupsData();
        if (BI.size(groupsData) > 0) {
            return true
        } else {
            return false
        }
    },

    getDefaultTableName: function () {
        var self = this;
        var tableInfo = this.getTableInfo();
        if (BI.isNotNull(tableInfo.table_name)) {
            return tableInfo.table_name + "_new_group";
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
        if (this.isReopen() === true) {
            tableNameString += "_new_group";
        } else {
            tableNameString = tableNameString + "_" + tableInfo.etl_type + "_new_group";
        }
        return tableNameString;
    },

    setNewGroupsData: function (data) {
        this.newGroupsData = data;
    }


})