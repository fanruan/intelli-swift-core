BI.AnalysisETLOperatorAddColumnAllFieldsModel = BI.inherit(BI.OB, {

    _init : function () {
        BI.AnalysisETLOperatorAddColumnAllFieldsModel.superclass._init.apply(this, arguments);

    },


    getFieldByValue : function (v) {
        return BI.find(this.get(ETLCst.FIELDS), function (idx, item) {
            return item.value === v;
        })
    },

    get: function(key){
        return this.options[key];
    },

    set: function(key, value){
        this.options[key] = value;
    },

    unset: function(key){
        delete this.options[key];
    },

    getValue: function(key){
        return BI.deepClone(this.options[key]);
    },

    populate: function(model){
        this.options = model || {};
        var fieldItems = [];
        BI.each(this.get(ETLCst.FIELDS) || [], function (idx, item) {
            fieldItems.push({
                text:item.fieldName,
                value:item.fieldName,
                fieldType:item.fieldType
            })
        })
        this.set(ETLCst.FIELDS, fieldItems);
    },

    update : function () {
        var v = BI.deepClone(this.options);
        delete v["fieldType"];
        delete v[ETLCst.FIELDS];
        return v;
    }

})