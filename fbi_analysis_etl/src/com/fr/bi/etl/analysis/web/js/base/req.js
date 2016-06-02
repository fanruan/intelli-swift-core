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
        var d = BI.deepClone(data);
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_etl", "rename_table", data, function(){
            BI.Utils.afterReNameTable(d.id, d.name, d.describe);
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
        if(data[ETLCst.ITEMS][0][ETLCst.FIELDS].length === 0){
            callback({
                value:[]
            });
            return;
        }
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

    reqFieldMinMaxValues: function (data, callback) {
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_etl", "get_field_min_max_value", data, function (res) {
            callback(res);
        });
    },


    reqTableStatus: function (data, callback) {
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_etl", "get_cube_status", data, function (res) {
            callback(res);
        });
    }
}