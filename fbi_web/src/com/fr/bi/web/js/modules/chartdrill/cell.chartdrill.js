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
            cls: "close-font",
            text: BI.i18nText("BI-Up_Drill")
        });
        this.downDrill = BI.createWidget({
            type: "bi.icon_text_item",
            cls: "close-font",
            text: BI.i18nText("BI-Down_Drill")
        });

        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: this.upDrill,
                width: 50
            }, {
                el: {
                    type: "bi.label",
                    text: BI.Utils.getDimensionNameByID(o.dId),
                    cls: "drill-dimension-name"
                },
                width: "fill"
            }, {
                el: this.downDrill,
                width: 50
            }]
        });
    }
});
$.shortcut("bi.chart_drill_cell", BI.ChartDrillCell);