BI.CalculateTargetSumPane = BI.inherit(BI.CalculateTargetAbstractPane, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetSumPane.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-calculate-target-sum-pane"
        })
    },

    _init: function () {
        BI.CalculateTargetSumPane.superclass._init.apply(this, arguments);
        this.principleCombo.populate(BICst.CAL_TARGET_SUM_TYPE);
        this.principleCombo.setValue(BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.SUM);
    },


    _refreshLabel: function () {
        switch (this.principleCombo.getValue()[0]) {
            case BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.SUM:
                this.logicLabel.setValue(BI.i18nText("BI-Calculate_Target_Sum_Get_Sum", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])));
                break;
            case BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.AVG:
                this.logicLabel.setValue(BI.i18nText("BI-Calculate_Target_Sum_Get_Average", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])));
                break;
            case BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.MAX:
                this.logicLabel.setValue(BI.i18nText("BI-Calculate_Target_Sum_Get_Max", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])));
                break;
            case BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.MIN:
                this.logicLabel.setValue(BI.i18nText("BI-Calculate_Target_Sum_Get_Min", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])));
                break;
        }
    },

    setValue: function (expression) {
        this.principleCombo.setValue(expression.summary_type);
        this.valueCombo.setValue(expression.cal_target_name);
        this._refreshLabel();
    },

    getValue: function () {
        var result = {};
        result.summary_type = this.principleCombo.getValue()[0];
        result.cal_target_name = this.valueCombo.getValue()[0];
        return result;
    }
});
$.shortcut("bi.calculate_target_sum_pane", BI.CalculateTargetSumPane);