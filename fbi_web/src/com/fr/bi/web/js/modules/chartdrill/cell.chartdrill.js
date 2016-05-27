/**
 * Created by Young's on 2016/5/26.
 */
BI.ChartDrillCell = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.ChartDrillCell.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-drill-cell"
        })
    },

    _init: function(){
        BI.ChartDrillCell.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.upDrill = BI.createWidget({
            type: "bi.icon_text_item",
            cls: "close-font up-drill-button",
            text: BI.i18nText("BI-Drill_up"),
            height: 25
        });
        this.downDrill = BI.createWidget({
            type: "bi.icon_text_item",
            cls: "close-font down-drill-button",
            text: BI.i18nText("BI-Drill_down"),
            height: 25
        });

        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: this.upDrill,
                width: 60
            }, {
                el: {
                    type: "bi.label",
                    text: BI.Utils.getDimensionNameByID(o.dId),
                    cls: "dimension-name",
                    height: 23
                },
                width: "fill"
            }, {
                el: this.downDrill,
                width: 60
            }],
            width: "100%",
            height: 25
        });
    }
});
$.shortcut("bi.chart_drill_cell", BI.ChartDrillCell);