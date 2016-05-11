/**
 * Created by roy on 16/4/6.
 */
BI.CalculateTargetCombo = BI.inherit(BI.AbstractDimensionTargetCombo, {

    defaultItems: function () {
        return [
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
        ];
    },

    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-calculate-target-combo"
        })
    },

    _init: function () {
        BI.CalculateTargetCombo.superclass._init.apply(this, arguments);
    },

    _rebuildItems: function () {
        return this.defaultItems();
    }
});
$.shortcut("bi.calculate_target_combo", BI.CalculateTargetCombo);