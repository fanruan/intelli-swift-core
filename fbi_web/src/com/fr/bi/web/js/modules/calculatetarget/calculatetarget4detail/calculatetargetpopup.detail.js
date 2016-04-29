/**
 * Created by roy on 16/3/30.
 */
BI.CalculateTargetPopupDetail = BI.inherit(BI.BarPopoverSection, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetPopupDetail.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "calculate-target-popup",
            wId: "",
            targetId: ""
        })
    },

    _init: function () {
        var o = this.options;
        BI.CalculateTargetPopupDetail.superclass._init.apply(this, arguments);
        this.model = new BI.CalculateTargetPopupDetailModel({
            wId: o.wId,
            targetId: o.targetId
        })


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


        this.targetEditor = BI.createWidget({
            type: "bi.text_editor",
            allowBlank: true,
            validationChecker: function (v) {
                return !self.model.isDuplicated(v)
            },
            errorText: BI.i18nText("BI-Cannot_Have_Repeated_Field_Name")
        });

        this.targetEditor.on(BI.TextEditor.EVENT_CHANGE, function () {
            self.model.setTargetName(self.targetEditor.getValue());
        });

        this.formulaEditor = BI.createWidget({
            type: "bi.formula_insert"
        });

        this.formulaEditor.on(BI.FormulaInsert.EVENT_CHANGE, function () {
            self.model.setFormulaExpression({
                formula_value: self.formulaEditor.getValue(),
                cal_target_name: self.formulaEditor.getUsedFields()
            });
        });

        BI.createWidget({            type: "bi.vtape",
            vgap: 10,
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
                    hgap: 10
                },
                height: 30
            }, {
                el: this.formulaEditor
            }]
        });
        this.populate();

    },

    rebuildSouth: function (south) {
        BI.CalculateTargetPopupDetail.superclass.rebuildSouth.apply(this, arguments);
        this.sure.setText(BI.i18nText("BI-Save_Config"));
    },


    getValue: function () {
        return this.model.getValue();
    },

    end: function () {
        this.fireEvent(BI.CalculateTargetPopupDetail.EVENT_CHANGE);
    },

    populate: function () {
        this.targetEditor.setValue(this.model.getTargetName());
        this.formulaEditor.populate(this.model.getFormulaFields());
        this.formulaEditor.setValue(this.model.getFormulaExpression());
    }

});
BI.CalculateTargetPopupDetail.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.calculate_target_popup_detail", BI.CalculateTargetPopupDetail);