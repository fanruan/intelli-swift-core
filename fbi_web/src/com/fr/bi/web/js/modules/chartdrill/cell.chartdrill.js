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
            cls: "chart-drill-up up-drill-button",
            text: BI.i18nText("BI-Drill_up"),
            height: 25
        });
        this.upDrill.on(BI.IconTextItem.EVENT_CHANGE, function(){
            self.fireEvent(BI.ChartDrillCell.EVENT_DRILL_UP); 
        });
        
        this.downDrill = BI.createWidget({
            type: "bi.down_list_combo",
            iconCls: "chart-drill-down ",
            el: {
                type: "bi.icon_text_item",
                cls: "down-drill-button",
                text: BI.i18nText("BI-Drill_down"),
                height: 25
            }
        });
        this.downDrill.on(BI.DownListCombo.EVENT_CHANGE, function(v){
            self.fireEvent(BI.ChartDrillCell.EVENT_DRILL_DOWN, v); 
        });
        this._initStatus();

        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: this.upDrill,
                width: 60
            }, {
                el: {
                    type: "bi.label",
                    text: o.value,
                    title: o.value,
                    cls: "dimension-name",
                    height: 23,
                    hgap: 2
                },
                width: "fill"
            }, {
                el: this.downDrill,
                width: 60
            }],
            width: "100%",
            height: 25
        });
    },

    _initStatus: function(){
        var dId = this.options.dId;
        var widgetId = BI.Utils.getWidgetIDByDimensionID(dId);
        var drillMap = BI.Utils.getDrillByID(widgetId);
        var allDims = BI.Utils.getAllDimDimensionIDs(widgetId);
        var allUsedDims = BI.Utils.getAllUsableDimDimensionIDs(widgetId);
        var drilledIds = [], upDrillName = null;
        BI.each(drillMap, function(drId, ds){
            //存在于钻取中
            if( ds.length > 0 && (dId === drId || ds[ds.length - 1].dId === dId)) {
                if(ds.length > 1) {
                    upDrillName = BI.Utils.getDimensionNameByID(ds[ds.length - 2].dId);
                } else {
                    upDrillName = BI.Utils.getDimensionNameByID(drId);
                }
            }
            BI.each(ds, function(i, drs){
                drilledIds.push(drs.dId);
            });
        });

        var downChildren = [];
        //下钻节点的时候需要去掉那些已下钻的
        BI.each(allDims, function(i, dim) {
            if(!allUsedDims.contains(dim) && !drilledIds.contains(dim)) {
                downChildren.push({
                    text: BI.Utils.getDimensionNameByID(dim),
                    value: dim
                })
            }
        });
        if(BI.isNull(upDrillName)){
            this.upDrill.setEnable(false);
        }
        if(BI.isEmptyArray(downChildren)) {
            this.downDrill.setEnable(false);
        } else {
            this.downDrill.populate([downChildren]);
        }
    }
});
BI.ChartDrillCell.EVENT_DRILL_DOWN = "EVENT_DRILL_DOWN";
BI.ChartDrillCell.EVENT_DRILL_UP = "EVENT_DRILL_UP";
$.shortcut("bi.chart_drill_cell", BI.ChartDrillCell);