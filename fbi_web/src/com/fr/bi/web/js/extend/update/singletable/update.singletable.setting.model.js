/**
 * Created by Young's on 2016/4/25.
 */
BI.UpdateSingleTableSettingModel = BI.inherit(FR.OB, {
    _init: function(){
        BI.UpdateSingleTableSettingModel.superclass._init.apply(this, arguments);
        var updateSetting = this.options.update_setting;
        this.table = this.options.table;
        this.currentTable=this.options.currentTable;
        this.updateType = BICst.SINGLE_TABLE_UPDATE_TYPE.ALL;
        this.addSql = "";
        this.deleteSql = "";
        this.modifySql = "";
        this.togetherNever = BICst.SINGLE_TABLE_UPDATE.TOGETHER;
        this.timeList = [];
        if(BI.isNotNull(updateSetting)) {
            this.updateType = updateSetting.update_type;
            this.togetherNever = updateSetting.together_never;
            this.addSql = updateSetting.add_sql;
            this.deleteSql = updateSetting.delete_sql;
            this.modifySql = updateSetting.modify_sql;
            this.timeList = updateSetting.time_list;
        }

    },

    getId: function(){
        return this.table.id;
    },

    getTableName: function() {
        return this.table.table_name;
    },

    getFields: function(){
        return this.table.fields;
    },

    getUpdateType: function(){
        return this.updateType;
    },

    getAddSql: function(){
        return this.addSql;
    },

    getDeleteSql: function(){
        return this.deleteSql;
    },

    getModifySql: function(){
        return this.modifySql;
    },

    getTogetherNever: function(){
        return this.togetherNever;
    },

    getTimeList: function(){
        return this.timeList;
    },

    getTable: function(){
        return this.table;
    }
});
