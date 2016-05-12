/**
 * Created by 小灰灰 on 2016/4/11.
 */
BI.ETLReq = {
    reqSaveTable: function(data, callback){
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_etl", "save_table", data, function(res){
            callback();
        })
    },

    reqRenameTable: function(data, callback){
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_etl", "rename_table", data, function(res){
            callback();
        })
    },

    reqCopyTable: function(data, callback){
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_etl", "copy_table", data, function(res){
            callback();
        })
    },

    reqDeleteTable: function(data, callback){
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_etl", "delete_table", data, function(res){
            callback();
        })
    },

    reqEditTable: function(data, callback){
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_etl", "edit_table", data, function(res){
            callback();
        })
    },

    reqFieldValues: function (data, callback) {
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_etl", "get_field_value", data, function (res) {
            callback(res);
        });
    },
}