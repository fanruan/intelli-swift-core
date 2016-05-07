/**
 * Created by roy on 16/4/11.
 */
BI.CalculateTargetYearOnYearRatePane = BI.inherit(BI.CalculateTargetAbstractPeriodRatePane, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetYearOnYearRatePane.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-calculate-target-year-on-year-rate-pane"
        })
    },

    _init: function () {
        BI.CalculateTargetYearOnYearRatePane.superclass._init.apply(this, arguments);
    },

    setValue: function (expression) {
        this.valueCombo.setValue(expression.ids);
        this._refreshLabel();
    },

    getValue: function () {
        var result = {};
        result.ids = this.valueCombo.getValue();
        result.period_type = BICst.TARGET_TYPE.CAL_VALUE.PERIOD_TYPE.RATE;
        return result;
    },

    _refreshLabel: function () {
        var self = this, o = this.options;
        this.logicValuePane.empty();
        this.logicPane.empty();
        var dimDimensionIDs = o.model.getDimDimensionIDs();
        var lastDimensionID = dimDimensionIDs.pop();
        var secondLastDimensionID = dimDimensionIDs.pop();
        var firstLabel = BI.createWidget({
            type: "bi.label",
            textHeight: 30,
            textAlign: "left"
        });
        firstLabel.setValue(BI.i18nText("BI-Calculate_Target_Each_Value_Get", BI.Utils.getDimensionNameByID(lastDimensionID) || "", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])));
        this.logicValuePane.addItem(firstLabel);
        BI.each(dimDimensionIDs, function (i, dId) {
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
        lastLabel.setValue(BI.i18nText("BI-Calculate_Target_Last_Include_In_Same", BI.Utils.getDimensionNameByID(secondLastDimensionID) || "", BI.Utils.getDimensionNameByID(lastDimensionID) || ""));
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
$.shortcut("bi.calculate_target_year_on_year_rate_pane", BI.CalculateTargetYearOnYearRatePane);