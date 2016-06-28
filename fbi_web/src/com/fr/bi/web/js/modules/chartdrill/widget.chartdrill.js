/**
 * Created by Young's on 2016/5/26.
 */
BI.ChartDrill = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ChartDrill.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-drill"
        })
    },

    _init: function () {
        BI.ChartDrill.superclass._init.apply(this, arguments);
        var self = this, wId = this.options.wId;

        this.wrapper = BI.createWidget({
            type: "bi.left",
            cls: "drill-wrapper",
            hgap: 5,
            vgap: 5
        });
        var pushButton = BI.createWidget({
            type: "bi.drill_push_button"
        });
        pushButton.on(BI.DrillPushButton.EVENT_CHANGE, function () {
            self._onClickPush();
        });
        this.outerWrapper = BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.wrapper,
                top: 0
            }, {
                el: pushButton,
                top: 0
            }]
        })
    },

    _initShowChartDrill: function () {
        var wId = this.options.wId;
        this.showDrill = false;
        var wType = BI.Utils.getWidgetTypeByID(wId);
        var allDims = BI.Utils.getAllDimDimensionIDs(wId);
        var allUsableDims = BI.Utils.getAllUsableDimDimensionIDs(wId);
        switch (wType) {
            case BICst.WIDGET.TABLE:
            case BICst.WIDGET.CROSS_TABLE:
            case BICst.WIDGET.COMPLEX_TABLE:
                this.showDrill = false;
                break;
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
                if (allDims.length > allUsableDims.length && allUsableDims.length > 0) {
                    this.showDrill = true;
                }
                break;
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.FUNNEL:
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.SCATTER:
                if (allDims.length > allUsableDims.length && allUsableDims.length > 0) {
                    this.showDrill = true;
                }
                break;

        }
    },

    populate: function (obj) {
        var self = this;
        this._initShowChartDrill();
        this.outerWrapper.setVisible(this.showDrill && BI.isNotNull(obj));
        if (this.showDrill === false || BI.isNull(obj)) {
            return;
        }
        var wId = this.options.wId;
        var dims = BI.Utils.getAllUsableDimDimensionIDs(wId);
        var classification = null, series = null;
        BI.each(dims, function (i, dim) {
            if (BI.Utils.getRegionTypeByDimensionID(dim) === BICst.REGION.DIMENSION1) {
                classification = dim;
            }
            if (BI.Utils.getRegionTypeByDimensionID(dim) === BICst.REGION.DIMENSION2) {
                series = dim;
            }
        });

        //看一下钻取
        var drillMap = BI.Utils.getDrillByID(wId);
        BI.each(drillMap, function (drId, ds) {
            var rType = BI.Utils.getRegionTypeByDimensionID(drId);
            if (rType === BICst.REGION.DIMENSION1 && ds.length > 0) {
                classification = ds[ds.length - 1].dId;
            }
            if (rType === BICst.REGION.DIMENSION2 && ds.length > 0) {
                series = ds[ds.length - 1].dId;
            }
        });

        this.wrapper.empty();
        if (BI.isNotNull(classification)) {
            var cDrill = BI.createWidget({
                type: "bi.chart_drill_cell",
                dId: classification,
                value: obj.x
            });
            cDrill.on(BI.ChartDrillCell.EVENT_DRILL_UP, function () {
                self._onClickDrill(classification, obj.x);
            });
            cDrill.on(BI.ChartDrillCell.EVENT_DRILL_DOWN, function (drillId) {
                self._onClickDrill(classification, obj.x, drillId);
            });
            this.wrapper.addItem(cDrill);
        }
        if (BI.isNotNull(series)) {
            var sDrill = BI.createWidget({
                type: "bi.chart_drill_cell",
                dId: series,
                value: obj.seriesName
            });
            sDrill.on(BI.ChartDrillCell.EVENT_DRILL_UP, function () {
                self._onClickDrill(series, obj.seriesName);
            });
            sDrill.on(BI.ChartDrillCell.EVENT_DRILL_DOWN, function (drillId) {
                self._onClickDrill(series, obj.seriesName, drillId);
            });
            this.wrapper.addItem(sDrill);
        }
        var bounds = BI.Utils.getWidgetBoundsByID(wId);
        var hgap = 0, w = bounds.width;
        this.buttonTop = 35;
        //单个
        if (BI.isNull(classification) || BI.isNull(series)) {
            if (w > 200) {
                hgap = Math.ceil((w - 200) / 2);
            }
        } else {
            if (w < 400 && w > 200) {
                hgap = Math.ceil((w - 200) / 2);
                this.buttonTop = 70;
            } else if (w >= 400) {
                hgap = Math.ceil((w - 400) / 2);
            } else if(w <= 200) {
                this.buttonTop = 70;
            }
        }
        this.wrapper.setVisible(true);
        this.outerWrapper.attr("items")[0].left = hgap;
        this.outerWrapper.attr("items")[0].right = hgap;
        this.outerWrapper.attr("items")[1].left = Math.ceil((w - 60) / 2);
        this.outerWrapper.attr("items")[1].top = this.buttonTop;
        this.outerWrapper.resize();
    },

    _onClickPush: function () {
        var isVisible = !this.wrapper.isVisible();
        this.wrapper.setVisible(isVisible);
        this.outerWrapper.attr("items")[1].top = isVisible ? this.buttonTop : 0;
        this.outerWrapper.resize();
    },

    _onClickDrill: function (dId, value, drillId) {
        var wId = this.options.wId;
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
        this.fireEvent(BI.ChartDrill.EVENT_CHANGE, {clicked: BI.extend(BI.Utils.getLinkageValuesByID(wId), drillMap)});
    }
});
BI.ChartDrill.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_drill", BI.ChartDrill);