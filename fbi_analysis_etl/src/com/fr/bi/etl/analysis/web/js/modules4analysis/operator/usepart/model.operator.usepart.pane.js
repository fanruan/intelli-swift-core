/**
 * Created by windy on 2017/4/7.
 */
BI.AnalysisETLOperatorUsePartPaneModel = BI.inherit(BI.OB, {

    //这get, set, getValue适配原有的结构三个方法，会删
    get: function(key){
        return this.options[key];
    },

    set: function(key, value){
        this.options[key] = value;
    },

    getValue: function(key){
        return BI.deepClone(this.options[key]);
    },

    update: function(){
        return BI.deepClone(this.options);
    },

    check : function () {
        var parent = this.get(ETLCst.PARENTS)[0];
        var operator = this.get('operator') || {};
        var newFields = [];
        BI.each(parent[ETLCst.FIELDS], function (i, item) {
            if (BI.indexOf(operator.value, item.fieldName) === -1){
                newFields.push(item);
            }
        });
        this.set(ETLCst.FIELDS, newFields);
        return [false, ""]
    },

    populate: function(model){
        this.options = model || {};
    }
});

ETLCst.OPERATOR_MODEL_CLASS[ETLCst.ANALYSIS_ETL_PAGES.USE_PART_FIELDS] = BI.AnalysisETLOperatorUsePartPaneModel;