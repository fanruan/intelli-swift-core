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
            type: "bi.show_dimension_manager",
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
        this.pane.on(BI.ShowDimensionsManager.EVENT_CHANGE, function () {
            var values = this.getValue();
            var view = values.view, scopes = values.scopes || {};
            //去除不需要的scope
            BI.remove(scopes, function (regionType) {
                return BI.isNull(view[regionType]) || view[regionType].length === 0;
            });
            self.model.set(values);
            //即使区域没有变化也要刷新一次
            this.populate();
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
        if (BI.has(changed, "type") || BI.has(changed, "subType")) {
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