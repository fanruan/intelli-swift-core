/**
 * Created by roy on 16/4/6.
 */
BI.CalculateTargetCombo = BI.inherit(BI.AbstractDimensionTargetCombo, {

    constants: {
        USED_POSITION: 2,
        USED_DISPLAY_POSITION: 0,
        USED_HIDDEN_POSITION: 1
    },
    defaultItems: function () {
        return [
            [{
                text: BI.i18nText("BI-Modify_Cal_Target"),
                value: BICst.CALCULATE_TARGET_COMBO.UPDATE_TARGET
            }],
            //[{
            //    text: BI.i18nText("BI-Display"),
            //    value: BICst.CALCULATE_TARGET_COMBO.DISPLAY,
            //    cls: "dot-ha-font"
            //}, {
            //    text: BI.i18nText("BI-Hidden"),
            //    value: BICst.CALCULATE_TARGET_COMBO.HIDDEN,
            //    cls: "dot-ha-font"
            //}],
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
    },
    _createValue: function () {
        var o = this.options;
        var used = BI.Utils.isDimensionUsable(o.dId);
        var selectedValue = used ? BICst.CALCULATE_TARGET_COMBO.DISPLAY : BICst.CALCULATE_TARGET_COMBO.HIDDEN;
        return [{value: selectedValue}];
    }
});
$.shortcut("bi.calculate_target_combo", BI.CalculateTargetCombo);