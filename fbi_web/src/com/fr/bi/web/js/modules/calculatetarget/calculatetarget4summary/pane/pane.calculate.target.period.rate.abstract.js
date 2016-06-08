BI.CalculateTargetAbstractPeriodRatePane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetAbstractPeriodRatePane.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-calculate-target-abstract-period-rate-pane",
            model: null
        })
    },

    _init: function () {
        BI.CalculateTargetAbstractPeriodRatePane.superclass._init.apply(this, arguments);
        var self = this;
        this.valueCombo = BI.createWidget({
            type: "bi.text_value_combo",
            height: 30
        });

        this.valueCombo.on(BI.TextValueCombo.EVENT_CHANGE, function () {
            self._refreshLabel();
            self.fireEvent(BI.CalculateTargetAbstractPeriodRatePane.EVENT_CHANGE)
        });


        this.logicValuePane = BI.createWidget({
            type: "bi.vertical",
        });

        var divisionLabel = BI.createWidget({
            type: "bi.label",
            value: BI.i18nText("BI-Divide_By"),
            textHeight: 30,
            textAlign: "center"
        });

        this.logicPane = BI.createWidget({
            type: "bi.vertical"
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
            type: "bi.vtape",
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
                        hgap: 10
                    },
                    height: 30,
                    bgap: 10
                }, {
                    el: {
                        type: "bi.htape",
                        items: [{
                            el: logicLabel
                        }, {
                            el: this.tipLabel,
                            width: 480
                        }],
                        hgap: 10
                    },
                    height: 30,
                    bgap: 10
                }, {
                    el: {
                        type: "bi.htape",
                        items: [
                            {
                                el: {
                                    type: 'bi.layout'
                                }
                            }, {
                                el: {
                                    type: "bi.htape",
                                    items: [{
                                        el: {
                                            type: "bi.vtape",
                                            cls: "cal-pane",
                                            items: [{
                                                el: this.logicValuePane,
                                                height: 30
                                            },
                                                {
                                                    el: divisionLabel,
                                                    height: 30
                                                },
                                                {
                                                    el: this.logicPane
                                                }]
                                        }
                                    }, {
                                        el: {
                                            type: "bi.vertical_adapt",
                                            items: [{
                                                type: "bi.label",
                                                value: BI.i18nText("BI-Minus"),
                                                textAlign: "center",
                                                width: 30
                                            }]
                                        },
                                        width: 30
                                    }, {
                                        el: {
                                            type: "bi.vertical_adapt",
                                            cls: "cal-pane",
                                            items: [{
                                                type: "bi.label",
                                                value: 1,
                                                textAlign: "center",
                                                width: 30
                                            }]
                                        },
                                        width: 30
                                    }]
                                },
                                width: 480
                            }],
                        hgap: 10

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
BI.CalculateTargetAbstractPeriodRatePane.EVENT_CHANGE = "EVENT_CHANGE";