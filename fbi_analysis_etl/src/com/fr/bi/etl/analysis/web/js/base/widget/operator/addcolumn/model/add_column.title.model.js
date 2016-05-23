
BI.AnalysisETLOperatorAddColumnPaneTitleModel = BI.inherit(BI.Model, {

    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorAddColumnPaneTitleModel.superclass._defaultConfig.apply(this, arguments), {
            field_name : null,
            field_type : BICst.COLUMN.NUMBER,
            add_column_type : BICst.ETL_ADD_COLUMN_TYPE.FORMULA
        })
    },

    update : function () {
        var o = this.options;
        return {
            field_name : o.field_name,
            field_type : o.field_type,
            add_column_type : o.add_column_type
        }
    },


    setName : function(name) {
        var o = this.options;
        o.field_name = name;
    },

    getName : function() {
        var o = this.options;
        return o.field_name;
    },

    setFieldType : function(v) {
        var o = this.options;
        o.field_type = v;
    },

    getFieldType : function () {
        var o = this.options;
        return o.field_type;
    },

    setAddColumnType : function (v) {
        var o = this.options;
        o.add_column_type = v;
    },

    getAddColumnType : function () {
        var o = this.options;
        return o.add_column_type;
    }

})