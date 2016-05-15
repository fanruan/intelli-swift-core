/**
 * Created by Young's on 2016/3/19.
 */
BI.EditSQLModel = BI.inherit(FR.OB, {
    _init: function(){
        BI.EditSQLModel.superclass._init.apply(this, arguments);
        this.sql = this.options.sql || "";
        this.dataLinkName = this.options.dataLinkName || "";
        this.tableName = BI.i18nText("BI-Sql_DataSet");
    },

    initData: function(callback){
        var self = this;
        BI.Utils.getConnectionNames(function(names){
            self.connctionNames = names;
            BI.isEmptyString(self.dataLinkName) && (self.dataLinkName = names[0]);
            callback();
        })
    },

    getSQL: function(){
        //去掉最后加的“;”
        if(this.sql.endWith(";")){
            return this.sql.slice(0, this.sql.length - 1);
        }
        return this.sql;
    },

    setSQL: function(sql){
        this.sql = sql;
    },

    getDataLinkName: function(){
        return this.dataLinkName;
    },

    setDataLinkName: function(dataLinkName){
        this.dataLinkName = dataLinkName;
    },

    getTableName: function(){
        return this.tableName;
    },

    getFields: function(){
        return this.fields;
    },

    setFields: function(fields){

    },

    getConnectionNames: function(){
        return this.connctionNames;
    }
});