BI.CalculateTargetAbstractPeriodValuePane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetAbstractPeriodValuePane.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-calculate-target-abstract-period-value-pane",
            model: null
        })
    },

    _init: function () {
        BI.CalculateTargetAbstractPeriodValuePane.superclass._init.apply(this, arguments);
        var self = this;
        this.valueCombo = BI.createWidget({
            type: "bi.text_value_combo",
            height: 30
        });

        this.valueCombo.on(BI.TextValueCombo.EVENT_CHANGE, function () {
            self._refreshLabel();
            self.fireEvent(BI.CalculateTargetAbstractPeriodValuePane.EVENT_CHANGE)
        });


        this.logicPane = BI.createWidget({
            type: "bi.vertical",
            cls: "cal-pane"
        });


        var valueLabel = BI.createWidget({
            type: "bi.label",
            cls: "cal-label",
            value: BI.i18nText("BI-Value_From"),
            textAlign: "left",
            textHeight: 30
        });

        var logicLabel = BI.createWidget({
            type: "bi.label",
            cls: "cal-label",
            value: BI.i18nText("BI-Value_Logic"),
            textAlign: 'left',
            textHeight: 30
        });

        this.tipLabel = BI.createWidget({
            type: "bi.label",
            cls: "cal-label",
            textHeight: 30,
            textAlign: 'left'
        });

        this.tipLabel.doHighLight();


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
                            el: logicLabel
                        }, {
                            el: {
                                type: "bi.vtape",
                                items: [
                                    {
                                        el: this.tipLabel,
                                        height: 30
                                    },
                                    {
                                        el: this.logicPane
                                    }
                                ]
                            },
                            width: 478
                        }],
                        hgap: 10,
                        height: 240
                    }
                }
            ]
        })
    },

    _refreshLabel: function () {
    },


    setValue: function (expression) {
    },

    getValue: function () {
    },

    populate: function (model) {
        var o = this.options;
        o.model = model;
        this.valueCombo.populate(o.model.getFieldComboItems());
        if (BI.isNotNull(o.model.getFormulaFields()[0])) {
            this.valueCombo.setValue(o.model.getFormulaFields()[0].value);
        }
        this._refreshLabel();
    }
});
BI.CalculateTargetAbstractPeriodValuePane.EVENT_CHANGE = "EVENT_CHANGE";