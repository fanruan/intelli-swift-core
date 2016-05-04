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

    setValueField : function (value, widget, model) {
        model.set('field', value);
    },

    populate : function (widget, model) {
        var fields = model.getNumberFields();
        widget.combo.populate(fields);
        if (BI.isNull(model.get('field')) && BI.isNotEmptyArray(fields)){
            model.set('field', fields[0].value)
        }
        widget.combo.setValue(model.get('field'));
    }
})