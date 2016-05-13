/**
 * @class BI.ChartType
 * @extend BI.Widget
 * 选择图表类型组
 */
BI.ChartType = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ChartType.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-type"
        })
    },

    _init: function () {
        BI.ChartType.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.tableCombo = BI.createWidget({
            type: "bi.table_type_combo",
            width: 40,
            height: 34,
            items: [BICst.TABLE_TYPE]
        });
        this.tableCombo.on(BI.AbstractTypeCombo.EVENT_CHANGE, function (v) {
            self.setValue(v);
            self.fireEvent(BI.ChartType.EVENT_CHANGE, arguments);
        });
        this.chartGroup = BI.createWidget({
            type: "bi.button_group",
            scrollable: false,
            items: BI.createItems(BICst.CHART_TYPE, {
                type: "bi.icon_button",
                iconHeight: 25,
                iconWidth: 25,
                width: 40,
                height: 34,
                extraCls: "chart-type-icon"
            }),
            layouts: [{
                type: "bi.horizontal",
                scrollx: false,
                hgap: 3
            }]
        });
        this.chartGroup.on(BI.ButtonGroup.EVENT_CHANGE, function (v) {
            self.setValue(v);
            self.fireEvent(BI.ChartType.EVENT_CHANGE, arguments);
        });
        BI.createWidget({
            type: "bi.horizontal",
            element: this.element,
            scrollx: false,
            scrollable: false,
            items: [this.tableCombo, this.chartGroup],
            vgap: 3,
            hgap: 3
        })
    },

    _createChartItem: function () {
        this.axis = BI.createWidget({
            type: "bi.axis_type_combo",
            width: 40,
            height: 34,
            items: [BICst.AXIS_CHART_TYPE]
        });
        this.bar = BI.createWidget({
            type: "bi.bar_type_combo",
            width: 40,
            height: 34,
            items: [BICst.BAR_CHART_TYPE]
        });
        this.area = BI.createWidget({
            type: "bi.area_type_combo",
            width: 40,
            height: 34,
            items: [BICst.AREA_CHART_TYPE]
        });
        this.radar = BI.createWidget({
            type: "bi.radar_type_combo",
            width: 40,
            height: 34,
            items: [BICst.RADAR_CHART_TYPE]
        });
        this.bubble = BI.createWidget({
            type: "bi.bubble_type_combo",
            width: 40,
            height: 34,
            items: [BICst.BUBBLE_CHART_TYPE]
        });
        this.combine = BI.createWidget({
            type: "bi.combine_chart_type_combo",
            width: 40,
            height: 34,
            items: [BICst.COMBINE_CHART_TYPE]
        });
    },

    getValue: function () {
        if (this.tableCombo.isSelected()) {
            return this.tableCombo.getValue();
        } else {
            return this.chartGroup.getValue()[0];
        }
    },

    setValue: function (v) {
        this.tableCombo.setValue(v);
        this.chartGroup.setValue(v);
    }
});
BI.ChartType.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_type", BI.ChartType);
