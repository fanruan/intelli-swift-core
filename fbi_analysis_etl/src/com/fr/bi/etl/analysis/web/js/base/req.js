/**
 * Created by 小灰灰 on 2016/4/11.
 */
BI.ETLReq = {
    reqSaveTable: function(data, callback){
        data.sessionID = Data.SharingPool.get("sessionID");
        BI.requestAsync("fr_bi_analysis_etl", "save_table", data, function(){
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