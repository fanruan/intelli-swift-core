/**
 * Created by roy on 16/4/9.
 */
BI.CalculateTargetSumAbovePane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetSumAbovePane.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-calculate-target-sum-above-pane",
            model: null
        })
    },

    _init: function () {
        BI.CalculateTargetSumAbovePane.superclass._init.apply(this, arguments);
        var self = this;
        this.valueCombo = BI.createWidget({
            type: "bi.text_value_combo",
            height: 30
        });

        this.valueCombo.on(BI.TextValueCombo.EVENT_CHANGE, function () {
            self._refreshLabel();
            self.fireEvent(BI.CalculateTargetSumAbovePane.EVENT_CHANGE);

        });

        this.logicLabel = BI.createWidget({
            type: "bi.label",
            cls: "cal-pane",
            textAlign: "left",
            textHeight: 30
        });

        var valueLabel = BI.createWidget({
            type: "bi.label",
            cls: "cal-label",
            value: BI.i18nText("BI-Value_From"),
            textAlign: "left",
            textHeight: 30
        });

        var logicTitleLabel = BI.createWidget({
            type: "bi.label",
            cls: "cal-label",
            value: BI.i18nText("BI-Value_Logic"),
            textAlign: 'left'
        });


        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [
                {
                    el: {
                        type: "bi.htape",
                        items: [{
                            el: valueLabel
                        }, {
                            el: this.valueCombo,
                            width: 480
                        }],
                        height: 30,
                        hgap: 10
                    },
                    bgap: 10
                },
                {
                    el: {
                        type: "bi.htape",
                        items: [{
                            el: logicTitleLabel
                        }, {
                            el: this.logicLabel,
                            width: 478
                        }],
                        hgap: 10,
                        height: 200
                    }
                }
            ]
        })
    },


    _refreshLabel: function () {
        this.logicLabel.setValue(BI.i18nText("BI-Calculate_Target_Sum_Above_Logic", BI.Utils.getDimensionNameByID(this.valueCombo.getValue()[0])))
    },

    setValue: function (expression) {
        this.valueCombo.setValue(expression.ids);
        this._refreshLabel();
    },

    getValue: function () {
        var result = {};
        result.ids = this.valueCombo.getValue();
        return result;
    },

    populate: function (model) {
        var o = this.options;
        o.model = model;
        this.valueCombo.populate(o.model.getFieldComboItems());
        if (BI.isNotNull(o.model.getFieldComboItems()[0])) {
            this.valueCombo.setValue(o.model.getFieldComboItems()[0].value);
        }
        this._refreshLabel();
    }
});
BI.CalculateTargetSumAbovePane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.calculate_target_sum_above_pane", BI.CalculateTargetSumAbovePane);