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
            type: "bi.button_group",
            cls: "drill-wrapper",
            layouts: [{
                type: "bi.left",
                cls: "drill-wrapper",
                hgap: 5,
                vgap: 5
            }]
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

        BI.Broadcasts.on(BICst.BROADCAST.CHART_CLICK_PREFIX + wId, function (obj) {
            var showDrill = self._canChartDrillShow();
            if (showDrill === false) {
                self.pushButton.setPushDown();
            } else {
                self.pushButton.setPushUp();
            }
            self.setVisible(showDrill);
            self.wrapper.setVisible(showDrill);
            var width = 0;
            BI.each(self.wrapper.getAllButtons(), function (idx, drill) {
                //当前点击的要展示
                if (BI.isNotNull(obj.dimensionIds) && BI.contains(obj.dimensionIds, drill.getDid())) {
                    drill.setVisible(true);
                    drill.setValue(obj);
                    drill.populate();
                    width += 190;
                } else {
                    drill.setVisible(false);
                }
            });
            self.wrapper.element.width(width);
            self._doHide = true;
            self._debounce2Hide();
        });

        BI.createWidget({
            type: "bi.horizontal_auto",
            element: this.element,
            items: [{
                el: this.wrapper
            }, {
                el: this.pushButton
            }]
        })
    },

    _hideDrill: function () {
        if (this._doHide && BI.Utils.isWidgetExistByID(this.options.wId) && !this._hasUpDrill()) {
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
            case BICst.WIDGET.RECT_TREE:
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.PARETO:
                if (allDims.length > allUsableDims.length && allUsableDims.length > 0) {
                    showDrill = true;
                }
                break;
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.MULTI_PIE:
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

    _onClickPush: function (isVisible) {
        this.wrapper.setVisible(isVisible);
        isVisible ? this.pushButton.setPushUp() : this.pushButton.setPushDown();
    },

    _hasUpDrill: function () {
        var allUsedDims = BI.Utils.getAllUsableDimDimensionIDs(this.options.wId);
        return BI.any(allUsedDims, function (idx, dId) {
            return BI.isNotNull(BI.Utils.getDrillUpDimensionIdByDimensionId(dId));
        })
    },

    populate: function () {
        var self = this, wId = this.options.wId;
        var wType = BI.Utils.getWidgetTypeByID(wId);
        if (wType === BICst.WIDGET.DETAIL ||
            wType === BICst.WIDGET.MAP) {
            this.setVisible(false);
            return;
        }

        this.setVisible(self._canChartDrillShow() && self._hasUpDrill());
        var currentDrilldIds = [];
        //看一下钻取
        var drillList = BI.Utils.getDrillList(wId);

        //哪些钻取框可以显示
        var visibleArray = [];
        BI.each(BI.Utils.getAllUsableDimDimensionIDs(wId), function (i, dim) {

            var dId = BI.Utils.getDrillUpDimensionIdByDimensionId(dim);
            if (BI.isNotNull(dId)) {
                var arr = drillList[dim];
                currentDrilldIds.push(arr[arr.length - 1]);
                visibleArray.push(arr[arr.length - 1]);
            } else {
                currentDrilldIds.push(dim);
            }
        });

        var width = 0;
        var items = [];
        BI.each(currentDrilldIds, function (idx, dId) {
            var drill = BI.createWidget({
                type: "bi.chart_drill_cell",
                dId: dId,
                invisible: !BI.contains(visibleArray, dId)
            });
            drill.on(BI.ChartDrillCell.EVENT_DRILL_UP, function (v) {
                self.fireEvent(BI.ChartDrill.EVENT_CHANGE, v);
            });
            drill.on(BI.ChartDrillCell.EVENT_DRILL_DOWN, function (v) {
                self.fireEvent(BI.ChartDrill.EVENT_CHANGE, v);
            });
            drill.populate();
            items.push(drill);
            if (BI.contains(visibleArray, dId)) {
                width += 190;
            }
        });
        this.wrapper.populate(items);
        this.wrapper.element.width(width);
        this.wrapper.setVisible(true);

        //如果已经下钻过了
        if (this._hasUpDrill(wId)) {
            BI.each(this.wrapper.getAllButtons(), function (idx, drill) {
                drill.setValue(BI.i18nText("BI-Unchosen"));
                drill.setDrillDownEnabled(false);
            });
            this._onClickPush(false);
            return;
        }

        this._debounce2Hide();
    }
});

BI.extend(BI.ChartDrill, {
    REQ_GET_DATA_LENGTH: 0,
    REQ_GET_ALL_DATA: -1
});

BI.ChartDrill.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_drill", BI.ChartDrill);