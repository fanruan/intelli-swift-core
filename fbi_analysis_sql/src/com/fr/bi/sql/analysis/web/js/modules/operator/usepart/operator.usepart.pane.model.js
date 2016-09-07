BI.AnalysisETLOperatorUsePartPaneModel = BI.inherit(BI.MVCModel, {
    
    check : function () {
        var parent = this.get(SQLCst.PARENTS)[0];
        var operator = this.get('operator') || {};
        var newFields = [];
        BI.each(parent[SQLCst.FIELDS], function (i, item) {
            if (BI.indexOf(operator.value, item.field_name) === -1){
                newFields.push(item);
            }
        })
        this.set(SQLCst.FIELDS, newFields)
        return [false, ""]
    }
})

SQLCst.OPERATOR_MODEL_CLASS[SQLCst.ANALYSIS_ETL_PAGES.USE_PART_FIELDS] = BI.AnalysisETLOperatorUsePartPaneModel;