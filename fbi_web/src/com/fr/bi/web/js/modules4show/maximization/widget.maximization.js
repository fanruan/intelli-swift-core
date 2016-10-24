/**
 * Created by zcf on 2016/10/14.
 */
BI.Maximization4Show = BI.inherit(BI.Widget, {
    _constants: {
        TOOL_ICON_WIDTH: 20,
        TOOL_ICON_HEIGHT: 20
    },
    _defaultConfig: function () {
        return BI.extend(BI.Maximization4Show.superclass._defaultConfig.apply(this.arguments), {
            baseCls: "bi-maximization-4show",
            wId: "",
            status: null
        })
    },
    _init: function () {
        BI.Maximization4Show.superclass._init.apply(this, arguments);

        var self = this;

        var maximization = BI.createWidget({
            type: "bi.icon_button",
            element: this.element,
            width: this._constants.TOOL_ICON_WIDTH,
            height: this._constants.TOOL_ICON_HEIGHT,
            title: BI.i18nText("BI-Maximization4Show"),
            cls: "widget-tools-maximization-font dashboard-title-detail"
        });
        maximization.on(BI.IconButton.EVENT_CHANGE, function () {
            self._createMaximization4ShowPane();
        })
    },
    _createMaximization4ShowPane: function () {
        var self = this, o = this.options;

        var backgroundLayer = BI.Layers.create(BICst.MAXIMIZATION_BACKGROUND_LAYER, null, {
            render: {
                type: "bi.layout",
                cls: "bi-overlay"
            }
        });
        BI.Layers.show(BICst.MAXIMIZATION_BACKGROUND_LAYER);

        var layer = BI.Layers.create(BICst.MAXIMIZATION_LAYER);
        BI.Layers.show(BICst.MAXIMIZATION_LAYER);

        this.chartPane = BI.createWidget({
            type: "bi.maximization_4show_chart_pane",
            wId: o.wId,
            status: o.status
        });
        this.chartPane.populate();
        this.chartPane.on(BI.Maximization4ShowChartPane.EVENT_CHANGE, function (type) {
            self.fireEvent(BI.Maximization4Show.EVENT_CHANGE, type);
        });
        this.chartPane.on(BI.Maximization4ShowChartPane.EVENT_SET, function (widget) {
            self.fireEvent(BI.Maximization4Show.EVENT_SET, widget);
        });
        this.chartPane.on(BI.Maximization4ShowChartPane.EVENT_CLOSE, function () {
            BI.Layers.remove(BICst.MAXIMIZATION_LAYER);
            BI.Layers.remove(BICst.MAXIMIZATION_BACKGROUND_LAYER);
        });

        BI.createWidget({
            type: "bi.absolute",
            element: layer,
            items: [{
                el: this.chartPane,
                left: 150,
                right: 150,
                top: 50,
                bottom: 50
            }]
        })
    },

    populate: function () {
        if (BI.isNotNull(this.chartPane)) {
            this.chartPane.populate();
        }
    }
});
BI.Maximization4Show.EVENT_SET = "BI.Maximization4Show.EVENT_SET";
BI.Maximization4Show.EVENT_CHANGE = "BI.Maximization4Show.EVENT_CHANGE";
$.shortcut("bi.maximization_4show", BI.Maximization4Show);