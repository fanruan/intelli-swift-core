/**
 * Created by roy on 16/3/1.
 */
BI.CalculateTargetPopupSummary = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetPopupSummary.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "calculate-target-popup",
            wId: "",
            targetId: ""
        })
    },

    _init: function () {
        var o = this.options;
        BI.CalculateTargetPopupSummary.superclass._init.apply(this, arguments);
        this.model = new BI.CalculateTargetPopupSummaryModel({
            wId: o.wId,
            targetId: o.targetId
        });

    },

    _createTabs: function (v) {
        var o = this.options, self = this;
        switch (v) {
            case BICst.TARGET_TYPE.FORMULA:
                var formulaPane = BI.createWidget({
                    type: "bi.calculate_target_formula_pane",
                });
                formulaPane.on(BI.CalculateTargetFormulaPane.EVENT_CHANGE, function () {
                    self.model.setTargetExpression(formulaPane.getValue());
                });
                formulaPane.populate(this.model);
                this.model.setTargetType(BICst.TARGET_TYPE.FORMULA);
                return formulaPane;
            case BICst.TARGET_TYPE.RANK:
                var rankPane = BI.createWidget({
                    type: "bi.calculate_target_rank_pane"
                });
                rankPane.on(BI.CalculateTargetAbstractGroupPane.EVENT_CHANGE, function () {
                    self.model.setTargetExpression(rankPane.getValue());
                });
                rankPane.populate(this.model);
                return rankPane;
            case BICst.TARGET_TYPE.RANK_IN_GROUP:
                var rankGroupPane = BI.createWidget({
                    type: "bi.calculate_target_rank_group_pane"
                });
                rankGroupPane.on(BI.CalculateTargetAbstractGroupPane.EVENT_CHANGE, function () {
                    self.model.setTargetExpression(rankGroupPane.getValue());
                });
                rankGroupPane.populate(this.model);
                return rankGroupPane;
            case  BICst.TARGET_TYPE.SUM_OF_ALL:
                var sumPane = BI.createWidget({
                    type: "bi.calculate_target_sum_pane"
                });
                sumPane.on(BI.CalculateTargetAbstractGroupPane.EVENT_CHANGE, function () {
                    self.model.setTargetExpression(sumPane.getValue())
                });
                sumPane.populate(this.model);
                return sumPane;
            case  BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP:
                var sumGroupPane = BI.createWidget({
                    type: "bi.calculate_target_sum_group_pane"
                });
                sumGroupPane.on(BI.CalculateTargetAbstractGroupPane.EVENT_CHANGE, function () {
                    self.model.setTargetExpression(sumGroupPane.getValue());
                });
                sumGroupPane.populate(this.model);
                return sumGroupPane;
            case BICst.TARGET_TYPE.MONTH_ON_MONTH_VALUE:
                var monthValuePane = BI.createWidget({
                    type: "bi.calculate_target_month_on_month_value_pane"
                });
                monthValuePane.on(BI.CalculateTargetAbstractPeriodValuePane.EVENT_CHANGE, function () {
                    self.model.setTargetExpression(monthValuePane.getValue());
                });
                monthValuePane.populate(this.model);
                return monthValuePane;
            case BICst.TARGET_TYPE.MONTH_ON_MONTH_RATE:
                var monthRatePane = BI.createWidget({
                    type: "bi.calculate_target_month_on_month_rate_pane"
                });
                monthRatePane.on(BI.CalculateTargetAbstractPeriodRatePane.EVENT_CHANGE, function () {
                    self.model.setTargetExpression(monthRatePane.getValue());
                });
                monthRatePane.populate(this.model);
                return monthRatePane;
            case BICst.TARGET_TYPE.YEAR_ON_YEAR_VALUE:
                var yearValuePane = BI.createWidget({
                    type: "bi.calculate_target_year_on_year_value_pane"
                });
                yearValuePane.on(BI.CalculateTargetAbstractPeriodValuePane.EVENT_CHANGE, function () {
                    self.model.setTargetExpression(yearValuePane.getValue());
                });
                yearValuePane.populate(this.model);
                return yearValuePane;
            case  BICst.TARGET_TYPE.YEAR_ON_YEAR_RATE:
                var yearRatePane = BI.createWidget({
                    type: "bi.calculate_target_year_on_year_rate_pane"
                });
                yearRatePane.on(BI.CalculateTargetAbstractPeriodRatePane.EVENT_CHANGE, function () {
                    self.model.setTargetExpression(yearRatePane.getValue());
                });
                yearRatePane.populate(this.model);
                return yearRatePane;
            case BICst.TARGET_TYPE.SUM_OF_ABOVE:
                var sumAbovePane = BI.createWidget({
                    type: "bi.calculate_target_sum_above_pane"
                });
                sumAbovePane.on(BI.CalculateTargetSumAbovePane.EVENT_CHANGE, function () {
                    self.model.setTargetExpression(sumAbovePane.getValue());
                });
                sumAbovePane.populate(this.model);
                return sumAbovePane;
            case BICst.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP:
                var sumAboveGroupPane = BI.createWidget({
                    type: "bi.calculate_target_sum_above_group_pane"
                });
                sumAboveGroupPane.on(BI.CalculateTargetAbstractPeriodValuePane.EVENT_CHANGE, function () {
                    self.model.setTargetExpression(sumAboveGroupPane.getValue());
                });
                sumAboveGroupPane.populate(this.model);
                return sumAboveGroupPane;


        }
    },

    rebuildNorth: function (north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            text: BI.i18nText("BI-Calculation_Index"),
            textAlign: "left",
            height: 50
        });
        return true
    },

    rebuildCenter: function (center) {
        var self = this;
        var targetLabel = BI.createWidget({
            type: "bi.label",
            cls: "cal-label",
            value: BI.i18nText("BI-Target_Name"),
            textAlign: "left",
            textHeight: 30
        });

        var calculateLabel = BI.createWidget({
            type: "bi.label",
            cls: "cal-label",
            value: BI.i18nText("BI-Cal_Type"),
            textAlign: "left",
            textHeight: 30
        });

        this.targetEditor = BI.createWidget({
            type: "bi.text_editor",
            validationChecker: function (v) {
                return !self.model.isDuplicated(v)
            },
            allowBlank: true,
            errorText: BI.i18nText("BI-Cannot_Have_Repeated_Field_Name")
        });


        this.targetEditor.on(BI.TextEditor.EVENT_CHANGE, function () {
            self.model.setTargetName(self.targetEditor.getValue())
        });

        this.calTypeCombo = BI.createWidget({
            type: "bi.text_icon_combo",
            height: 30,
            items: BICst.CAL_TARGET_TYPE
        });


        this.tab = BI.createWidget({
            direction: "custom",
            type: "bi.tab",
            tab: this.calTypeCombo,
            cardCreator: BI.bind(this._createTabs, this),
            height: 280
        });

        this.tab.on(BI.Tab.EVENT_CHANGE, function () {
            self.model.setTargetType(self.calTypeCombo.getValue()[0]);
            self.model.setTargetExpression(self.tab.getValue());
        });


        BI.createWidget({
            type: "bi.vertical",
            element: center,
            items: [{
                el: {
                    type: "bi.htape",
                    items: [{
                        el: targetLabel
                    }, {
                        el: this.targetEditor,
                        width: 478
                    }],
                    hgap: 10,
                    height: 30
                }
            }, {
                el: {
                    type: "bi.htape",
                    hgap: 10,
                    items: [{
                        el: calculateLabel
                    }, {
                        el: this.calTypeCombo,
                        width: 480
                    }],
                    height: 30
                },
                tgap: 10,
                bgap: 10
            }, this.tab]
        });
        this.populate();
    },

    rebuildSouth: function (south) {
        BI.CalculateTargetPopupSummary.superclass.rebuildSouth.apply(this, arguments);
        this.sure.setText(BI.i18nText("BI-Save_Config"));
    },


    getValue: function () {
        return this.model.getValue();
    },

    end: function () {
        this.fireEvent(BI.CalculateTargetPopupSummary.EVENT_CHANGE);
    },

    populate: function () {
        this.targetEditor.setValue(this.model.getTargetName());
        this.calTypeCombo.setValue(this.model.getTargetType());
        this.tab.setSelect(this.model.getTargetType());
        this.tab.populate(this.model);
        this.tab.setValue(this.model.getTargetExpression());
    }


});
BI.CalculateTargetPopupSummary.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.calculate_target_popup_summary", BI.CalculateTargetPopupSummary);