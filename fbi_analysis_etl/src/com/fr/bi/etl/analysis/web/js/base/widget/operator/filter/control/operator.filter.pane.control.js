BI.AnalysisETLOperatorFilterPaneController = BI.inherit(BI.MVCController, {

    filterChange : function(filter, widget, model){
        var operator = model.get('operator');
        operator.items = operator.items || [];
        var items = operator.items;
        if (BI.isNull(filter.value) || BI.isEmptyArray(filter.value)){
            BI.remove(items, function (idx, item) {
                return item.field_name === filter.field_name;
            });
        } else {
            var item = BI.find(items, function (idx, item) {
                return item.field_name === filter.field_name;
            })
            if (BI.isNull(item)){
                items.push(filter)
            } else {
                BI.extend(item, filter);
            }
        }
        this.populate(widget, model);
    },

    getFilterValue : function (field, widget, model) {
        var operator = model.get('operator');
        return BI.find(operator.items, function (idx, item) {
            return item.field_name === field;
        })
    },

    operatorChange : function (v, widget, model) {
        var operator = model.get('operator');
        operator.type = v;
    },
    
    populate : function (widget, model) {

        var operator = model.get('operator');
        if (BI.isNull(operator)){
            model.set('operator', {type : BICst.FILTER_TYPE.AND});
            operator = model.get('operator');
        }
        this._check(widget, model);
        var items = operator.items;
        if(BI.isNull(items) || items.length === 0) {
            widget.card.showCardByName(widget._constant.nullCard)
        } else {
            widget.card.showCardByName(widget._constant.filterCard)
            var type = operator.type || BICst.FILTER_TYPE.AND
            widget.operatorCombo.setValue(type)
            var fieldItems = [];
            BI.each(model.get(ETLCst.PARENTS)[0][ETLCst.FIELDS], function (i, item) {
                fieldItems.push({
                    text : item.field_name,
                    value : item.field_name,
                    fieldType : item.field_type
                })
            })
            widget.content.populate(items, fieldItems);
        }
        this.doCheck(widget, model)
        widget.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, widget.controller, widget.options.value.operatorType)
    },

    doCheck : function (widget, model) {
        var operator = model.get('operator');
        var items = operator.items;
        widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, BI.isNotNull(items) && items.length !== 0)
    },

    _check : function (widget, model) {
        var self = this;
        var parent = model.get(ETLCst.PARENTS)[0];
        var operator =  model.get('operator');
        var items =operator[ETLCst.ITEMS];
        var newItems = [];
        var invalid = false;
        BI.each(items, function (i, item) {
            var field = BI.find(parent[ETLCst.FIELDS], function (i, field) {
                return field.field_name === item.field_name;
            })
            if (BI.isNotNull(field)){
                newItems.push(item);
                if (!invalid){
                    if (field.field_type !== item.field_type){
                        invalid = true;
                        widget.fireEvent(BI.TopPointerSavePane.EVENT_INVALID, BI.i18nText('BI-Filter') + field.field_name + BI.i18nText('BI-Illegal_Field_Type'))
                    } else {
                        invalid = self._checkItem(widget, item, parent[ETLCst.FIELDS]);
                    }
                }
            }
        });
        operator.items = newItems;
        if (!invalid){
            widget.fireEvent(BI.TopPointerSavePane.EVENT_FIELD_VALID, BI.deepClone(parent[ETLCst.FIELDS]))
        }
        widget.fireEvent(BI.AnalysisETLOperatorAbstractController.VALID_CHANGE, !invalid);
    },

    _checkItem : function (widget, item, fields) {
        return BI.some(item.value, function (i, v) {
            if (v.filter_type === BICst.FILTER_TYPE.FORMULA ) {
                var fs = BI.Utils.getFieldsFromFormulaValue(v.filter_value);
                var lostField = BI.find(fs, function (i, field) {
                    return BI.isNull(BI.find(fields, function (idx, f) {
                        return f.field_name === field;
                    }))
                })
                if (BI.isNotNull(lostField)){
                    widget.fireEvent(BI.TopPointerSavePane.EVENT_INVALID, BI.i18nText('BI-Filter')  + BI.i18nText('BI-Formula_Valid')) + lostField
                    return true;
                }
            }
        })
    },

    isDefaultValue : function (widget, model) {
        var operator = model.get('operator');
        var items = operator.items;
        return BI.isNull(items) || items.length === 0
    },

    
    update : function (widget, model) {
        var v =  model.update();
        v.etlType = ETLCst.ETL_TYPE.FILTER;
        var parent = model.get(ETLCst.PARENTS)[0];
        v[ETLCst.FIELDS] = BI.deepClone(parent[ETLCst.FIELDS])
        return v;
    }

})