/**
 * Created by roy on 15/11/10.
 */
CustomSortView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(CustomSortView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-custom-sort-view"
        })
    },
    _init: function () {
        CustomSortView.superclass._init.apply(this, arguments);
    },
    _render: function (vessel) {
        var o = this.options;
        var wIds = BI.Utils.getAllWidgetIDs();
        var dId = BI.Utils.getAllDimDimensionIDs(wIds[0])[0];
        var sort = BI.createWidget({
            type: "bi.custom_sort_pane",
            element: vessel,
            dId: dId
        });
        sort.populate();
    }
});

CustomSortModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(CustomSortModel.superclass._defaultConfig.apply(this, arguments), {})
    },
    _init: function () {
        CustomSortModel.superclass._init.apply(this, arguments);
    }
});