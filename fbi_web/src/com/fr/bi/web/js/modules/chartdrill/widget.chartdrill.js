/**
 * Created by Young's on 2016/5/26.
 */
BI.ChartDrill = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.ChartDrill.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-drill"
        })
    },

    _init: function(){
        BI.ChartDrill.superclass._init.apply(this, arguments);
        var self = this, wId = this.options.wId;
        var wType = BI.Utils.getWidgetTypeByID(wId);
        switch (wType) {
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.ACCUMULATE_AXIS:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.LINE:
            case BICst.WIDGET.AREA:
            case BICst.WIDGET.ACCUMULATE_AREA:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.RADAR:
                break;
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.FUNNEL:
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.SCATTER:
                break;

        }

        this.wrapper = BI.createWidget({
            type: "bi.left",
            cls: "drill-wrapper",
            hgap: 5,
            vgap: 5
        });
        var pushButton = BI.createWidget({
            type: "bi."
        });
        this.outerWrapper = BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.wrapper,
                top: 0
            }]
        })
    },

    populate: function(){
        var wId = this.options.wId;
        var dims = BI.Utils.getAllDimDimensionIDs(wId);
        var classification = null, series = null;
        BI.each(dims, function(i, dim){
            if(BI.Utils.isDimensionUsable(dim)) {
                if(BI.Utils.getRegionTypeByDimensionID(dim) === BICst.REGION.DIMENSION1) {
                    classification = dim;
                }
                if(BI.Utils.getRegionTypeByDimensionID(dim) === BICst.REGION.DIMENSION2) {
                    series = dim;
                }
            }
        });
        this.wrapper.empty();
        if(BI.isNotNull(classification)) {
            var cDrill = BI.createWidget({
                type: "bi.chart_drill_cell",
                dId: classification
            });
            this.wrapper.addItem(cDrill);
        }
        if(BI.isNotNull(series)) {
            var sDrill = BI.createWidget({
                type: "bi.chart_drill_cell",
                dId: series
            });
            this.wrapper.addItem(sDrill);
        }
        var bounds = BI.Utils.getWidgetBoundsByID(wId);
        var hgap = 0, w = bounds.width;
        if(w < 400 && w > 200) {
            hgap = Math.ceil((w - 200) / 2);
        } else if(w >= 400) {
            hgap = Math.ceil((w - 400) / 2);
        }
        this.outerWrapper.attr("items")[0].left = hgap;
        this.outerWrapper.attr("items")[0].right = hgap;
        this.outerWrapper.resize();
    }

});
$.shortcut("bi.chart_drill", BI.ChartDrill);