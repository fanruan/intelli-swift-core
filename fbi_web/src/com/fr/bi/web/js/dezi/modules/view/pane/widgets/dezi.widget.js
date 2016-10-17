/**
 * @class BIDezi.WidgetView
 * @extends BI.View
 * @type {*|void|Object}
 */
BIDezi.WidgetView = BI.inherit(BI.View, {

    _constants: {
        SHOW_CHART: 1,
        SHOW_FILTER: 2,
        TOOL_ICON_WIDTH: 20,
        TOOL_ICON_HEIGHT: 20
    },

    _defaultConfig: function () {
        return BI.extend(BIDezi.WidgetView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dashboard-widget"
        })
    },

    _init: function () {
        BIDezi.WidgetView.superclass._init.apply(this, arguments);
        var self = this, wId = this.model.get("id");
        BI.Broadcasts.on(BICst.BROADCAST.LINKAGE_PREFIX + wId, function (dId, v) {
            var clicked = self.model.get("clicked") || {};
            var allFromIds = BI.Utils.getAllLinkageFromIdsByID(BI.Utils.getWidgetIDByDimensionID(dId));
            //这条链上所有的其他clicked都应当被清掉 钻取的东西也清掉
            BI.each(clicked, function (cid, click) {
                if (allFromIds.contains(cid) || BI.Utils.isDimensionByDimensionID(cid)) {
                    delete clicked[cid];
                }
            });
            if (BI.isNull(v)) {
                delete clicked[dId];
            } else {
                clicked[dId] = v;
            }
            self.model.set("clicked", clicked);
        });
        BI.Broadcasts.on(BICst.BROADCAST.REFRESH_PREFIX + wId, function () {
            self._refreshTableAndFilter();
        });
        BI.Broadcasts.on(BICst.BROADCAST.RESET_PREFIX + wId, function () {
            self.model.set("clicked", {});
        });
        //全局样式的修改
        BI.Broadcasts.on(BICst.BROADCAST.GLOBAL_STYLE_PREFIX, function (globalStyle) {
            self._refreshGlobalStyle(globalStyle);
        });
    },

    _render: function (vessel) {
        var self = this;
        this._buildWidgetTitle();
        this._buildChartDrill();
        this._createTools();

        this.tableChart = BI.createWidget({
            type: "bi.table_chart_manager",
            wId: self.model.get("id"),
            status: BICst.WIDGET_STATUS.EDIT
        });

        this.hideDrill = BI.debounce(BI.bind(this.chartDrill.hideDrill, this.chartDrill), 3000);
        this.tableChartPopupulate = BI.debounce(BI.bind(this.tableChart.populate, this.tableChart), 0);
        this.tableChartResize = BI.debounce(BI.bind(this.tableChart.resize, this.tableChart), 0);
        this.tableChart.on(BI.TableChartManager.EVENT_CHANGE, function (widget) {
            self.model.set(widget);
        });
        this.tableChart.on(BI.TableChartManager.EVENT_CLICK_CHART, function (obj) {
            self._onClickChart(obj);
            self.hideDrill(self.model.get('id'))
        });

        this.widget = BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: this.tools,
                top: 0,
                right: 10
            }, {
                el: this.titleWrapper,
                top: 0,
                left: 0,
                right: 0
            }, {
                el: this.tableChart,
                left: 10,
                right: 10,
                top: 45,
                bottom: 10
            }, {
                el: this.chartDrill,
                left: 0,
                top: 0,
                right: 0
            }]
        });
        this.widget.element.hover(function () {
            self.tools.setVisible(true);
            self.widget.attr("items")[3].top = 6;
            self.widget.resize();
        }, function () {
            if (!self.widget.element.parent().parent().parent().hasClass("selected")) {
                self.tools.setVisible(false);
            }
            self.widget.attr("items")[3].top = 0;
            self.widget.resize();
        });

        BI.Broadcasts.on(BICst.BROADCAST.WIDGET_SELECTED_PREFIX, function () {
            if (!self.widget.element.parent().parent().parent().hasClass("selected")) {
                self.tools.setVisible(false);
                self.chartDrill.setDrillVisible(self.model.get("id"))
            }
        });
    },

    _buildWidgetTitle: function () {
        var self = this;
        var id = this.model.get("id");
        if (!this.title) {
            this.title = BI.createWidget({
                type: "bi.shelter_editor",
                cls: BI.Utils.getWSNamePosByID(id) === BICst.DASHBOARD_WIDGET_NAME_POS_LEFT ?
                    "dashboard-title-left" : "dashboard-title-center",
                value: BI.Utils.getWidgetNameByID(id),
                textAlign: "left",
                height: 25,
                allowBlank: false,
                errorText: function (v) {
                    if (BI.isNotNull(v) && BI.trim(v) !== "") {
                        return BI.i18nText("BI-Widget_Name_Can_Not_Repeat");
                    }
                    return BI.i18nText("BI-Widget_Name_Can_Not_Null");
                },
                validationChecker: function (v) {
                    return BI.Utils.checkWidgetNameByID(v, id);
                }
            });
            this.titleWrapper = BI.createWidget({
                type: "bi.absolute",
                height: 35,
                cls: "dashboard-widget-title",
                items: [{
                    el: this.title,
                    left: 10,
                    top: 10,
                    right: 10
                }]
            });
            this.title.on(BI.ShelterEditor.EVENT_CHANGE, function () {
                self.model.set("name", this.getValue());
            });
        } else {
            this.title.setValue(BI.Utils.getWidgetNameByID(id));
        }
    },

    _buildChartDrill: function () {
        var self = this;
        this.chartDrill = BI.createWidget({
            type: "bi.chart_drill",
            wId: this.model.get("id")
        });
        this.chartDrill.on(BI.ChartDrill.EVENT_CHANGE, function (widget) {
            self.model.set(widget);
        });
        this.chartDrill.populate();
    },

    _onClickChart: function (obj) {
        if (BI.has(obj, "clicked")) {
            this.model.set(obj);
        } else {
            this.chartDrill.populate(obj);
        }
    },

    _onClickLinkage: function () {
        var self = this, wId = this.model.get("id");

        var layer = BI.Layers.make(self.getName(), "body");
        var linkage = BI.createWidget({
            type: "bi.linkage",
            element: layer,
            wId: wId
        });
        linkage.on(BI.Linkage.EVENT_CONFIRM, function () {
            var values = linkage.getValue();
            self.model.set("linkages", values);
            BI.Layers.remove(self.getName());
        });
        linkage.on(BI.Linkage.EVENT_CANCEL, function () {
            BI.Layers.remove(self.getName());
        });
        linkage.populate();
        BI.Layers.show(this.getName());
    },
    _onClickShowName: function () {
        var settings = this.model.get("settings");
        settings.widget_setting.show_name = !BI.Utils.getWSShowNameByID(this.model.get("id"));
        this.model.set("settings", settings);
        this._refreshLayout();
    },
    _onClickNamePosLeft: function () {
        var settings = this.model.get("settings");
        settings.name_pos = BICst.DASHBOARD_WIDGET_NAME_POS_LEFT;
        this.model.set("settings", settings);
        this._refreshTitlePosition();
    },
    _onClickNamePosCenter: function () {
        var settings = this.model.get("settings");
        settings.name_pos = BICst.DASHBOARD_WIDGET_NAME_POS_CENTER;
        this.model.set("settings", settings);
        this._refreshTitlePosition();
    },
    _onClickDelete: function () {
        var self = this;
        BI.Msg.confirm("", BI.i18nText("BI-Sure_Delete") + this.model.get("name") + "?", function (v) {
            if (v === true) {
                self.model.destroy();
            }
        });
    },
    _createTools: function () {
        var self = this, wId = this.model.get("id");

        this.refreshChartButton = BI.createWidget({
            type: "bi.icon_button",
            width: this._constants.TOOL_ICON_WIDTH,
            height: this._constants.TOOL_ICON_HEIGHT,
            cls: "recover-chart-font-hightlight dashboard-title-detail",
            title: BI.i18nText("BI-Restore")
        });
        this.refreshChartButton.on(BI.IconButton.EVENT_CHANGE, function () {
            self.tableChart.magnify();
        });

        this.maximize = BI.createWidget({
            type: "bi.maximization",
            wId: wId,
            status: BICst.WIDGET_STATUS.EDIT
        });
        this.maximize.on(BI.Maximization.EVENT_SET, function (widget) {
            self.model.set(widget);
        });
        this.maximize.on(BI.Maximization.EVENT_CHANGE, function (type) {
            switch (type) {
                case BICst.DASHBOARD_WIDGET_EXPAND:
                    self._expandWidget();
                    break;
                case  BICst.DASHBOARD_WIDGET_LINKAGE:
                    self._onClickLinkage();
                    break;
                case BICst.DASHBOARD_WIDGET_SHOW_NAME:
                    // self._onClickShowName();
                    // var settings = self.model.get("settings");
                    // settings.show_name = !BI.Utils.getWSShowNameByID(self.model.get("id"));
                    // self.model.set("settings", settings);
                    // self._refreshLayout();
                    break;
                case BICst.DASHBOARD_WIDGET_RENAME:
                    self.title.focus();
                    break;
                case BICst.DASHBOARD_WIDGET_NAME_POS_LEFT:
                    self._onClickNamePosLeft();
                    break;
                case BICst.DASHBOARD_WIDGET_NAME_POS_CENTER:
                    self._onClickNamePosCenter();
                    break;
                case BICst.DASHBOARD_WIDGET_COPY:
                    self.model.copy();
                    break;
                case BICst.DASHBOARD_WIDGET_DELETE:
                    self._onClickDelete();
                    break;
            }
        });

        var filter = BI.createWidget({
            type: "bi.icon_button",
            width: this._constants.TOOL_ICON_WIDTH,
            height: this._constants.TOOL_ICON_HEIGHT,
            title: BI.i18nText("BI-Show_Filters"),
            cls: "widget-tools-filter-font dashboard-title-detail"
        });
        filter.on(BI.IconButton.EVENT_CHANGE, function () {
            self._onClickFilter();
        });

        var expand = BI.createWidget({
            type: "bi.icon_button",
            width: this._constants.TOOL_ICON_WIDTH,
            height: this._constants.TOOL_ICON_HEIGHT,
            title: BI.i18nText("BI-Detailed_Setting"),
            cls: "widget-combo-detail-font dashboard-title-detail"
        });
        expand.on(BI.IconButton.EVENT_CHANGE, function () {
            self._expandWidget();
        });

        var combo = BI.createWidget({
            type: "bi.widget_combo",
            wId: wId
        });
        combo.on(BI.WidgetCombo.EVENT_CHANGE, function (type) {
            switch (type) {
                case BICst.DASHBOARD_WIDGET_EXPAND:
                    self._expandWidget();
                    break;
                case BICst.DASHBOARD_WIDGET_LINKAGE:
                    self._onClickLinkage();
                    break;
                case BICst.DASHBOARD_WIDGET_SHOW_NAME:
                    var settings = self.model.get("settings");
                    settings.widget_setting.show_name = !BI.Utils.getWSShowNameByID(self.model.get("id"));
                    self.model.set("settings", settings);
                    self._refreshLayout();
                    break;
                case BICst.DASHBOARD_WIDGET_RENAME:
                    self.title.focus();
                    break;
                case BICst.DASHBOARD_WIDGET_NAME_POS_LEFT:
                    self._onClickNamePosLeft();
                    break;
                case BICst.DASHBOARD_WIDGET_NAME_POS_CENTER:
                    self._onClickNamePosCenter();
                    break;
                case BICst.DASHBOARD_WIDGET_FILTER:
                    self._onClickFilter();
                    break;
                case BICst.DASHBOARD_WIDGET_EXCEL:
                    window.open(FR.servletURL + "?op=fr_bi_dezi&cmd=bi_export_excel&sessionID=" + Data.SharingPool.get("sessionID") + "&name="
                        + window.encodeURIComponent(self.model.get("name")));
                    break;
                case BICst.DASHBOARD_WIDGET_COPY:
                    self.model.copy();
                    break;
                case BICst.DASHBOARD_WIDGET_DELETE:
                    self._onClickDelete();
                    break;
            }
        });
        combo.on(BI.WidgetCombo.EVENT_BEFORE_POPUPVIEW, function () {
            self.chartDrill.populate();
        });

        this.tools = BI.createWidget({
            type: "bi.left",
            cls: "operator-region",
            items: [this.refreshChartButton, this.maximize, filter, expand, combo],
            lgap: 10
        });
        this.tools.setVisible(false);
    },

    _onClickFilter: function () {
        var self = this;
        if (BI.isNull(this.filterPane)) {
            this.filterPane = BI.createWidget({
                type: "bi.widget_filter",
                wId: this.model.get("id")
            });
            this.filterPane.on(BI.WidgetFilter.EVENT_REMOVE_FILTER, function (widget) {
                self.model.set(widget);
            });
            BI.createWidget({
                type: "bi.absolute",
                element: this.tableChart,
                items: [{
                    el: this.filterPane,
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: 0
                }]
            });
            return;
        }
        this.filterPane.setVisible(!this.filterPane.isVisible());
    },

    _refreshTableAndFilter: function () {
        BI.isNotNull(this.filterPane) && this.filterPane.populate();
        this.tableChartPopupulate();
        this.chartDrill.populate();
        this.maximize.populate();
    },

    _refreshLayout: function () {
        var showTitle = BI.Utils.getWSShowNameByID(this.model.get("id"));
        if (showTitle === false) {
            this.titleWrapper.setVisible(false);
            this.widget.attr("items")[0].top = 0;
            this.widget.attr("items")[2].top = 20;
        } else {
            this.titleWrapper.setVisible(true);
            this.widget.attr("items")[0].top = 10;
            this.widget.attr("items")[2].top = 35;
        }
        this.widget.resize();
        this.tableChartResize();
    },

    _refreshTitlePosition: function () {
        var pos = BI.Utils.getWSNamePosByID(this.model.get("id"));
        var cls = pos === BICst.DASHBOARD_WIDGET_NAME_POS_CENTER ?
            "dashboard-title-center" : "dashboard-title-left";
        this.title.element.removeClass("dashboard-title-left")
            .removeClass("dashboard-title-center").addClass(cls);
    },

    _refreshWidgetTitle: function () {
        var id = this.model.get("id");
        var titleSetting = this.model.get("settings").title_detail || {};
        var $title = this.title.element.find(".shelter-editor-text .bi-text");
        $title.css(titleSetting.detail_style || {});

        this.titleWrapper.element.css({"background": this._getBackgroundValue(titleSetting.detail_background)});
    },

    _refreshWidgetBG: function () {
        var widgetBG = this.model.get("settings").widget_bg || {};
        this.element.css({"background": this._getBackgroundValue(widgetBG)})
    },

    _getBackgroundValue: function (bg) {
        if (!bg) {
            return "";
        }
        switch (bg.type) {
            case BICst.BACKGROUND_TYPE.COLOR:
                return bg.value;
            case BICst.BACKGROUND_TYPE.IMAGE:
                return "url(" + FR.servletURL + "?op=fr_bi&cmd=get_uploaded_image&image_id=" + bg["value"] + ")";
        }
        return "";
    },

    _refreshGlobalStyle: function () {
        this._refreshTitlePosition();
    },

    _expandWidget: function () {
        var wId = this.model.get("id");
        var type = this.model.get("type");
        this.addSubVessel("detail", "body", {
            isLayer: true
        }).skipTo("detail", "detail", "detail", {}, {
            id: wId
        });
        BI.Broadcasts.send(BICst.BROADCAST.DETAIL_EDIT_PREFIX + wId);
    },

    _refreshMagnifyButton: function () {
        switch (this.model.get("type")) {
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.MAP:
                this.refreshChartButton.setVisible(true);
                break;
            default:
                this.refreshChartButton.setVisible(false);
        }
    },

    listenEnd: function () {

    },

    change: function (changed, prev, context, options) {
        if (options.notrefresh === true) {
            return;
        }
        if (BI.has(changed, "bounds")) {
            this.tableChartResize();
            this.chartDrill.populate();
        }
        if (BI.has(changed, "dimensions") ||
            BI.has(changed, "sort") ||
            BI.has(changed, "linkages")) {
            this._refreshTableAndFilter();
        }
        if (BI.has(changed, "clicked") || BI.has(changed, "filter_value")) {
            this._refreshTableAndFilter();
        }
        if (BI.has(changed, "type")) {
            this._refreshMagnifyButton();
        }
        if (BI.has(changed, "name")) {
            this.title.setValue(this.model.get("name"))
        }
        if (BI.has(changed, "settings") && (changed.settings.title_detail !== prev.settings.title_detail)) {
            this._refreshWidgetTitle()
        }
        if (BI.has(changed, "settings") && (changed.settings.widget_bg !== prev.settings.widget_bg)) {
            this._refreshWidgetBG()
        }
    },

    local: function () {
        if (this.model.has("expand")) {
            this.model.get("expand");
            this._expandWidget();
            return true;
        }
        return false;
    },

    refresh: function () {
        this._buildWidgetTitle();
        this._refreshWidgetTitle();
        this._refreshWidgetBG();
        this._refreshMagnifyButton();
        this._refreshTableAndFilter();
        this._refreshLayout();
        this._refreshTitlePosition();

        this._refreshGlobalStyle();
    }
});
