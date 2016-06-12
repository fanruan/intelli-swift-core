/**
 * @class BIShow.DetailTableDetailView
 * @extends BI.View
 * 明细表的详细设置————诡异的命名
 */
BIShow.DetailTableDetailView = BI.inherit(BI.BarFloatSection, {

    constants: {
        DETAIL_NORTH_HEIGHT: 40,
        DETAIL_TAB_HEIGHT: 40,
        DETAIL_WEST_WIDTH: 280,
        DETAIL_DATA_STYLE_HEIGHT: 240,
        DETAIL_GAP_NORMAL: 10,
        DETAIL_PANE_HORIZONTAL_GAP: 20

    },

    _defaultConfig: function () {
        return BI.extend(BIShow.DetailTableDetailView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-widget-attribute-setter"
        })
    },

    _init: function () {
        BIShow.DetailTableDetailView.superclass._init.apply(this, arguments);
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
            type: "bi.dimensions_manager",
            wId: this.model.get("id"),
            dimensionCreator: function (dId, regionType, op) {
                var relationItem = op.relationItem;
                if (BI.isNotNull(relationItem)) {
                    self.model.set("setRelation", {
                        dId: dId,
                        relationItem: op.relationItem
                    });
                }

                if (!dimensionsVessel[dId]) {
                    dimensionsVessel[dId] = BI.createWidget({
                        type: "bi.layout"
                    });
                    var dimensions = self.model.cat("dimensions");
                    if (!BI.has(dimensions, dId)) {
                        self.model.set("addDimension", {
                            dId: dId,
                            regionType: regionType,
                            src: op
                        });
                    }
                }
                self.addSubVessel(dId, dimensionsVessel[dId]).skipTo(regionType + "/" + dId, dId, "dimensions." + dId);
                return dimensionsVessel[dId];

            }
        });

        this.dimensionsManager.on(BI.DimensionsManager.EVENT_CHANGE, function () {
            var values = this.getValue();
            self.model.set(values);
            this.populate();
        });


        return this.dimensionsManager;
    },

    refresh: function () {
        var self = this;
        this.dimensionsManager.populate();
    }
});