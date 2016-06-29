BIDezi.TreeDetailView = BI.inherit(BI.View, {

    constants: {
        DETAIL_NORTH_HEIGHT: 40,
        DETAIL_TAB_WIDTH: 100,
        DETAIL_TAB_HEIGHT: 40,
        DETAIL_WEST_WIDTH: 280,
        DETAIL_DATA_STYLE_HEIGHT: 240,
        DETAIL_GAP_NORMAL: 10,
        DETAIL_PANE_HORIZONTAL_GAP: 10

    },

    _defaultConfig: function () {
        return BI.extend(BIDezi.TreeDetailView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-widget-attribute-setter"
        })
    },

    _init: function () {
        BIDezi.TreeDetailView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var mask = BI.createWidget();
        mask.element.__buildZIndexMask__(0);
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: mask,
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }, {
                el: {
                    type: "bi.htape",
                    cls: "widget-attribute-setter-container",
                    items: [{
                        el: this._buildWest(),
                        width: this.constants.DETAIL_WEST_WIDTH
                    }, {
                        type: "bi.vtape",
                        items: [{
                            el: this._buildNorth(), height: this.constants.DETAIL_NORTH_HEIGHT
                        }, {
                            el: this._buildCenter()
                        }]
                    }]
                },
                left: 20,
                right: 20,
                top: 20,
                bottom: 20
            }]
        });
    },

    _buildNorth: function () {
        var self = this;
        var shrink = BI.createWidget({
            type: "bi.button",
            height: 25,
            title: BI.i18nText('BI-Return_To_Dashboard'),
            text: BI.i18nText('BI-Detail_Set_Complete'),
            handler: function () {
                self.notifyParentEnd();
            }
        });
        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            items: {
                right: [shrink]
            },
            lhgap: this.constants.DETAIL_PANE_HORIZONTAL_GAP,
            rhgap: this.constants.DETAIL_PANE_HORIZONTAL_GAP
        });
    },

    _buildWest: function () {
        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: {
                    type: BI.Utils.isRealTime() ? "bi.select_string_4_realtime" : "bi.tree_select_data",
                    wId: this.model.get("id"),
                    cls: "widget-select-data-pane"
                },
                left: this.constants.DETAIL_PANE_HORIZONTAL_GAP,
                top: this.constants.DETAIL_GAP_NORMAL,
                bottom: this.constants.DETAIL_GAP_NORMAL,
                right: this.constants.DETAIL_PANE_HORIZONTAL_GAP
            }],
            cls: "widget-attr-west"
        });
    },

    _buildCenter: function () {
        var combo = this._createCombo();
        var top = BI.createWidget({
            type: "bi.vtape",
            cls: "widget-top-wrapper",
            items: [{
                el: {
                    type: "bi.button_group",
                    items: BI.createItems([{
                        text: BI.i18nText("BI-Data"),
                        selected: true
                    }], {
                        type: "bi.line_segment_button",
                        height: this.constants.DETAIL_TAB_HEIGHT
                    }),
                    height: this.constants.DETAIL_TAB_HEIGHT,
                    layouts: [{
                        type: "bi.absolute_center_adapt",
                        items: [{
                            type: "bi.center",
                            width: this.constants.DETAIL_TAB_WIDTH,
                            height: this.constants.DETAIL_TAB_HEIGHT
                        }]
                    }]
                },
                height: this.constants.DETAIL_TAB_HEIGHT
            }, {
                el: this._createRegion()
            }]
        });

        return BI.createWidget({
            type: "bi.absolute",
            cls: "widget-attr-center",
            items: [{
                el: {
                    type: "bi.vtape",
                    items: [{
                        el: top,
                        height: this.constants.DETAIL_DATA_STYLE_HEIGHT - this.constants.DETAIL_NORTH_HEIGHT
                    }, {
                        el: {
                            type: "bi.absolute",
                            cls: "widget-center-wrapper",
                            items: [{
                                el: combo,
                                left: 10,
                                right: 10,
                                top: 10
                            }]
                        }
                    }],
                    vgap: 10
                },
                left: 0,
                right: this.constants.DETAIL_PANE_HORIZONTAL_GAP,
                top: this.constants.DETAIL_GAP_NORMAL,
                bottom: this.constants.DETAIL_GAP_NORMAL
            }]
        });
    },


    _createRegion: function () {
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

    _createCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.select_tree_data_combo",
            wId: this.model.get("id")
        });
        this.combo.on(BI.SelectTreeDataCombo.EVENT_CONFIRM, function () {
            self.model.set("value", self.combo.getValue());
        });
        return this.combo;
    },

    splice: function (old, key1, key2) {
        if (key1 === "dimensions") {
            this.dimensionsManager.populate();
        }
    },


    change: function (changed, prev) {
        if (BI.has(changed, "value")) {
            this.combo.setValue(this.model.get("value"))
        }
    },


    local: function () {
        if (this.model.has("addDimension")) {
            this.model.get("addDimension");
            return true;
        }
        if (this.model.has("sorted")) {
            this.model.get("sorted");
            return true;
        }
        return false;
    },

    refresh: function () {
        this.dimensionsManager.populate();
        this.combo.setValue(this.model.get("value"));
    }
});