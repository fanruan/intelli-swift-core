/**
 * Created by roy on 16/4/11.
 */
BI.CalculateTargetSumAboveGroupPane = BI.inherit(BI.CalculateTargetAbstractPeriodValuePane, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetSumAboveGroupPane.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-calculate-target-sum-above-group-pane"
        })
    },

    _init: function () {
        BI.CalculateTargetSumAboveGroupPane.superclass._init.apply(this, arguments);
    },


    _refreshLabel: function () {
        this.logicPane.empty();
        var o = this.options, self = this;
        var dimDimensionIDs = o.model.getDimDimensionIDs();
        var lastDimensionID = dimDimensionIDs.pop();
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
        var lastDimLabel = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            textHeight: 30
        });
        lastDimLabel.setValue(BI.i18nText("BI-Calculate_Target_Sum", BI.Utils.getDimensionNameByID(lastDimensionID) || ""));
        this.logicPane.addItem(lastDimLabel);
        lastLabel.setValue(BI.i18nText("BI-Calculate_Target_Sum_Above_Group_Get_Sum", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])))
        this.logicPane.addItem(lastLabel);
    },

    setValue: function (expression) {
        this.valueCombo.setValue(expression.ids);
        this._refreshLabel();
    },

    getValue: function () {
        var result = {};
        result.ids = this.valueCombo.getValue();
        return result;
    }
});
$.shortcut("bi.calculate_target_sum_above_group_pane", BI.CalculateTargetSumAboveGroupPane);