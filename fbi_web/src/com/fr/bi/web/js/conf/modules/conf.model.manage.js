//Fine BIService
BIConf.Models = new (BI.inherit( BI.WRouter, {
    routes: {
        "" : "index",
        "/BI-Packages_Man/:group/:pack/:etl" : "tableInfo"
    },

    index: function(){
        var _getDefaultName = function(){
            //数据连接为空时则默认显示数据连接界面
            var allConnectionName = BI.requestSync("fr_bi_configure", "get_data_link",{
                discard_server: true
            });
            if( BI.isEmpty(allConnectionName)) {
                return "BI-Data_Connection_Man";
            } else {
                return "BI-Packages_Man";
            }
        };
        return {
            "default": _getDefaultName()
        }
    }


}));
