/**
 * Created by GUY on 2016/3/17.
 * @class BI.CombineChartTargetScope
 * @extends BI.Widget
 */
BI.CombineChartTargetScope = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CombineChartTargetScope.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-combine-chart-target-scope",
        });
    },

    _init: function () {
        BI.CombineChartTargetScope.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combo = BI.createWidget({
            type: "bi.icon_combo",
            el: {
                isShowDown: false,
            },
            iconWidth: 24,
            iconHeight: 24,
            items: [{
                text: BI.i18nText("BI-Stacked_Chart"),
                title: BI.i18nText("BI-Stacked_Chart"),
                value: BICst.ACCUMULATE_TYPE.COLUMN,
                iconClass: "drag-axis-accu-icon",
                iconWidth: 24,
                iconHeight: 24,
            }, {
                text: BI.i18nText("BI-Accumulate_Area") + "(" + BI.i18nText("BI-Polyline") + ")",
                title: BI.i18nText("BI-Accumulate_Area") + "(" + BI.i18nText("BI-Polyline") + ")",
                value: BICst.ACCUMULATE_TYPE.AREA_NORMAL,
                iconClass: "area-chart-style-broken-icon",
                iconWidth: 24,
                iconHeight: 24,
            }, {
                text: BI.i18nText("BI-Accumulate_Area") + "(" + BI.i18nText("BI-Curve") + ")",
                title: BI.i18nText("BI-Accumulate_Area") + "(" + BI.i18nText("BI-Curve") + ")",
                value: BICst.ACCUMULATE_TYPE.AREA_CURVE,
                iconClass: "area-chart-style-curve-icon",
                iconWidth: 24,
                iconHeight: 24,

            }, {
                text: BI.i18nText("BI-Accumulate_Area") + "(" + BI.i18nText("BI-Right_Angled_Polyline") + ")",
                title: BI.i18nText("BI-Accumulate_Area") + "(" + BI.i18nText("BI-Right_Angled_Polyline") + ")",
                value: BICst.ACCUMULATE_TYPE.AREA_RIGHT_ANGLE,
                iconClass: "area-chart-style-vertical-icon",
                iconWidth: 24,
                iconHeight: 24,
            }]
        });

        this.combo.on(BI.IconCombo.EVENT_CHANGE, function () {
            self.fireEvent(BI.CombineChartTargetScope.EVENT_CHANGE, arguments);
        });

        this._createMask();

        BI.createWidget({
            type: "bi.center_adapt",
            element: this.element,
            items: [this.combo]
        });
    },

    _createMask: function () {
        this.mask = BI.createWidget({
            type: "bi.layout",
            cls: "bi-accumulate-selector-masker",
            width: "100%",
            height: "100%"
        })
        BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: this.mask,
                top: 0,
                left: 0,
                bottom: 0
            }],
            element: this.element
        });
        this.mask.setVisible(false);
    },

    setEnable: function (v) {
        this.mask.setVisible(!v);
        this.combo.setEnable(v);
    },

    getValue: function () {
        var values = this.combo.getValue();
        return {chartType: values[0] || BICst.ACCUMULATE_TYPE.COLUMN};
    },

    setValue: function (data) {
        data = data || {};
        this.combo.setValue(data.chartType || BICst.ACCUMULATE_TYPE.COLUMN);
    },

    populate: function () {

    }
});
BI.CombineChartTargetScope.EVENT_CHANGE = "CombineChartTargetScope.EVENT_CHANGE";
$.shortcut("bi.combine_chart_target_scope", BI.CombineChartTargetScope);