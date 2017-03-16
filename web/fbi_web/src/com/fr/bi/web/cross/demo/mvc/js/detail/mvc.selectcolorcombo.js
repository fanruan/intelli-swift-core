SelectColorComboView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SelectColorComboView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-SelectColorCombo bi-mvc-layout"
        })
    },

    _init: function () {
        SelectColorComboView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var item = BI.createWidget({
            type: "bi.chart_setting_wrap_select_color_item",
            header: "默认",
            text: ["#5caae4", "#70cc7f", "#ebbb67", "#e97e7b", "#6ed3c9"],
            value: ["#5caae4", "#70cc7f", "#ebbb67", "#e97e7b", "#6ed3c9"]
        });
        var trigger = BI.createWidget({
            type: "bi.chart_setting_select_color_trigger",
            width: 130,
            items: BICst.CHART_COLORS
        });
        trigger.setValue(["#5caae4", "#70cc7f", "#ebbb67", "#e97e7b", "#6ed3c9"]);

        var popup = BI.createWidget({
            type: "bi.chart_setting_select_color_popup"
        });
        popup.populate(BICst.CHART_COLORS);
        var combo = BI.createWidget({
            type: "bi.chart_setting_select_color_combo",
            width: 130
        });
        combo.populate(BICst.CHART_COLORS);
        combo.setValue(["#5caae4", "#70cc7f", "#ebbb67", "#e97e7b", "#6ed3c9"]);
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            vgap: 20,
            hgap: 30,
            items: [item, trigger, popup, combo]
        })
    }
});

SelectColorComboModel = BI.inherit(BI.Model, {});