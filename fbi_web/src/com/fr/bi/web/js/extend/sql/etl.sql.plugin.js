/**
 * Created by Young's on 2016/3/19.
 */
BI.ETLSQL = BI.inherit(BI.ETL, {

    _constant: {
        SQL_LAYER: "__sql_layer__"
    },

    _init: function(){
        BI.ETLSQL.superclass._init.apply(this, arguments);
    },

    _populate: function(){
        BI.ETLSQL.superclass._populate.apply(this, arguments);
        var self = this;
        var allTables = this.model.getAllTables();
        if(allTables.length !== 1){
            return;
        }
        var finalTable = allTables[0][0];
        if(finalTable.connection_name !== BICst.CONNECTION.SQL_CONNECTION){
            return;
        }
        var reModifySQL = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Remodify_Sql"),
            width: 110,
            height: 26
        });
        reModifySQL.on(BI.Button.EVENT_CHANGE, function(){
            BI.Layers.remove(self._constant.SQL_LAYER);
            var sqlEditor = BI.createWidget({
                type: "bi.edit_sql",
                element: BI.Layers.create(self._constant.SQL_LAYER),
                sql: finalTable.sql,
                dataLinkName: finalTable.dataLinkName
            });
            BI.Layers.show(self._constant.SQL_LAYER);
            sqlEditor.on(BI.ExcelUpload.EVENT_CANCEL, function(){
                BI.Layers.remove(self._constant.SQL_LAYER);
            });
            sqlEditor.on(BI.ExcelUpload.EVENT_SAVE, function(data){
                BI.Layers.remove(self._constant.SQL_LAYER);
                self.model.setFields(data.fields);
                self.model.saveTableById(finalTable.id, data);
                self._populate();
            });
        });
        this.tableNameWrapper.addItem({
            el: reModifySQL,
            top: 0,
            right: 0
        });
    }
});
$.shortcut("bi.etl_sql", BI.ETLSQL);