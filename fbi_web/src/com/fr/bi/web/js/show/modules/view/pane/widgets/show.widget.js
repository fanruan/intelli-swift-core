/**
 * @class BIShow.WidgetView
 * @extends BI.View
 * @type {*|void|Object}
 */
BIShow.WidgetView = BI.inherit(BI.View, {

    _constants: {
        SHOW_CHART: 1,
        SHOW_FILTER: 2
    },

    _defaultConfig: function () {
        return {
            baseCls: "bi-dashboard-widget"
        }
    },

    _init: function () {
        BIShow.WidgetView.superclass._init.apply(this, arguments);
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

        this.filterTab = BI.createWidget({
            type: "bi.tab",
            cardCreator: function(v){
                switch (v) {
                    case self._constants.SHOW_CHART:
                        self.tableChartTab = BI.createWidget({
                            type: "bi.table_chart_manager",
                            wId: self.model.get("id")
                        });
                        self.tableChartPopupulate = BI.debounce(BI.bind(self.tableChartTab.populate, self.tableChartTab), 0);
                        self.tableChartTab.on(BI.TableChartManager.EVENT_CHANGE, function(widget){
                            self.model.set(widget);
                        });
                        return self.tableChartTab;
                    case self._constants.SHOW_FILTER:
                        self.widgetFilter = BI.createWidget({
                            type: "bi.widget_filter",
                            wId: self.model.get("id")
                        });
                        self.widgetFilter.on(BI.WidgetFilter.EVENT_REMOVE_FILTER, function(widget){
                            self.model.set(widget);
                        });
                        return self.widgetFilter;
                }
            }
        });
        this.filterTab.setSelect(this._constants.SHOW_CHART);

        this.widget = BI.createWidget({
            type: "bi.vtape",
            element: vessel,
            items: [{
                el: this.title,
                height: 32
            }, this.filterTab]
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
                self.filterTab.setSelect(self.filterTab.getSelect() === self._constants.SHOW_FILTER ?
                    self._constants.SHOW_CHART : self._constants.SHOW_FILTER);
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
                widgetType: BICst.Widget.TABLE_SHOW
            });
            combo.on(BI.WidgetCombo.EVENT_CHANGE, function (type) {
                switch (type) {
                    case BICst.DASHBOARD_WIDGET_EXPAND:
                        self._expandWidget();
                        break;
                    case BICst.DASHBOARD_WIDGET_EXCEL:
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
        var selectedTab = this.filterTab.getSelect();
        this.filterTab.setSelect(this._constants.SHOW_CHART);
        BI.isNotNull(this.widgetFilter) && this.widgetFilter.populate();
        this.tableChartPopupulate();
        this.filterTab.setSelect(selectedTab);
    },

    _expandWidget: function () {
        var wId = this.model.get("id");
        BIShow.FloatBoxes.open("detail", "detail", {}, this, {
            id: wId
        })

    },
    listenEnd: function () {

    },

    change: function (changed) {
        //阻断实时刷新
        // if (BI.has(changed, "dimensions") ||
        //     BI.has(changed, "sort") ||
        //     BI.has(changed, "linkages")) {
        //     this.tableChartPopupulate();
        // }
        // if(BI.has(changed, "clicked") || BI.has(changed, "filter_value")) {
        //     this._refreshTableAndFilter();
        // }
    },

    local: function () {
        return false;
    },

    refresh: function () {
        this._buildWidgetTitle();
        this.tableChartPopupulate();
    }
});
