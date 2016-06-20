/**
 * Created by roy on 16/4/6.
 */
BI.CalculateTargetCombo = BI.inherit(BI.AbstractDimensionTargetCombo, {

    constants: {
        CHART_TYPE_POSITION: 2,
        CordonPos: 0
    },
    defaultItems: function () {
        return [
            [{
                text: BI.i18nText("BI-Style_Setting"),
                value: BICst.CALCULATE_TARGET_COMBO.FORM_SETTING,
                cls: "style-set-h-font"
            }],
            [{
                text: BI.i18nText("BI-Modify_Cal_Target"),
                value: BICst.CALCULATE_TARGET_COMBO.UPDATE_TARGET
            }],
            [{
                el: {
                    text: BI.i18nText("BI-Chart_Type"),
                    value: BICst.TARGET_COMBO.CHART_TYPE,
                    iconCls1: "",
                    disabled: true
                },
                children: [{
                    text: BI.i18nText("BI-Column_Chart"),
                    value: BICst.WIDGET.AXIS,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Stacked_Chart"),
                    value: BICst.WIDGET.ACCUMULATE_AXIS,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Line_Chart"),
                    value: BICst.WIDGET.LINE,
                    cls: "dot-e-font"
                }, {
                    text: BI.i18nText("BI-Area_Chart"),
                    value: BICst.WIDGET.AREA,
                    cls: "dot-e-font"
                }]
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
        var o = this.options;
        var item = this.defaultItems();
        var wId = BI.Utils.getWidgetIDByDimensionID(o.dId);
        var regionType = BI.Utils.getRegionTypeByDimensionID(o.dId);
        var wType = BI.Utils.getWidgetTypeByID(wId);
        switch (wType) {
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                item[this.constants.CordonPos][0].cls = "";
                item[this.constants.CordonPos][0] = {
                    el: item[this.constants.CordonPos][0],
                    children: [{
                        text: BI.i18nText("BI-Cordon") + "(" + BI.i18nText("BI-Horizontal") + ")",
                        value: BICst.TARGET_COMBO.CORDON
                    }]
                };
                item[this.constants.CHART_TYPE_POSITION][0].el.disabled = true;
                break;
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                item[this.constants.CordonPos][0].cls = "";
                item[this.constants.CordonPos][0] = {
                    el: item[this.constants.CordonPos][0],
                    children: [{
                        text: BI.i18nText("BI-Cordon") + "(" + BI.i18nText("BI-Horizontal") + ")",
                        value: BICst.TARGET_COMBO.CORDON
                    }]
                };
                item[this.constants.CHART_TYPE_POSITION][0].el.disabled = false;
                break;
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.BUBBLE:
                var text = BI.i18nText("BI-Horizontal");
                switch (regionType) {
                    case BICst.REGION.TARGET1:
                        text = BI.i18nText("BI-Horizontal");
                        break;
                    case BICst.REGION.TARGET2:
                        text = BI.i18nText("BI-Vertical");
                        break;
                    case BICst.REGION.TARGET3:
                        return;
                }
                item[this.constants.CordonPos][0].cls = "";
                item[this.constants.CordonPos][0] = {
                    el: item[this.constants.CordonPos][0],
                    children: [{
                        text: BI.i18nText("BI-Cordon") + "(" + text + ")",
                        value: BICst.TARGET_COMBO.CORDON
                    }]
                };
                item[this.constants.CHART_TYPE_POSITION][0].el.disabled = true;
                break;
            default:
                item[this.constants.CHART_TYPE_POSITION][0].el.disabled = false;
                break;
        }
        return item;
    },
    _createValue: function () {
        var o = this.options;
        var used = BI.Utils.isDimensionUsable(o.dId);
        var selectedValue = used ? BICst.CALCULATE_TARGET_COMBO.DISPLAY : BICst.CALCULATE_TARGET_COMBO.HIDDEN;
        return [{value: selectedValue}];
    }
});
$.shortcut("bi.calculate_target_combo", BI.CalculateTargetCombo);