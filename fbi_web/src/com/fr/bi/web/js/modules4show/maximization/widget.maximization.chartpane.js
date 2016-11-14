/**
 * Created by zcf on 2016/10/14.
 */
BI.Maximization4ShowChartPane = BI.inherit(BI.Widget, {
    _constants: {
        TOOL_ICON_WIDTH: 20,
        TOOL_ICON_HEIGHT: 20
    },
    _defaultConfig: function () {
        return BI.extend(BI.Maximization4ShowChartPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-maximization-4show-chart-pane",
            wId: "",
            status: null
        })
    },
    _init: function () {
        BI.Maximization4ShowChartPane.superclass._init.apply(this, arguments);

        var self = this, o = this.options;
        var type = BI.Utils.getWidgetTypeByID(o.wId);

        this.tools = this._createTools();
        this._setRefreshButtonVisible(type);

        this._buildChartDrill();
        this._createTableChart(type);
        this._buildWidgetTitle();

        this.tableChartPopupulate = BI.debounce(BI.bind(this.tableChart.populate, this.tableChart), 0);

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.tableChart,
                left: 10,
                right: 10,
                top: 45,
                bottom: 10
            }, {
                el: this.titleWrapper,
                top: 0,
                left: 0,
                right: 0
            }, {
                el: this.chartDrill,
                left: 0,
                top: 0,
                right: 0
            }, {
                el: this.tools,
                top: 10,
                right: 10
            }]
        })
    },

    _createTableChart: function (type) {
        switch (type) {
            case BICst.WIDGET.DETAIL:
                this._createDetail();
                break;
            default :
                this._createChart();
                break;
        }
    },

    _createDetail: function () {
        var self = this, o = this.options;
        this.tableChart = BI.createWidget({
            type: "bi.detail_table",
            wId: o.wId,
            status: o.status
        });
        this.tableChart.on(BI.DetailTable.EVENT_CHANGE, function (ob) {
            self.fireEvent(BI.Maximization4ShowChartPane.EVENT_SET, ob);
        });
    },

    _createChart: function () {
        var self = this, o = this.options;
        this.tableChart = BI.createWidget({
            type: "bi.table_chart_manager",
            wId: o.wId,
            status: o.status
        });
        this.tableChart.on(BI.TableChartManager.EVENT_CHANGE, function (widget) {
            self.fireEvent(BI.Maximization4ShowChartPane.EVENT_SET, widget);
        });
    },

    _onClickChart: function (obj) {
        this.chartDrill.populate(obj);
    },

    _buildChartDrill: function () {
        var self = this, wId = this.options.wId;
        this.chartDrill = BI.createWidget({
            type: "bi.chart_drill",
            wId: wId
        });
        this.chartDrill.on(BI.ChartDrill.EVENT_CHANGE, function (widget) {
            self.fireEvent(BI.Maximization4ShowChartPane.EVENT_SET, widget);
        });
        this.chartDrill.populate();
    },

    _createToolsButton: function (title, cls) {
        return BI.createWidget({
            type: "bi.icon_button",
            width: this._constants.TOOL_ICON_WIDTH,
            height: this._constants.TOOL_ICON_HEIGHT,
            title: BI.i18nText(title),
            cls: cls + " dashboard-title-detail"
        });
    },
    _createTools: function () {
        var self = this, wId = this.options.wId;

        this.refresh = this._createToolsButton("BI-Restore", "recover-chart-font-hightlight");
        this.refresh.on(BI.IconButton.EVENT_CHANGE, function () {
            self.tableChart.magnify();
        });

        var minimize = this._createToolsButton("BI-minimization", "widget-tools-minimization-font");
        minimize.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.Maximization4ShowChartPane.EVENT_CLOSE);
        });

        var filter = this._createToolsButton("BI-Show_Filters", "widget-tools-filter-font");
        filter.on(BI.IconButton.EVENT_CHANGE, function () {
            self._onClickFilter();
        });

        var excel = this._createToolsButton("BI-Export_As_Excel", "widget-tools-export-excel-font");
        excel.on(BI.IconButton.EVENT_CHANGE, function () {
            window.open(FR.servletURL + "?op=fr_bi_dezi&cmd=bi_export_excel&sessionID=" + Data.SharingPool.get("sessionID") + "&name="
                + window.encodeURIComponent(BI.Utils.getWidgetNameByID(wId)));
        });
        return BI.createWidget({
            type: "bi.left",
            cls: "operator-region",
            items: [this.refresh, minimize, filter, excel],
            lgap: 10
        });
    },

    _buildWidgetTitle: function () {
        var self = this;
        var id = this.options.wId;
        if (!this.title) {
            this.title = BI.createWidget({
                type: "bi.shelter_editor",
                cls: "dashboard-title-left",
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
                // self.fireEvent(BI.MaximizationChartPane.EVENT_SET_TITLE_NAME, this.getValue());
            });
        } else {
            this.title.setValue(BI.Utils.getWidgetNameByID(id));
        }
        this._refreshTitlePosition();
    },

    _refreshTitlePosition: function () {
        var pos = BI.Utils.getWSNamePosByID(this.options.wId);
        var cls = pos === BICst.DASHBOARD_WIDGET_NAME_POS_CENTER ?
            "center" : "left";
        this.title.setTextStyle({
            "textAlign": cls
        });
    },

    _setRefreshButtonVisible: function (type) {
        switch (type) {
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
                this.refresh.setVisible(true);
                break;
            default:
                this.refresh.setVisible(false);
        }
    },

    _onClickFilter: function () {
        var self = this, wId = this.options.wId;
        if (BI.isNull(this.filterPane)) {
            this.filterPane = BI.createWidget({
                type: "bi.widget_filter",
                wId: wId
            });
            BI.createWidget({
                type: "bi.absolute",
                element: this.element,
                items: [{
                    el: this.filterPane,
                    top: 45,
                    left: 10,
                    right: 10,
                    bottom: 10
                }]
            });
            return;
        }
        this.filterPane.setVisible(!this.filterPane.isVisible());
    },
    _setTitleVisible: function () {
        var showTitle = BI.Utils.getWSShowNameByID(this.options.wId);
        this.titleWrapper.setVisible(showTitle);
    },
    _refreshTableAndFilter: function () {
        BI.isNotNull(this.filterPane) && this.filterPane.populate();
        this.tableChartPopupulate();
        this.chartDrill.populate();
    },

    populate: function () {
        this._refreshTableAndFilter();
        this._setTitleVisible();
    }
});
BI.Maximization4ShowChartPane.EVENT_SET = "BI.Maximization4ShowChartPane.EVENT_SET";
BI.Maximization4ShowChartPane.EVENT_CHANGE = "BI.Maximization4ShowChartPane.EVENT_CHANGE";
BI.Maximization4ShowChartPane.EVENT_CLOSE = "BI.Maximization4ShowChartPane.EVENT_CLOSE";
$.shortcut("bi.maximization_4show_chart_pane", BI.Maximization4ShowChartPane);