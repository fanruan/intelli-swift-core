/**
 * Created by windy on 2017/4/10.
 */
BI.AnalysisETLOperatorDatePaneModel = BI.inherit(BI.OB, {

    //这get, set, getValue适配原有的结构三个方法，会删
    get: function(key){
        return this.options[key];
    },

    set: function(key, value){
        this.options[key] = value;
    },

    getCopyValue: function(key){
        return BI.deepClone(this.options[key]);
    },

    update: function(){
        return BI.deepClone(this.options);
    },

    populate: function(model){
        this.options = model || {};
    }
});