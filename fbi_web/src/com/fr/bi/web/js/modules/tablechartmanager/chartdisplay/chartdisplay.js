/**
 * 图表控件
 * @class BI.ChartDisplay
 * @extends BI.Pane
 */
BI.ChartDisplay = BI.inherit(BI.Pane, {

    constants: {
        SCATTER_REGION_COUNT: 3,
        BUBBLE_REGION_COUNT: 4
    },

    _defaultConfig: function () {
        return BI.extend(BI.ChartDisplay.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-display",
            overlap: false,
            wId: ""
        })
    },

    _init: function () {
        BI.ChartDisplay.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.vanChars = VanCharts.init(this.element[0]);
    },

    populate: function () {
        var widget = this, o = widget.options;

        widget.loading();

        BI.Utils.getWidgetDataByID(o.wId, {
            success: function (options) {

                widget.loaded();

                widget.vanChars.setOptions(options);

            }
        });
    },

    _assertValue: function (v) {
        if (BI.isNull(v)) {
            return;
        }
        if (!BI.isFinite(v)) {
            return 0;
        }
        return v;
    },

    resize: function () {
        this.vanChars.resize();
    },

    magnify: function () {

    }
});

BI.ChartDisplay.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.chart_display', BI.ChartDisplay);
