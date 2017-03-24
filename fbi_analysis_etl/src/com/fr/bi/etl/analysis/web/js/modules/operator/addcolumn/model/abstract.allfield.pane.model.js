BI.AnalysisETLOperatorAddColumnAllFieldsModel = BI.inherit(BI.MVCModel, {

    _init : function () {
        BI.AnalysisETLOperatorAddColumnAllFieldsModel.superclass._init.apply(this, arguments);
        var fieldItems = [];
        BI.each(this.get(ETLCst.FIELDS) || [], function (idx, item) {
            fieldItems.push({
                text:item.fieldName,
                value:item.fieldName,
                fieldType:item.fieldType,
            })
        })
        this.set(ETLCst.FIELDS, fieldItems)
    },


    getFieldByValue : function (v) {
        return BI.find(this.get(ETLCst.FIELDS), function (idx, item) {
            return item.value === v;
        })
    },

    update : function () {
        var v = BI.AnalysisETLOperatorAddColumnAllFieldsModel.superclass.update.apply(this, arguments);
        delete v["fieldType"];
        delete v[ETLCst.FIELDS];
        return v;
    }

})