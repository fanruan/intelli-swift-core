BI.AnalysisETLOperatorUsePartPaneModel = BI.inherit(BI.MVCModel, {
    
    check : function () {
        var parent = this.get(ETLCst.PARENTS)[0];
        var operator = this.get('operator') || {};
        var newFields = [];
        BI.each(parent[ETLCst.FIELDS], function (i, item) {
            if (BI.indexOf(operator.value, item.field_name) === -1){
                newFields.push(item);
            }
        })
        this.set(ETLCst.FIELDS, newFields)
        return [false, ""]
    }
})

ETLCst.OPERATOR_MODEL_CLASS[ETLCst.ANALYSIS_ETL_PAGES.USE_PART_FIELDS] = BI.AnalysisETLOperatorUsePartPaneModel;