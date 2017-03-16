/**
 * Created by roy on 16/4/6.
 */
BI.CalculateTargetComboShow = BI.inherit(BI.AbstractDimensionTargetComboShow, {

    constants: {
        USED_POSITION: 2,
        USED_DISPLAY_POSITION: 0,
        USED_HIDDEN_POSITION: 1
    },
    defaultItems: function () {
        return [
            [{
                text: BI.i18nText("BI-Show_Field"),
                value: BICst.TARGET_COMBO.SHOW_FIELD,
                cls: BI.Utils.isDimensionUsable(this.options.dId) ? "widget-combo-show-title-font" : ""
            }]
        ];
    },

    _defaultConfig: function () {
        return BI.extend(BI.CalculateTargetComboShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-calculate-target-combo"
        })
    },

    _init: function () {
        BI.CalculateTargetComboShow.superclass._init.apply(this, arguments);
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
$.shortcut("bi.calculate_target_combo_show", BI.CalculateTargetComboShow);