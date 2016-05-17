/**
 * Created by 小灰灰 on 2016/4/11.
 */
BI.ETLReq = {
    reqSaveTable: function(data, callback){
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_etl", "save_table", data, function(res){
            BI.Utils.afterSaveTable(res);
            callback();
        })
    },

    reqRenameTable: function(data, callback){
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_etl", "rename_table", data, function(){
            BI.Utils.afterReNameTable(data.id, data.name);
            callback();
        })
    },

    reqDeleteTable: function(data, callback){
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_etl", "delete_table", data, function(){
            BI.Utils.afterDeleteTable(data.id);
            callback();
        })
    },

    reqEditTable: function(data, callback){
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_etl", "edit_table", data, function(res){
            callback(res);
        })
    },

    reqPreviewTable: function(data, callback){
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_etl", "preview_table", data, function (res) {
            callback(res);
        })
    },

    reqFieldValues: function (data, callback) {
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_etl", "get_field_value", data, function (res) {
            callback(res);
        });
    },
}