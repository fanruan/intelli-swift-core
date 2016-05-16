/**
 * 指标弹出明细表
 *
 * Created by GUY on 2016/5/13.
 * @class BI.DetailTablePopup
 * @extends BI.Widget
 */
BI.DetailTablePopup = BI.inherit(BI.Widget, {

    constants: {
        DETAIL_NORTH_HEIGHT: 40,
        DETAIL_TAB_HEIGHT: 40,
        DETAIL_WEST_WIDTH: 280,
        DETAIL_DATA_STYLE_HEIGHT: 200,
        DETAIL_GAP_NORMAL: 10,
        DETAIL_PANE_HORIZONTAL_GAP: 20
    },

    _defaultConfig: function () {
        return BI.extend(BI.DetailTablePopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-table-popup",
            dId: ""
        });
    },

    _init: function () {
        BI.DetailTablePopup.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.DetailTablePopupModel({
            dId: o.dId
        });
        BI.createWidget({
            type: "bi.border",
            element: this.element,
            items: {
                north: {el: this._buildNorth(), height: this.constants.DETAIL_NORTH_HEIGHT},
                west: {el: this._buildWest(), width: this.constants.DETAIL_WEST_WIDTH},
                center: {el: this._buildCenter()}
            }
        });
    },

    _buildNorth: function () {
        var self = this;
        var shrink = BI.createWidget({
            type: "bi.button",
            height: 25,
            title: BI.i18nText('BI-Return_To_Dashboard'),
            text: BI.i18nText('BI-Detail_Set_Complete')
        });
        shrink.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.DetailTablePopup.EVENT_COMPLETE);
        });
        return BI.createWidget({
            type: "bi.right_vertical_adapt",
            items: [shrink],
            hgap: this.constants.DETAIL_PANE_HORIZONTAL_GAP
        });
    },

    _buildWest: function () {
        //TODO 实时报表
        return BI.createWidget({
            type: "bi.absolute",
            cls: "widget-attr-west",
            items: [{
                el: {
                    type: "bi.detail_table_popup_select_data",
                    cls: "widget-select-data-pane",
                    model: this.model
                },
                left: this.constants.DETAIL_PANE_HORIZONTAL_GAP,
                top: this.constants.DETAIL_GAP_NORMAL,
                bottom: this.constants.DETAIL_GAP_NORMAL,
                right: this.constants.DETAIL_PANE_HORIZONTAL_GAP
            }]
        });
    },

    _buildCenter: function () {
        var self = this;
        var table = this._createTable();
        //var tab = BI.createWidget({
        //    type: "bi.data_style_tab",
        //    cls: "widget-top-wrapper",
        //    cardCreator: BI.bind(self._createTabs, this)
        //});
        var tab = this._createTypeAndData();

        return BI.createWidget({
            type: "bi.absolute",
            cls: "widget-attr-center",
            items: [{
                el: {
                    type: "bi.border",
                    items: {
                        north: {
                            el: tab,
                            height: this.constants.DETAIL_DATA_STYLE_HEIGHT,
                            bottom: this.constants.DETAIL_GAP_NORMAL
                        },
                        center: table
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
            type: "bi.detail_table_popup_dimensions_manager",
            cls: "widget-top-wrapper",
            model: this.model,
            dimensionCreator: function (dId, regionType, op) {

                if (!dimensionsVessel[dId]) {
                    dimensionsVessel[dId] = BI.createWidget({
                        type: "bi.layout"
                    });
                    //var dimensions = self.model.cat("dimensions");
                    //if (!BI.has(dimensions, dId)) {
                    //    self.model.set("addDimension", {
                    //        dId: dId,
                    //        regionType: regionType,
                    //        src: op
                    //    });
                    //}
                }
                return dimensionsVessel[dId];

            }
        });

        this.dimensionsManager.on(BI.DetailTablePopupDimensionsManager.EVENT_CHANGE, function () {
            var values = this.getValue();
            BI.Msg.toast(JSON.stringify(values));
        });


        return this.dimensionsManager;
    },

    _createTable: function () {
        return BI.createWidget({
            type: "bi.layout",
            cls: "widget-center-wrapper"
        });
        //var self = this;
        //this.table = BI.createWidget({
        //    type: "bi.detail_table",
        //    cls: "widget-center-wrapper",
        //    wId: this.model.get("id")
        //});
        //this.table.on(BI.DetailTable.EVENT_CHANGE, function (ob) {
        //
        //});
        //return this.table;
    }
});

BI.DetailTablePopup.EVENT_COMPLETE = "DetailTablePopup.EVENT_COMPLETE";
$.shortcut('bi.detail_table_popup', BI.DetailTablePopup);