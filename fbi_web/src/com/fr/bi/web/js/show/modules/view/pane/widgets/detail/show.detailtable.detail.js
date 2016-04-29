/**
 * @class BIShow.DetailTableDetailView
 * @extend BI.View
 * 明细表的详细设置————诡异的命名
 */
BIShow.DetailTableDetailView = BI.inherit(BI.View, {

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
            baseCls: "bi-detail-detail-view"
        })
    },

    _init: function () {
        BIShow.DetailTableDetailView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.border",
            element: vessel,
            items: {
                north: {el: this._buildNorth(), height: this.constants.DETAIL_NORTH_HEIGHT},
                west: {el: this._buildWest(), width: this.constants.DETAIL_WEST_WIDTH},
                center: {el: this._buildCenter()}
            }
        });
    },

    _buildNorth: function () {
        var self = this;
        var input = BI.createWidget({
            type: "bi.text_editor",
            height: 22,
            width: 400,
            validationChecker: function (v) {

            }
        });
        input.setValue(this.model.get("name"));
        input.on(BI.TextEditor.EVENT_CONFIRM, function () {
            self.model.set("name", input.getValue());
        });
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
            cls: "widget-attr-north",
            items: {
                left: [input],
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
                    type: "bi.detail_select_data_pane",
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
        var self = this;
        var table = this._createTable();
        this.center = BI.createWidget({
            type: "bi.default",
            cls: "widget-attr-chart"
        });
        this.region = BI.createWidget({
            type: "bi.detail_region"
        });


        var tab = BI.createWidget({
            type: "bi.data_style_tab",
            cardCreator: BI.bind(self._createTabs, this)
        });

        return BI.createWidget({
            type: "bi.absolute",
            cls: "widget-attr-center",
            items: [{
                el: {
                    type: "bi.border",
                    cls: "widget-show-data-pane",
                    items: {
                        north: {
                            el: tab,
                            height: this.constants.DETAIL_DATA_STYLE_HEIGHT,
                            bottom: this.constants.DETAIL_GAP_NORMAL
                        },
                        center: this.table
                    }
                },
                left: 0,
                right: this.constants.DETAIL_PANE_HORIZONTAL_GAP,
                top: this.constants.DETAIL_GAP_NORMAL,
                bottom: this.constants.DETAIL_GAP_NORMAL
            }]
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

        this.dimensionsManager.on(BI.DimensionsManager.EVENT_CHANGE, function () {
            var values = this.getValue();
            self.model.set(values);
            this.populate();
        });


        return this.dimensionsManager;
    },

    _createStyle: function () {
        return BI.createWidget({
            type: "bi.label",
            text: "Style"
        });
    },

    splice: function (old, key1, key2) {
        if (key1 === "dimensions") {
            this.dimensionsManager.populate();
            BI.Broadcasts.send(old._src.id, true);
        }
    },

    _createTabs: function (v) {
        switch (v) {
            case BICst.DETAIL_TAB_TYPE_DATA:
                return this._createTypeAndData();
            case BICst.DETAIL_TAB_STYLE:
                return this._createStyle();
        }
    },

    _createTable: function () {
        this.table = BI.createWidget({
            type: "bi.detail_table",
            cls: "widget-attr-chart",
            wId: this.model.get("id")
        });
        return this.table;
    },


    change: function (changed, prev) {
        if (BI.has(changed, "dimensions") ||
            BI.has(changed, "view") ||
            (BI.has(changed, "page") && changed.page !== 0) ||
            (BI.has(changed, "target_relation"))) {
            this.table.populate();
        }
        if (BI.has(changed, "settings")) {
            this.table.populate();
        }
        if (BI.has(changed, "dimensions")) {
            if (BI.size(changed.dimensions) > BI.size(prev.dimensions)) {
                var result = BI.find(changed.dimensions, function (did, dimension) {
                    return !BI.has(prev.dimensions, did);
                });
                BI.Broadcasts.send(result._src.id, true);
            }
        }
    },


    local: function () {
        if (this.model.has("sorted")) {
            this.model.get("sorted");
            return true;
        }
        return false;
    },

    refresh: function () {
        var self = this;
        this.dimensionsManager.populate();
        this.table.populate();
    }
});
