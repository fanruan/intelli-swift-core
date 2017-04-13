/**
 * Created by windy on 2017/4/5.
 */
BI.AnalysisETLOperatorFilterPaneModel = BI.inherit(BI.OB, {

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

    update : function () {
        return BI.deepClone(this._toJSON(this.options));
    },

    _init: function () {
        BI.AnalysisETLOperatorFilterPaneModel.superclass._init.apply(this, arguments);
        var o = this.options;
        this.populate(o.model);
    },

    _toJSON : function (attr) {
        var ob = {};
        var self  = this;
        BI.each(attr, function (idx, item) {
            ob[idx] = BI.isNotNull(item) && BI.isFunction(item.toJSON) ? self._toJSON(item.toJSON()) :item;
        })
        return ob;
    },

    check : function () {
        var self = this;
        var parent = this.get(ETLCst.PARENTS)[0];
        var operator =  this.get('operator');
        var items =operator[ETLCst.ITEMS];
        var newItems = [];
        var invalid = [false];
        BI.each(items, function (i, item) {
            var field = BI.find(parent[ETLCst.FIELDS], function (i, field) {
                return field.fieldName === item.fieldName;
            })
            if (BI.isNotNull(field)){
                newItems.push(item);
                if (!invalid[0]){
                    if (field.fieldType !== item.fieldType){
                        invalid = [true, BI.i18nText('BI-Basic_Filter') + field.fieldName + BI.i18nText('BI-Illegal_Field_Type')];
                    } else {
                        invalid = self._checkItem(item, parent[ETLCst.FIELDS]);
                    }
                }
            }
        });
        operator.items = newItems;
        return invalid;
    },

    _checkItem : function (item, fields) {
        var msg = "";
        var invalid =  BI.some(item.value, function (i, v) {
            if (v.filterType === BICst.FILTER_TYPE.FORMULA ) {
                var fs = BI.Utils.getFieldsFromFormulaValue(v.filterValue);
                var lostField = BI.find(fs, function (i, field) {
                    return BI.isNull(BI.find(fields, function (idx, f) {
                        return f.fieldName === field;
                    }))
                })
                if (BI.isNotNull(lostField)){
                    msg = BI.i18nText('BI-Basic_Filter')  + BI.i18nText('BI-Formula_Valid') + lostField
                    return true;
                }
            }
        })
        return [invalid, msg]
    },


    populate: function(model){
        this.options = model || {};
    }
})
ETLCst.OPERATOR_MODEL_CLASS[ETLCst.ANALYSIS_ETL_PAGES.FILTER] =  BI.AnalysisETLOperatorFilterPaneModel