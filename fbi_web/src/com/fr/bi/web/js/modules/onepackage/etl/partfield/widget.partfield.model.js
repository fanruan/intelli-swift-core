/**
 * @class BI.PartFieldModel
 * @extends BI.Widget
 * @author windy
 */
BI.PartFieldModel = BI.inherit(BI.Widget, {

    _defaultConfig: function(){
        return BI.extend(BI.PartFieldModel.superclass._defaultConfig.apply(this, arguments), {
            info: {}
        })
    },

    _init: function(){
        BI.PartFieldModel.superclass._init.apply(this, arguments);
        this.populate(this.options.info);
    },

    getDefaultTableName: function(){
        var self = this;
        if(BI.isNotNull(this.old_tables.table_name)){
            return this.old_tables.table_name + "_partial";
        }
        var tables = this.tables;
        var tableName = [];
        function getDefaultName(tables){
            //只取tables[0]
            if(BI.isNotNull(tables[0].etl_type)){
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
        BI.each(tableName, function(i, name){
            tableNameString += name;
        });
        if(self.reopen === true){
            tableNameString += "_partial";
        } else {
            tableNameString = tableNameString + "_" + self.old_tables.etl_type + "_partial";
        }
        return tableNameString;
    },

    /**
     * 预览时所需数据参数
     */
    getPreTableStructure: function(){
        return this.old_tables;
    },

    getTableStructure: function(){
        var tables = {};
        if(this.reopen === true){
            tables = this.tables[0];
        } else {
            tables = this.old_tables;
        }
        return tables;
    },

    getTablesDetailInfoByTables: function(callback){
        BI.Utils.getTablesDetailInfoByTables([this.getTableStructure()], function(data){
            callback(data);
        });
    },

    setFieldState: function(values){
        var fieldState = [];
        BI.each(values["notselectField"], function (idx, value) {
            fieldState.push({
                checked: false,
                field_name: value
            });
        });
        BI.each(values["selectField"], function (idx, value) {
            fieldState.push({
                checked: true,
                field_name: value
            });
        });
        this.fields_state = fieldState;
    },

    getFieldState: function () {
        return this.fields_state;
    },

    getValue: function () {
        return {
            etl_type: "partial",
            etl_value: {
                fields_state: this.getFieldState()
            },
            connection_name: BICst.CONNECTION.ETL_CONNECTION,
            tables: [this.getTableStructure()]
        }
    },

    isCubeGenerated: function(){
        return this.isGenerated;
    },

    getRelations: function () {
        return this.relations;
    },

    populate: function (info) {
        var etlValue = info.tableInfo.etl_value;
        this.reopen = info.reopen;
        this.fields_state = info.reopen === true ? etlValue.fields_state : [];
        this.tables = info.tableInfo.tables;
        this.old_tables = BI.extend(info.tableInfo, {id: BI.UUID()});
        this.isGenerated = info.isGenerated;
        this.relations = info.relations;
    }
});