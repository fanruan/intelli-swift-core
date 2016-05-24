BI.AnalysisETLOperatorFilterPaneModel = BI.inherit(BI.MVCModel, {


    check : function () {
        var self = this;
        var parent = this.get(ETLCst.PARENTS)[0];
        var operator =  this.get('operator');
        var items =operator[ETLCst.ITEMS];
        var newItems = [];
        var invalid = [false];
        BI.each(items, function (i, item) {
            var field = BI.find(parent[ETLCst.FIELDS], function (i, field) {
                return field.field_name === item.field_name;
            })
            if (BI.isNotNull(field)){
                newItems.push(item);
                if (!invalid[0]){
                    if (field.field_type !== item.field_type){
                        invalid = [true, BI.i18nText('BI-Filter') + field.field_name + BI.i18nText('BI-Illegal_Field_Type')];
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
            if (v.filter_type === BICst.FILTER_TYPE.FORMULA ) {
                var fs = BI.Utils.getFieldsFromFormulaValue(v.filter_value);
                var lostField = BI.find(fs, function (i, field) {
                    return BI.isNull(BI.find(fields, function (idx, f) {
                        return f.field_name === field;
                    }))
                })
                if (BI.isNotNull(lostField)){
                    msg = BI.i18nText('BI-Filter')  + BI.i18nText('BI-Formula_Valid') + lostField
                    return true;
                }
            }
        })
        return [invalid, msg]
    }
})