/**
 * Created by 小灰灰 on 2016/5/4.
 */
BI.AnalysisETLOperatorAddColumnSumPane  = BI.inherit(BI.AnalysisETLOperatorAddColumnAccPane, {

    _createComboPane: function () {
        var self = this;
        var comboItems = BI.AnalysisETLOperatorAddColumnSumPane.superclass._createComboPane.apply(this, arguments);
        comboItems = BI.concat(comboItems, [
            {
                el: {
                    type: 'bi.label',
                    cls: 'label-name',
                    text: BI.i18nText('BI-Statistic_Type'),
                    textAlign: 'left',
                    ref: function (_ref) {
                        self.sumLabel = _ref;
                    }
                },
                left: 0,
                top: 85
            }, {
                el: {
                    type: "bi.text_value_combo",
                    height: self._constants.HEIGHT,
                    width: self._constants.WIDTH,
                    items: BICst.SUMMARY_TYPE_ITEMS,
                    ref: function (_ref) {
                        self.sumType = _ref;
                    },
                    listeners: [{
                        eventName: BI.TextValueCombo.EVENT_CHANGE,
                        action: function (v) {
                            self.setSumType(v);
                        }
                    }]
                },
                left: 70,
                top: 80
            }])
        return comboItems;
    },

    //原controller
    _checkCanSave : function () {
        if (BI.isNull(this.model.get('sum_type'))){
            this.fireEvent(BI.AnalysisTopPointerSavePane.EVENT_CHECK_SAVE_STATUS, false, BI.i18nText('BI-Property_Not_Setted', BI.i18nText('BI-Statistic_Type')));
        } else {
            BI.AnalysisETLOperatorAddColumnSumPane.superclass._checkCanSave.apply(this, arguments);
        }
    },

    _isGroupWillShow: function () {
        return this.model.get('rule') ===  BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP;
    },

    _getLabelLastText : function () {
        switch (this.model.get('sum_type')) {
            case BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.SUM:
                return BI.i18nText('BI-Calculate_Target_Sum_Get_Sum', this.model.get('field'));
            case BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.AVG:
                return BI.i18nText('BI-Calculate_Target_Sum_Get_Average', this.model.get('field'));
            case BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.MAX:
                return BI.i18nText('BI-Calculate_Target_Sum_Get_Max', this.model.get('field'));
            case BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.MIN:
                return BI.i18nText('BI-Calculate_Target_Sum_Get_Min', this.model.get('field'));
        }

    },

    setSumType : function (value) {
        this.model.set('sum_type', value);
        this._refreshLabel();
        this._checkCanSave();
    },

    _populate: function(){
        var fields = this.model.getNumberFields();
        this.fieldCombo.populate(fields);
        if (BI.isNull(this.model.get('field')) && BI.isNotEmptyArray(fields)){
            this.model.set('field', fields[0].value)
        }
        this.fieldCombo.setValue(this.model.get('field'));
        this.rule.populate([{
            text: BI.i18nText("BI-All_Values"),
            value: BICst.TARGET_TYPE.SUM_OF_ALL
        }, {
            text: BI.i18nText("BI-All_Values_In_Group"),
            value: BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP
        }]);
        this.model.set('rule', this.model.get('rule') || BICst.TARGET_TYPE.SUM_OF_ALL);
        this.rule.setValue(this.model.get('rule'));
        this.sumType.populate(BICst.CAL_TARGET_SUM_TYPE);
        this.model.set('sum_type', this.model.get('sum_type') || BICst.CAL_TARGET_SUM_TYPE[0].value);
        this.sumType.setValue(this.model.get('sum_type'));
        this._afterValueSetted();
    },

    populate : function (m, options) {
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    }

});
BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.ADD_COLUMN + '_' + BICst.ETL_ADD_COLUMN_TYPE.EXPR_SUM, BI.AnalysisETLOperatorAddColumnSumPane);