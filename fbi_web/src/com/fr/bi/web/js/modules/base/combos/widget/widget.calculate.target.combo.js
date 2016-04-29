/**
 * Created by roy on 16/4/6.
 */
BI.CalculateTargetCombo = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-calculate-target-combo"
        })
    },

    _init: function () {
        BI.CalculateTargetCombo.superclass._init.apply(this, arguments);
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.down_list_combo",
            element: this.element,
            height: 25,
            iconCls: "detail-dimension-set-font",
            items: [
                [{
                    text: BI.i18nText("BI-Style_Setting"),
                    value: BICst.CALCULATE_TARGET_COMBO.FORM_SETTING
                }],
                [{
                    text: BI.i18nText("BI-Modify_Cal_Target"),
                    value: BICst.CALCULATE_TARGET_COMBO.UPDATE_TARGET
                }], [{
                    text: BI.i18nText("BI-Display"),
                    value: BICst.CALCULATE_TARGET_COMBO.DISPLAY
                }, {
                    text: BI.i18nText("BI-Hidden"),
                    value: BICst.CALCULATE_TARGET_COMBO.HIDDEN
                }],
                [{
                    text: BI.i18nText("BI-Rename"),
                    value: BICst.CALCULATE_TARGET_COMBO.RENAME
                }],
                [{
                    text: BI.i18nText("BI-Copy"),
                    value: BICst.CALCULATE_TARGET_COMBO.COPY
                }],
                [{
                    text: BI.i18nText("BI-Remove"),
                    value: BICst.CALCULATE_TARGET_COMBO.DELETE
                }]
            ]
        });
        this.combo.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
            self.fireEvent(BI.CalculateTargetCombo.EVENT_CHANGE, v);
        })
    },

    getValue: function () {
        return this.combo.getValue();
    }


});
BI.CalculateTargetCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.calculate_target_combo", BI.CalculateTargetCombo);