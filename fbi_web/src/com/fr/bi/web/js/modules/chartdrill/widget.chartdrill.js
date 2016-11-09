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
        this.wrapper.element.hover(function () {
            self._doHide = false;
        }, function () {
            self._doHide = true;
            self._debounce2Hide();
        });
        this.pushButton = BI.createWidget({
            type: "bi.drill_push_button",
        });
        this.pushButton.on(BI.DrillPushButton.EVENT_CHANGE, function () {
            self._onClickPush(!self.wrapper.isVisible());
        });

        this._doHide = true;
        this._debounce2Hide = BI.debounce(BI.bind(this._hideDrill, this), 3000);

        BI.Broadcasts.on(BICst.BROADCAST.CHART_CLICK_PREFIX + wId, function(obj){
            self.populate();
        });

        BI.createWidget({
            type: "bi.horizontal_auto",
            element: this.element,
            items: [{
                el: this.wrapper
            }, {
                el: this.pushButton,
            }]
        })
    },

    _hideDrill: function () {
        if (this._doHide && this._checkUPDrillEmpty(this.options.wId)) {
            this.setVisible(false);
        }
    },

    _canChartDrillShow: function () {
        var wId = this.options.wId;
        var wType = BI.Utils.getWidgetTypeByID(wId);
        var allDims = BI.Utils.getAllDimDimensionIDs(wId);
        var allUsableDims = BI.Utils.getAllUsableDimDimensionIDs(wId);
        var showDrill = false;
        switch (wType) {
            case BICst.WIDGET.TABLE:
            case BICst.WIDGET.CROSS_TABLE:
            case BICst.WIDGET.COMPLEX_TABLE:
                showDrill = false;
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
            case BICst.WIDGET.ACCUMULATE_RADAR:
            case BICst.WIDGET.DASHBOARD:
                if (allDims.length > allUsableDims.length && allUsableDims.length > 0) {
                    showDrill = true;
                }
                break;
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.FUNNEL:
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.FORCE_BUBBLE:
            case BICst.WIDGET.SCATTER:
                if (allDims.length > allUsableDims.length && allUsableDims.length > 0) {
                    showDrill = true;
                }
                break;

        }
        return showDrill;
    },

    _checkUPDrillEmpty: function () {
        var wId = this.options.wId;
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

    _onClickPush: function (isVisible) {
        this.wrapper.setVisible(isVisible);
        isVisible ? this.pushButton.setPushUp() : this.pushButton.setPushDown();
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
    },

    populate: function (obj) {
        var self = this, wId = this.options.wId;

        this.pushButton.setPushUp();
        this.setVisible(true);

        var classification = null;
        var series = null;
        var currentDrilldIds = [];
        //看一下钻取
        var currentDrilldIds = BI.pluck(BI.Utils.getCurrentDrillInfo(wId), "dId");
        BI.each(BI.Utils.getAllUsableDimDimensionIDs(wId), function (i, dim) {
            currentDrilldIds.pushDistinct(dim);
        });
        var width = 0;
        this.wrapper.empty();
        BI.each(currentDrilldIds, function(idx, dId){
            var drill = BI.createWidget({
                type: "bi.chart_drill_cell",
                dId: dId,
                value: ""
            });
            drill.on(BI.ChartDrillCell.EVENT_DRILL_UP, function () {
                //self._onClickDrill(classification);
            });
            drill.on(BI.ChartDrillCell.EVENT_DRILL_DOWN, function (drillId) {
                //self._onClickDrill(classification, drillId);
            });
            self.wrapper.addItem(drill);
            width += 190;
        });
        this.wrapper.element.width(width);
        this.wrapper.setVisible(true);
        this._debounce2Hide();
    }
});
BI.ChartDrill.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_drill", BI.ChartDrill);
