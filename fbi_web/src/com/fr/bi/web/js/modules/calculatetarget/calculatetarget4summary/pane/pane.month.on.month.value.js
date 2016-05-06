/**
 * Created by roy on 16/4/10.
 */
BI.CalculateTargetMonthOnMonthValuePane = BI.inherit(BI.CalculateTargetAbstractPeriodValuePane, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetMonthOnMonthValuePane.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-calculate-target-month-on-month-value-pane"
        })
    },

    _init: function () {
        BI.CalculateTargetMonthOnMonthValuePane.superclass._init.apply(this, arguments);
    },

    setValue: function (expression) {
        this.valueCombo.setValue(expression.ids);
        this._refreshLabel();
    },

    getValue: function () {
        var result = {};
        result.ids = this.valueCombo.getValue();
        result.period_type = BICst.TARGET_TYPE.CAL_VALUE.PERIOD_TYPE.VALUE;
        return result;
    },

    _refreshLabel: function () {
        var self = this, o = this.options;
        this.logicPane.empty();
        var firstLabel = BI.createWidget({
            type: "bi.label",
            textHeight: 30,
            textAlign: "left"
        });
        firstLabel.setValue(BI.i18nText("BI-Calculate_Target_Each_Value", BI.i18nText("BI-Month_Fen"), BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])));
        this.logicPane.addItem(firstLabel);
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
        lastLabel.setValue(BI.i18nText("BI-Calculate_Target_Include_In_Same_Last", BI.i18nText("BI-Year_Fen"), BI.i18nText("BI-Month_Fen")))
        this.logicPane.addItem(lastLabel);
        var valueLabel = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            textHeight: 30
        });
        valueLabel.setValue(BI.i18nText("BI-Brackets_Value", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])));
        this.logicPane.addItem(valueLabel);
        this.tipLabel.setValue(BI.i18nText("BI-Calculate_Target_Sum_Group_Tip_Label", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])))
    }

});
$.shortcut("bi.calculate_target_month_on_month_value_pane", BI.CalculateTargetMonthOnMonthValuePane);