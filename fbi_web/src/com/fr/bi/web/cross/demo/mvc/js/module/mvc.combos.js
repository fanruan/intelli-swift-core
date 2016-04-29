CombosView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(CombosView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-combos bi-mvc-layout"
        })
    },

    _init: function () {
        CombosView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var wids = BI.Utils.getAllWidgetIDs();
        var dids = BI.Utils.getAllDimDimensionIDs(wids[0]);
        BI.createWidget({
            type: "bi.vertical",
            hgap: 200,
            vgap: 10,
            element: vessel,
            items: [, {
                type: "bi.label",
                height: 25,
                text: "表格组件下拉框"
            }, {
                type: "bi.widget_combo",
                width: 32,
                widgetType: BICst.Widget.TABLE
            }, {
                type: "bi.label",
                height: 25,
                text: "文本类型维度下拉框"
            }, {
                type: "bi.dimension_string_combo",
                dId: dids[0],
                width: 25
            }, {
                type: "bi.label",
                height: 25,
                text: "数值类型维度下拉框"
            }, {
                type: "bi.dimension_number_combo",
                dId: dids[0],
                width: 25
            }, {
                type: "bi.label",
                height: 25,
                text: "日期类型维度下拉框"
            }, {
                type: "bi.dimension_date_combo",
                dId: dids[0],
                width: 25
            }, {
                type: "bi.label",
                height: 25,
                text: "指标下拉框"
            }, {
                type: "bi.target_combo",
                width: 25
            }]
        })
    }
});

CombosModel = BI.inherit(BI.Model, {});