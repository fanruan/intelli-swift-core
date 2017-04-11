/**
 * Created by windy on 2017/4/10.
 */
BI.AnalysisETLOperatorAddColumnPaneTitleModel = BI.inherit(BI.OB, {

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorAddColumnPaneTitleModel.superclass._defaultConfig.apply(this, arguments), {
            
        })
    },

    update : function () {
        var o = this.options;
        return {
            fieldName : o.fieldName,
            fieldType : o.fieldType,
            add_column_type : o.add_column_type
        }
    },


    setName : function(name) {
        var o = this.options;
        o.fieldName = name;
    },

    getName : function() {
        var o = this.options;
        return o.fieldName;
    },

    setFieldType : function(v) {
        var o = this.options;
        o.fieldType = v;
    },

    getFieldType : function () {
        var o = this.options;
        return o.fieldType;
    },

    setAddColumnType : function (v) {
        var o = this.options;
        o.add_column_type = v;
    },

    getAddColumnType : function () {
        var o = this.options;
        return o.add_column_type;
    },

    get: function(key){
        return this.options[key];
    },

    set: function(key, value){
        this.options[key] = value;
    },

    getValue: function(key){
        return BI.deepClone(this.options[key]);
    },

    populate: function(model){
        var o = this.options;
        o = model || {};
        o.fieldType =  o.fieldType || BICst.COLUMN.NUMBER;
        o.add_column_type = o.add_column_type || BICst.ETL_ADD_COLUMN_TYPE.FORMULA;
    }

})