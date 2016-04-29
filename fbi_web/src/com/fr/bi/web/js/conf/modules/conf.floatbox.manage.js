BIConf.FloatBoxes = new (BI.inherit( BI.FloatBoxRouter, {
    routes: {
        "/fbi_Data_Connection_Man/:id" : "BIConf.AddDataLinkView",
        "/fbi_Data_Connection_Man/:id/error" : "BIConf.SP4TestDataLinkErrorView",

        "/BI-Packages_Man/:group/:pack/:etl/:tId/addField/:id": "BIConf.ETLAddFormulaFieldFloatboxView",
        "/BI-Packages_Man/:group/:pack/:etl/:id/statistic/:region/:type/:dId/numberCustomGroup": "BIConf.NumberCustomGroupView",
        "/BI-Packages_Man/:group/:pack/:etl/:id/statistic/:region/:type/:dId/CustomGroup": "BIConf.CustomGroupView",
        "/BI-Packages_Man/:group/:pack/:etl/:tId/addgroupfield/:id":"BIConf.ETLAddGroupFieldFloatBoxView",
        "/BI-Packages_Man/:group/:pack/:tableId/:fieldName": "BIConf.FieldRelationSetView"

    }
}));