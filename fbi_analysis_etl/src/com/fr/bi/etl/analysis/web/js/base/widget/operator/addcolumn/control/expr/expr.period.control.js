/**
 * Created by 小灰灰 on 2016/4/5.
 */
BI.AnalysisETLOperatorAddColumnExprPeriodController = BI.inherit(BI.MVCController, {
    _checkCanSave : function (widget, model) {
        if (BI.isNull(model.get('field'))){
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Value_From')));
        } else {
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true);
        }
    },

    _createLeftItems : function(model){
        var group = model.get('group') || [];
        return BI.filter(model.getNumberFields(), function (i, item) {
            return BI.indexOf(group, item.value) === -1;
        })
    },

    _afterValueSetted : function (widget, model) {
        this._refreshGroup(widget, model);
        this._populateLabel(widget, model);
        this._checkCanSave(widget, model);
    },

    _refreshCombo : function (widget, model) {
        widget.combo.populate(this._createLeftItems(model));
        widget.combo.setValue(model.get('field'));
    },
    _getSelectedFields : function (model) {
        var fields = [];
        if (BI.isNotNull(model.get('field'))){
            fields.push(model.get('field'));
        }
        return fields;
    },
    _refreshGroup : function (widget, model) {
        var items = [];
        var group = model.getValue('group')||[];
        var selectedFields = this._getSelectedFields(model);
        BI.each(selectedFields, function (i ,item) {
            group.remove(item);
        })
        BI.each(group, function (i, item) {
            items.push({
                text : item,
                value : item,
                title : item,
                selected : true
            });
        })
        BI.each(model.get(ETLCst.FIELDS), function (i, item) {
            if (BI.indexOf(group, item.value) === -1 && BI.indexOf(selectedFields, item.value) === -1){
                items.push({text : item.text, value : item.value, title : item.text});
            }
        })
        widget.refreshGroup(items);
    },

    _refreshComboValue : function (combo, oldValue, widget, model) {
        if (combo !== widget.combo && combo.getValue()[0] === model.get('field')){
            widget.combo.setValue(oldValue);
            model.set('field', oldValue);
        } 
    },

    setGroup : function (group, widget, model) {
        model.set('group' , group);
        this._populateLabel(widget, model);
        this._checkCanSave(widget, model);
        this._refreshCombo(widget, model);
    },

    setValueField : function (value, widget, model) {
        var oldValue = model.get('field');
        model.set('field', value);
        this._refreshComboValue(widget.combo, oldValue, widget, model)
        this._afterValueSetted(widget, model);
    },

    populate : function (widget, model) {
        var fields = model.getNumberFields();
        if (BI.isNull(model.get('field')) && BI.isNotEmptyArray(fields)){
            model.set('field', fields[0].value)
        }
        widget.combo.populate(this._createLeftItems(model));
        widget.combo.setValue(model.get('field'));
    }
})