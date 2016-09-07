BI.AnalysisETLOperatorAddColumnAllFieldsModel = BI.inherit(BI.MVCModel, {

    _init : function () {
        BI.AnalysisETLOperatorAddColumnAllFieldsModel.superclass._init.apply(this, arguments);
        var fieldItems = [];
        BI.each(this.get(SQLCst.FIELDS) || [], function (idx, item) {
            fieldItems.push({
                text:item.field_name,
                value:item.field_name,
                fieldType:item.field_type,
            })
        })
        this.set(SQLCst.FIELDS, fieldItems)
    },


    getFieldByValue : function (v) {
        return BI.find(this.get(SQLCst.FIELDS), function (idx, item) {
            return item.value === v;
        })
    },

    update : function () {
        var v = BI.AnalysisETLOperatorAddColumnAllFieldsModel.superclass.update.apply(this, arguments);
        delete v["field_type"];
        delete v[SQLCst.FIELDS];
        return v;
    }

})