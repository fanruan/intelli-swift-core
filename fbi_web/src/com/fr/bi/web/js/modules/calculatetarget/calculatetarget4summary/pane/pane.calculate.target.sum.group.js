BI.CalculateTargetSumGroupPane = BI.inherit(BI.CalculateTargetAbstractGroupPane, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetSumGroupPane.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-calculate-target-rank-group-pane"
        })
    },

    _init: function () {
        BI.CalculateTargetSumGroupPane.superclass._init.apply(this, arguments);
        this.principleCombo.populate(BICst.CAL_TARGET_SUM_TYPE);
        this.principleCombo.setValue(BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.SUM);
    },


    _refreshLabel: function () {
        var self = this, o = this.options;
        this.logicPane.empty();
        BI.each(o.model.getDimDimensionIDs(), function (i, dId) {
            var dimensionName = BI.Utils.getDimensionNameByID(dId);
            var label = BI.createWidget({
                type: "bi.label",
                textHeight: 30,
                textAlign: "left"
            });
            label.setValue(BI.i18nText("BI-Calculate_Target_Include_In_Same", dimensionName));
            self.logicPane.addItem(label);
        });
        var lastLabel = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            textHeight: 30
        });
        switch (this.principleCombo.getValue()[0]) {
            case BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.SUM:
                lastLabel.setValue(BI.i18nText("BI-Calculate_Target_Sum_Get_Sum", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])));
                break;
            case BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.AVG:
                lastLabel.setValue(BI.i18nText("BI-Calculate_Target_Sum_Get_Average", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])));
                break;
            case BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.MAX:
                lastLabel.setValue(BI.i18nText("BI-Calculate_Target_Sum_Get_Max", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])));
                break;
            case BICst.TARGET_TYPE.CAL_VALUE.SUMMARY_TYPE.MIN:
                lastLabel.setValue(BI.i18nText("BI-Calculate_Target_Sum_Get_Min", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])));
                break;
        }
        this.logicPane.addItem(lastLabel);
        this.tipLabel.setValue(BI.i18nText("BI-Calculate_Target_Sum_Group_Tip_Label", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])))
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
BI.CalculateTargetSumGroupPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.calculate_target_sum_group_pane", BI.CalculateTargetSumGroupPane);