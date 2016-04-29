/**
 * Created by 小灰灰 on 2016/3/31.
 */
BI.AnalysisETLOperatorAddColumnValueConvertController = BI.inherit(BI.MVCController, {
    _checkCanSave : function (widget, model) {
        if (BI.isNull(model.get('field'))){
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Field')));
        } else {
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, true);
        }
    },

    setField : function (field, widget, model) {
        model.set('field', field);
        var f = model.getFieldByValue(field)
        widget.segment.setEnabledValue(ETLCst.ANALYSIS_ADD_COLUMN_CONVERT_ACCEPT_FIELDS[f.fieldType])
        var type = ETLCst.ANALYSIS_ADD_COLUMN_CONVERT_ACCEPT_FIELDS[f.fieldType][0];
        widget.segment.setValue(type)
        this.setType(type, widget, model)
    },

    setType : function (type, widget, model) {
        model.set('field_type', type);
        this._checkCanSave(widget, model);
    },

    populate : function (widget, model) {
        var fields = model.createFieldsItems();
        widget.combo.populate(fields);
        var field = model.get('field') || (fields.length > 0 ? fields[0].value : null);
        widget.combo.setValue(field);
        if(BI.isNotNull(field)) {
            var f = model.getFieldByValue(field);
            model.set('field', field);
            widget.segment.setEnabledValue(ETLCst.ANALYSIS_ADD_COLUMN_CONVERT_ACCEPT_FIELDS[f.fieldType]);
            var type = model.get('field_type') || ETLCst.ANALYSIS_ADD_COLUMN_CONVERT_ACCEPT_FIELDS[f.fieldType][0];
            widget.segment.setValue(type);
            model.set('field_type', type);
        }

    }
})