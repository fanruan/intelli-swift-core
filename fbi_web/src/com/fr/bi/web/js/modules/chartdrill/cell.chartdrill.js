/**
 * Created by Young's on 2016/5/26.
 */
BI.ChartDrillCell = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ChartDrillCell.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-drill-cell",
            disableDownButton: false,
            width: 180,
            height: 25
        })
    },

    _init: function () {
        BI.ChartDrillCell.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var dId = o.dId, text = o.value;
        //日期需要format
        if (BI.Utils.getFieldTypeByDimensionID(dId) === BICst.COLUMN.DATE &&
            BI.Utils.getDimensionGroupByID(dId).type === BICst.GROUP.YMD) {
            text = this._formatDate(text);
        }

        this.upDrill = BI.createWidget({
            type: "bi.icon_text_item",
            cls: "chart-drill-up up-drill-button",
            text: BI.i18nText("BI-Drill_up"),
            height: 25
        });
        this.upDrill.on(BI.IconTextItem.EVENT_CHANGE, function () {
            self.fireEvent(BI.ChartDrillCell.EVENT_DRILL, self._onClickDrill(o.dId, o.value));
        });

        this.downTrigger = BI.createWidget({
            type: "bi.icon_text_item",
            cls: "down-drill-button chart-drill-down",
            text: BI.i18nText("BI-Drill_down"),
            height: 25
        });
        this.downDrill = BI.createWidget({
            type: "bi.down_list_combo",
            el: this.downTrigger
        });
        this.downDrill.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
            self.fireEvent(BI.ChartDrillCell.EVENT_DRILL, self._onClickDrill(o.dId, o.value, v));
        });

        this.label = BI.createWidget({
            type: "bi.label",
            text: text,
            title: text,
            cls: "dimension-name",
            height: 23,
            hgap: 2
        });
        this._initStatus();


        BI.createWidget({
            type: "bi.htape",
            element: this.element,
            items: [{
                el: this.upDrill,
                width: 60
            }, {
                el: this.label,
                width: "fill"
            }, {
                el: this.downDrill,
                width: 60
            }]
        });
    },

    _onClickDrill: function (dId, value, drillId) {
        var wId = BI.Utils.getWidgetIDByDimensionID(this.options.dId);
        var drillMap = BI.Utils.getDrillByID(wId);
        //value 存当前的过滤条件——因为每一次钻取都要带上所有父节点的值
        //当前钻取的根节点
        var rootId = dId;
        BI.each(drillMap, function (drId, ds) {
            if (dId === drId || (ds.length > 0 && ds[ds.length - 1].dId === dId)) {
                rootId = drId;
            }
        });

        var drillOperators = drillMap[rootId] || [];
        //上钻
        if (BI.isNull(drillId)) {
            drillOperators.pop();
        } else {
            drillOperators.push({
                dId: drillId,
                values: [{
                    dId: dId,
                    value: [value]
                }]
            });
        }
        drillMap[rootId] = drillOperators;
        return {clicked: BI.extend(BI.Utils.getLinkageValuesByID(wId), drillMap)};
    },

    _formatDate: function (d) {
        if (BI.isNull(d) || !BI.isNumeric(d)) {
            return d || "";
        }
        var date = new Date(BI.parseInt(d));
        return date.print("%Y-%X-%d")
    },

    _checkUPDrillEmpty: function () {
        var wId = BI.Utils.getWidgetIDByDimensionID(this.options.wId);
        var wType = BI.Utils.getWidgetTypeByID(wId);
        if (wType < 5 || wType === BICst.WIDGET.MAP) {
            return false
        }
        var drillMap = BI.Utils.getDrillByID(wId);
        var upDrillID = null, dId = null;
        BI.each(drillMap, function (drId, ds) {
            var rType = BI.Utils.getRegionTypeByDimensionID(drId);
            if (rType === BICst.REGION.DIMENSION1 && ds.length > 0) {
                dId = ds[ds.length - 1].dId;
            }
            if (rType === BICst.REGION.DIMENSION2 && ds.length > 0) {
                dId = ds[ds.length - 1].dId;
            }
            if (ds.length > 0 && (dId === drId || ds[ds.length - 1].dId === dId)) {
                if (ds.length > 1) {
                    upDrillID = ds[ds.length - 2].dId
                } else {
                    upDrillID = drId;
                }
            }
        });
        return BI.isNull(upDrillID)
    },

    _initStatus: function () {
        var dId = this.options.dId;
        var widgetId = BI.Utils.getWidgetIDByDimensionID(dId);
        var drillMap = BI.Utils.getDrillByID(widgetId);
        var allDims = BI.Utils.getAllDimDimensionIDs(widgetId);
        var allUsedDims = BI.Utils.getAllUsableDimDimensionIDs(widgetId);
        var drilledIds = [], upDrillName = null;
        BI.each(drillMap, function (drId, ds) {
            //存在于钻取中
            if (ds.length > 0 && (dId === drId || ds[ds.length - 1].dId === dId)) {
                if (ds.length > 1) {
                    upDrillName = BI.Utils.getDimensionNameByID(ds[ds.length - 2].dId);
                } else {
                    upDrillName = BI.Utils.getDimensionNameByID(drId);
                }
            }
            BI.each(ds, function (i, drs) {
                drilledIds.push(drs.dId);
            });
        });

        var downChildren = [];
        //下钻节点的时候需要去掉那些已下钻的
        BI.each(allDims, function (i, dim) {
            if (!allUsedDims.contains(dim) && !drilledIds.contains(dim)) {
                downChildren.push({
                    text: BI.Utils.getDimensionNameByID(dim),
                    value: dim
                })
            }
        });
        if (BI.isNull(upDrillName)) {
            this.upDrill.setEnable(false);
        }
        if (BI.isEmptyArray(downChildren) || this.options.disableDownButton) {
            this.downDrill.setEnable(false);
            this.downTrigger.setEnable(false);
        } else {
            this.downDrill.populate([downChildren]);
        }
    },

    setValue: function(value){
        this.label.setValue(value);
    },

    populate: function(){
        this.downDrill.populate();
    }
});
BI.ChartDrillCell.EVENT_DRILL = "EVENT_DRILL";
$.shortcut("bi.chart_drill_cell", BI.ChartDrillCell);
