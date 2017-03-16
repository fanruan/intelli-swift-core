/**
 * Created by zcf on 12/22/2016.
 */
DataPaneView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(DataPaneView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: ""
        })
    },

    _init: function () {
        DataPaneView.superclass._init.apply(this, arguments);
    },
    _render: function (vessel) {
        this.pane = BI.createWidget({
            type: "bi.date_calendar_popup"
        });
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [this.pane]
        })
    }
});
DataPaneModel = BI.inherit(BI.Model, {});