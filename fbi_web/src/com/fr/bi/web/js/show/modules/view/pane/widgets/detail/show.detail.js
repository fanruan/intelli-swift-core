/**
 * Created by GUY on 2015/6/24.
 */
BIShow.DetailView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(BIShow.DetailView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-widget-attribute-setter"
        })
    },

    _init: function () {
        BIShow.DetailView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var self = this;
        var dimensionsVessel = {};
        this.pane = BI.createWidget({
            type: "bi.dimension_switch_pane_show",
            element: vessel,
            wId: this.model.get("id"),
            dimensionCreator: function (dId, regionType, op) {
                if (!dimensionsVessel[dId]) {
                    dimensionsVessel[dId] = BI.createWidget({
                        type: "bi.layout"
                    });
                }
                self.addSubVessel(dId, dimensionsVessel[dId]).skipTo(regionType + "/" + dId, dId, "dimensions." + dId);
                return dimensionsVessel[dId];
            }
        });
        this.pane.on(BI.DimensionSwitchPaneShow.EVENT_CHANGE, function () {
            self.model.set("view", this.getValue());
        });
    },

    _refreshDimensions: function () {
        var self = this;
        BI.each(self.model.cat("view"), function (regionType, dids) {
            BI.each(dids, function (i, dId) {
                self.skipTo(regionType + "/" + dId, dId, "dimensions." + dId, {}, {force: true});
            });
        });
    },

    change: function (changed, prev) {
        if (BI.has(changed, "type") || BI.has(changed, "sub_type")) {
            this._refreshDimensions();
        }
        if (BI.has(changed, "dimensions")) {
            this._refreshDimensions();
        }
        if (BI.has(changed, "view")) {
            this._refreshDimensions();
        }
    },

    refresh: function () {
        this.pane.populate();
        this._refreshDimensions();
    }
});