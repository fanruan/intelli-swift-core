/**
 * Created by roy on 16/4/9.
 */
BI.CalculateTargetAbstractPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetAbstractPane.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-calculate-target-abstract-pane",
            model: null
        })
    },

    _init: function () {
        BI.CalculateTargetAbstractPane.superclass._init.apply(this, arguments);
        var self = this;
        this.valueCombo = BI.createWidget({
            type: "bi.text_icon_combo",
            height: 30
        });

        this.valueCombo.on(BI.TextIconCombo.EVENT_CHANGE, function () {
            self._refreshLabel();
            self.fireEvent(BI.CalculateTargetAbstractPane.EVENT_CHANGE);

        });

        this.principleCombo = BI.createWidget({
            type: "bi.text_icon_combo",
            height: 30
        });

        this.principleCombo.on(BI.TextIconCombo.EVENT_CHANGE, function () {
            self._refreshLabel();
            self.fireEvent(BI.CalculateTargetAbstractPane.EVENT_CHANGE);
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

        var principleLabel = BI.createWidget({
            type: "bi.label",
            cls: "cal-label",
            value: BI.i18nText("BI-Value_Principle"),
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
                }, {
                    el: {
                        type: "bi.htape",
                        items: [{
                            el: principleLabel
                        }, {
                            el: this.principleCombo,
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
    },

    setValue: function (expression) {
    },

    getValue: function () {
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
BI.CalculateTargetAbstractPane.EVENT_CHANGE = "EVENT_CHANGE";