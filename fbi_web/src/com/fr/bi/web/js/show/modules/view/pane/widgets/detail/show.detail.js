/**
 * Created by GUY on 2015/6/24.
 */
BIShow.DetailView = BI.inherit(BI.BarFloatSection, {

    constants: {
        DETAIL_NORTH_HEIGHT: 40,
        DETAIL_TAB_HEIGHT: 40,
        DETAIL_WEST_WIDTH: 280,
        DETAIL_DATA_STYLE_HEIGHT: 320,
        DETAIL_GAP_NORMAL: 10,
        DETAIL_PANE_HORIZONTAL_GAP: 20,
        DETAIL_TAB_WIDTH: 200
    },

    _defaultConfig: function () {
        return BI.extend(BIShow.DetailView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-widget-attribute-setter"
        })
    },

    _init: function () {
        BIShow.DetailView.superclass._init.apply(this, arguments);
    },

    rebuildCenter: function (center) {
        var o = this.options;
        BI.createWidget({
            type: "bi.border",
            element: center,
            items: {
                center: {el: this._createTypeAndData()}
            }
        });

    },

    _createTypeAndData: function () {
        var self = this;
        var dimensionsVessel = {};
        this.dimensionsManager = BI.createWidget({
            type: "bi.dimensions_manager_show",
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

        this.dimensionsManager.on(BI.DimensionsManagerShow.EVENT_CHANGE, function () {
            var values = this.getValue();
            self.model.set(values);
            this.populate();
        });
        return this.dimensionsManager;
    },

    _refreshDimensions: function () {
        var self = this;
        BI.each(self.model.cat("view"), function (regionType, dids) {
            BI.each(dids, function (i, dId) {
                self.skipTo(regionType + "/" + dId, dId, "dimensions." + dId, {}, {force: true});
            });
        });
    },

    change: function(changed, prev) {
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
        this.dimensionsManager.populate();
        this._refreshDimensions();
    }
});