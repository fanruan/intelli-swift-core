/**
 * Created by 小灰灰 on 2016/4/11.
 */
BI.SQLReq = {
    reqSaveTable: function(data, callback){
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_sql", "save_table", data, function(res){
            BI.Utils.afterSaveSQLTable(res);
            callback();
        })
    },

    reqRenameTable: function(data, callback){
        var d = BI.deepClone(data);
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_sql", "rename_table", data, function(){
            BI.Utils.afterReNameSQLTable(d.id, d.name, d.describe);
            callback();
        })
    },

    reqDeleteTable: function(data, callback){
        data.sessionID = Data.SharingPool.get("sessionID");
        var mask = BI.createWidget({
            type: "bi.sql_loading_mask",
            masker: 'body',
            text: BI.i18nText("BI-Loading")
        });
        BI.requestAsync("fr_bi_analysis_sql", "get_used_tables", data, function(res){
            if(mask != null) {
                mask.destroy()
            }
            var text = BI.isNull(res.table) ? BI.i18nText('BI-Is_Delete_Table') : BI.i18nText('BI-ETL_Sure_Delete_Used_Table', res.table);
            BI.Msg.confirm(BI.i18nText("BI-Warning"), text, function (v) {
                if(v === true) {
                    BI.requestAsync("fr_bi_analysis_sql", "delete_table", data, function(){
                        BI.Utils.afterDeleteSQLTable(data.id);
                        callback();
                    })
                }
            })
        })
    },

    reqEditTable: function(data, callback){
        data.sessionID = Data.SharingPool.get("sessionID");
        var mask = BI.createWidget({
            type: "bi.sql_loading_mask",
            masker: 'body',
            text: BI.i18nText("BI-Loading")
        });
        BI.requestAsync("fr_bi_analysis_sql", "edit_table", data, function(res){
            if(mask != null) {
                mask.destroy()
            }
            if(res['used']){
                BI.Msg.confirm(BI.i18nText("BI-Warning"), BI.i18nText("BI-ETL_Table_Edit_Warning"), function (v) {
                    if(v === true) {
                        callback(res);
                    }
                })
            } else {
                callback(res);
            }
        })
    },

    reqPreviewTable: function(data, callback){
        data.sessionID = Data.SharingPool.get("sessionID");
        if(data[SQLCst.ITEMS][0][SQLCst.FIELDS].length === 0){
            callback({
                value:[]
            });
            return;
        }
        BI.requestAsync("fr_bi_analysis_sql", "preview_table", data, function (res)                                                    {
            callback(res);
        })
    },

    reqFieldValues: function (data, callback) {
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_sql", "get_field_value", data, function (res) {
            callback(res);
        });
    },

    reqFieldMinMaxValues: function (data, callback) {
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_sql", "get_field_min_max_value", data, function (res) {
            callback(res);
        });
    },

}