/**
 * @class BIDezi.WidgetView
 * @extends BI.View
 * @type {*|void|Object}
 */
BIDezi.WidgetView = BI.inherit(BI.View, {

    _constants: {
        SHOW_CHART: 1,
        SHOW_FILTER: 2
    },

    _defaultConfig: function () {
        return BI.extend(BIDezi.WidgetView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dashboard-widget"
        })
    },

    _init: function () {
        BIDezi.WidgetView.superclass._init.apply(this, arguments);
        var self = this, wId = this.model.get("id");
        BI.Broadcasts.on(wId, function(dId, v){
            if(BI.isNotNull(dId)) {
                var clicked = self.model.get("clicked") || {};
                var allFromIds = BI.Utils.getAllLinkageFromIdsByID(BI.Utils.getWidgetIDByDimensionID(dId));
                //这条链上所有的其他clicked都应当被清掉
                BI.each(clicked, function(cid, click){
                    if(allFromIds.contains(cid)){
                        delete clicked[cid];
                    }
                });
                if(BI.isNull(v)) {
                    delete clicked[dId];
                } else {
                    clicked[dId] = v;
                }
                self.model.set("clicked", clicked);
            } else {
                self._refreshTableAndFilter();
            }
        });
    },

    _render: function (vessel) {
        var self = this;
        this.title = this._buildWidgetTitle();
        this.tableChart = BI.createWidget({
            type: "bi.table_chart_manager",
            wId: self.model.get("id")
        });
        this.tableChartPopupulate = BI.debounce(BI.bind(this.tableChart.populate, this.tableChart), 0);
        this.tableChart.on(BI.TableChartManager.EVENT_CHANGE, function(widget){
            self.model.set(widget);
        });

        this.widget = BI.createWidget({
            type: "bi.vtape",
            element: vessel,
            items: [{
                el: this.title,
                height: 32
            }, this.tableChart]
        })
    },

    _buildWidgetTitle: function () {
        var self = this, o = this.options;
        if (!this.title) {
            var text = this.title = BI.createWidget({
                type: "bi.label",
                cls: "dashboard-title-text",
                text: BI.Utils.getWidgetNameByID(this.model.get("id")),
                textAlign: "left",
                height: 32
            });

            var filter = BI.createWidget({
                type: "bi.icon_button",
                cls: "filter-font dashboard-title-filter",
                width: 32,
                height: 32
            });
            filter.on(BI.IconButton.EVENT_CHANGE, function () {
                if(BI.isNull(self.filterPane)) {
                    self.filterPane = BI.createWidget({
                        type: "bi.widget_filter",
                        wId: self.model.get("id")
                    });
                    self.filterPane.on(BI.WidgetFilter.EVENT_REMOVE_FILTER, function(widget){
                        self.model.set(widget);
                    });
                    BI.createWidget({
                        type: "bi.absolute",
                        element: self.$vessel,
                        items: [{
                            el: self.filterPane,
                            top: 32,
                            left: 0,
                            right: 0,
                            bottom: 0
                        }]
                    });
                    return;
                }
                self.filterPane.setVisible(!self.filterPane.isVisible());
            });


            var expand = BI.createWidget({
                type: "bi.icon_button",
                width: 32,
                height: 32,
                cls: "dashboard-widget-combo-detail-set-font dashboard-title-detail"
            });
            expand.on(BI.IconButton.EVENT_CHANGE, function () {
                self._expandWidget();
            });

            var combo = BI.createWidget({
                type: "bi.widget_combo",
                widgetType: BICst.Widget.TABLE
            });
            combo.on(BI.WidgetCombo.EVENT_CHANGE, function (type) {
                switch (type) {
                    case BICst.DASHBOARD_WIDGET_DELETE:
                        self.model.destroy();
                        break;
                    case BICst.DASHBOARD_WIDGET_EXPAND:
                        self._expandWidget();
                        break;
                    case BICst.DASHBOARD_WIDGET_DRILL:
                        var layer = BI.Layers.make(self.getName(), "body");
                        var linkage = BI.createWidget({
                            type: "bi.linkage",
                            element: layer,
                            wId: self.model.get("id")
                        });
                        linkage.on(BI.Linkage.EVENT_CONFIRM, function () {
                            var values = linkage.getValue();
                            self.model.set("linkages", values);
                            BI.Layers.remove(self.getName());
                        });
                        linkage.on(BI.Linkage.EVENT_CANCEL, function(){
                            BI.Layers.remove(self.getName());
                        });
                        linkage.populate();
                        BI.Layers.show(self.getName());
                        break;
                    case BICst.DASHBOARD_DETAIL_WIDGET_DRILL:
                        break;
                    case BICst.DASHBOARD_WIDGET_EXCEL:
                        break;
                    case BICst.DASHBOARD_WIDGET_COPY:
                        self.model.copy();
                        break;
                }
            });

            return BI.createWidget({
                type: "bi.border",
                cls: "dashboard-title",
                items: {
                    center: text,
                    east: {
                        el: BI.createWidget({
                            type: "bi.center_adapt",
                            cls: "operator-region",
                            items: [filter, expand, combo]
                        }),
                        width: 96
                    }
                }
            });
        } else {
            this.title.setValue(BI.Utils.getWidgetNameByID(this.model.get("id")));
        }
    },

    _refreshTableAndFilter: function(){
        BI.isNotNull(this.filterPane) && this.filterPane.populate();
        this.tableChartPopupulate();
    },

    _expandWidget: function () {
        var wId = this.model.get("id");
        var type = this.model.get("type");
        this.addSubVessel("detail", "body", {
            isLayer: true
        }).skipTo("detail", "detail", "detail", {}, {
            id: wId
        })
    },

    listenEnd: function () {

    },

    change: function (changed) {
        if (BI.has(changed, "bounds")) {
            //this.tableChart.resize();
        }
        if (BI.has(changed, "dimensions") ||
            BI.has(changed, "sort") ||
            BI.has(changed, "linkages")) {
            this.tableChartPopupulate();
        }
        if(BI.has(changed, "clicked") || BI.has(changed, "filter_value")) {
            this._refreshTableAndFilter();
        }
    },

    local: function () {
        return false;
    },

    refresh: function () {
        this._buildWidgetTitle();
        this.tableChartPopupulate();
    }
});