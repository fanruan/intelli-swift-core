/**
 * Created by 小灰灰 on 2016/5/4.
 */
BI.AnalysisETLOperatorAddColumnExprSumController = BI.inherit(BI.AnalysisETLOperatorAddColumnExprAccController, {
    _checkCanSave : function (widget, model) {
        if (BI.isNull(model.get('sum_type'))){
            widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Statistic_Type')));
        } else {
             BI.AnalysisETLOperatorAddColumnExprSumController.superclass._checkCanSave.apply(this, arguments);
        }
    },

    _isGroupWillShow: function (model) {
        return model.get('rule') ===  BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP;
    },

    _getLabelLastText : function (model) {
        switch (model.get('sum_type')) {
            case BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.SUM:
                return BI.i18nText('BI-Calculate_Target_Sum_Get_Sum', model.get('field'));
            case BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.AVG:
                return BI.i18nText('BI-Calculate_Target_Sum_Get_Average', model.get('field'));
            case BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.MAX:
                return BI.i18nText('BI-Calculate_Target_Sum_Get_Max', model.get('field'));
            case BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.MIN:
                return BI.i18nText('BI-Calculate_Target_Sum_Get_Min', model.get('field'));
        }

    },

    setSumType : function (value, widget, model) {
        model.set('sum_type', value);
        this._refreshLabel(widget, model);
        this._checkCanSave(widget, model);
    },

    populate : function (widget, model) {
        var fields = model.getNumberFields();
        widget.fieldCombo.populate(fields);
        if (BI.isNull(model.get('field')) && BI.isNotEmptyArray(fields)){
            model.set('field', fields[0].value)
        }
        widget.fieldCombo.setValue(model.get('field'));
        widget.rule.populate([{
            text: BI.i18nText("BI-All_Values"),
            value: BICst.TARGET_TYPE.SUM_OF_ALL
        }, {
            text: BI.i18nText("BI-All_Values_In_Group"),
            value: BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP
        }]);
        model.set('rule', model.get('rule') || BICst.TARGET_TYPE.SUM_OF_ALL);
        widget.rule.setValue(model.get('rule'));
        widget.sumType.populate(BICst.CAL_TARGET_SUM_TYPE);
        model.set('sum_type',model.get('sum_type') || BICst.CAL_TARGET_SUM_TYPE[0].value)
        widget.sumType.setValue(model.get('sum_type'));
        this._afterValueSetted(widget, model);
    }
})