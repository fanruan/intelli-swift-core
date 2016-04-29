DimensionsManagerView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(DimensionsManagerView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-dimensions-manager bi-mvc-layout"
        })
    },

    _init: function () {
        DimensionsManagerView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var ids = BI.Utils.getAllWidgetIDs();
        var manager = BI.createWidget({
            type: "bi.dimensions_manager",
            element: vessel,
            wId: ids[0],
            dimensionCreator: function (dId, regionType, op) {
                return BI.createWidget({
                    type: "bi.label",
                    value: dId
                })
            }
        });
        manager.on(BI.DimensionsManager.EVENT_CHANGE, function () {
            BI.Msg.toast(JSON.stringify(manager.getValue()));
        });
        manager.populate();
    }
});

DimensionsManagerModel = BI.inherit(BI.Model, {});