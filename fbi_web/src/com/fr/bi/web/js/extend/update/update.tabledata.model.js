/**
 * Created by Young's on 2016/4/22.
 */
BI.UpdateTableDataModel = BI.inherit(FR.OB, {
    _init: function(){
        BI.UpdateTableDataModel.superclass._init.apply(this, arguments);
        this.table = this.options.table;
    },

    getId: function(){
        return this.table.id;
    },

    getTableName: function() {
        var translations = this.table.translations;
        var id = this.table.id;
        return translations[id];
    },

    getSourceTables: function(){
        //找到所有基础表
        var sourceTables = [];
        function getbts(table){
            if(BI.isNotNull(table.etl_type)) {
                BI.each(table.tables, function(i, t){
                    getbts(t);
                })
            } else {
                sourceTables.push(table);
            }
        }
        getbts(this.table);
        return sourceTables;
    },

    getSourceTableIds: function(){
        var tables = this.getSourceTables();
        var tableIds = [];
        BI.each(tables, function(i, table){
            tableIds.push(table.md5);
        });
        return tableIds;
    },

    getUpdateSettingBySourceTableId: function(sourceTableId) {
        var updateSettings = this.table.update_settings;
        return updateSettings[sourceTableId];
    },

    getTableBySourceTableId: function(sourceTableId) {
        var sourceTable = {};
        BI.each(this.getSourceTables(), function(i, table) {
            if(table.md5 === sourceTableId) {
                sourceTable = table;
            }
        });
        return sourceTable;
    },

    getAllSettings: function(){
        return this.table.update_settings;
    }
});