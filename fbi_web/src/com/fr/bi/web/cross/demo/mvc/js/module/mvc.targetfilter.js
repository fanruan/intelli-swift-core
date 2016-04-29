TargetFilterView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(TargetFilterView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-target-filter"
        })
    },

    _init: function () {
        TargetFilterView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var filterPane = this._createFilterPane();
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [filterPane, {
                type: "bi.button",
                text: "指标过滤——getValue()",
                height: 30,
                handler: function () {
                    BI.Msg.alert("指标过滤", JSON.stringify(filterPane.getValue()));
                }
            }],
            vgap: 20,
            hgap: 10
        })
    },

    _createFilterPane: function () {
        var wIds = BI.Utils.getAllWidgetIDs();
        var dId = BI.Utils.getAllDimDimensionIDs(wIds[0])[0];
        return BI.createWidget({
            type: "bi.target_filter",
            width: 600,
            height: 400,
            dId: dId
        })
    }
});

TargetFilterModel = BI.inherit(BI.Model, {});