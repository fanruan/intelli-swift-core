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

        this.tools = this._createTools();
        this._buildChartDrill();

        this.tableChart = BI.createWidget({
            type: "bi.table_chart_manager",
            wId: o.wId,
            status: o.status
        });
        this.tableChart.on(BI.TableChartManager.EVENT_CHANGE, function (widget) {
            self.fireEvent(BI.Maximization4ShowChartPane.EVENT_SET, arguments);
        });
        this.tableChart.on(BI.TableChartManager.EVENT_CLICK_CHART, function (obj) {
            self._onClickChart(obj);
        });

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
                el: this.chartDrill,
                left: 0,
                top: 0,
                right: 0
            }, {
                el: this.tools,
                top: 0,
                right: 10
            }]
        })
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
            items: [minimize, filter, excel],
            hgap: 3
        });
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
    },

    populate: function () {
        this._refreshTableAndFilter();
    }
});
BI.Maximization4ShowChartPane.EVENT_SET = "BI.Maximization4ShowChartPane.EVENT_SET";
BI.Maximization4ShowChartPane.EVENT_CHANGE = "BI.Maximization4ShowChartPane.EVENT_CHANGE";
BI.Maximization4ShowChartPane.EVENT_CLOSE = "BI.Maximization4ShowChartPane.EVENT_CLOSE";
$.shortcut("bi.maximization_4show_chart_pane", BI.Maximization4ShowChartPane);