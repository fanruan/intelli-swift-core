BI.TableChartManagerAspect = function () {
    var self = this;
    var assertTip = function () {
        if (!self.tipPane) {
            self.tipPane = BI.createWidget({
                type: "bi.layout"
            });
            BI.createWidget({
                type: "bi.absolute",
                element: self.element,
                items: [{
                    el: self.tipPane, top: 0,
                    bottom: 0,
                    left: 0,
                    right: 0
                }]
            })
        }
    };

    /**
     * 图表不能画出基本逻辑
     * 通用：至少要有分类和指标有一个 条形、柱形图、面积图、折线图、组合图、（饼图以前）
     * 饼图、仪表盘 只要有指标就可以了
     * 气泡、散点 必须每个区域都要有一个
     *
     */
    var _isShowChartPane = function () {
        var o = self.options;
        var wId = o.wId, status = o.status;
        var view = BI.Utils.getWidgetViewByID(wId);
        var cls = true;
        var dim1Size = 0, dim2Size = 0, tar1Size = 0, tar2Size = 0, tar3Size = 0;
        BI.each(view, function (vId, v) {
            switch (vId) {
                case BICst.REGION.DIMENSION1:
                    BI.each(v, function (i, dId) {
                        BI.Utils.isDimensionUsable(dId) && dim1Size++;
                    });
                    break;
                case BICst.REGION.DIMENSION2:
                    BI.each(v, function (i, dId) {
                        BI.Utils.isDimensionUsable(dId) && dim2Size++;
                    });
                    break;
                case BICst.REGION.TARGET1:
                    BI.each(v, function (i, dId) {
                        BI.Utils.isDimensionUsable(dId) && tar1Size++;
                    });
                    break;
                case BICst.REGION.TARGET2:
                    BI.each(v, function (i, dId) {
                        BI.Utils.isDimensionUsable(dId) && tar2Size++;
                    });
                    break;
                case BICst.REGION.TARGET3:
                    BI.each(v, function (i, dId) {
                        BI.Utils.isDimensionUsable(dId) && tar3Size++;
                    });
                    break;
            }
        });
        switch (BI.Utils.getWidgetTypeByID(wId)) {
            case BICst.WIDGET.TABLE:
                (dim1Size + dim2Size + tar1Size + tar2Size + tar3Size) === 0 && (cls = status === BICst.WIDGET_STATUS.EDIT ? "table-group-text-tip-background" : "table-group-tip-background");
                break;
            case BICst.WIDGET.CROSS_TABLE:
                (dim1Size + dim2Size + tar1Size + tar2Size + tar3Size) === 0 && (cls = status === BICst.WIDGET_STATUS.EDIT ? "table-cross-text-tip-background" : "table-cross-tip-background");
                break;
            case BICst.WIDGET.COMPLEX_TABLE:
                (dim1Size + dim2Size + tar1Size + tar2Size + tar3Size) === 0 && (cls = status === BICst.WIDGET_STATUS.EDIT ? "table-complex-text-tip-background" : "table-complex-tip-background");
                break;
            case BICst.WIDGET.BUBBLE:
                !(dim1Size > 0 && tar1Size > 0 && tar2Size > 0 && tar3Size > 0) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "bubble-text-tip-background" : "bubble-tip-background");
                break;
            case BICst.WIDGET.SCATTER:
                !(dim1Size > 0 && tar1Size > 0 && tar2Size > 0) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "scatter-text-tip-background" : "scatter-tip-background");
                break;
            case BICst.WIDGET.AXIS:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "axis-text-tip-background" : "axis-tip-background");
                break;
            case BICst.WIDGET.LINE:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "line-tip-text-background" : "line-tip-background");
                break;
            case BICst.WIDGET.AREA:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "area-tip-text-background" : "area-tip-background");
                break;
            case BICst.WIDGET.ACCUMULATE_AXIS:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "axis-accu-text-tip-background" : "axis-accu-tip-background");
                break;
            case BICst.WIDGET.ACCUMULATE_AREA:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "area-accu-text-tip-background" : "area-accu-tip-background");
                break;
            case BICst.WIDGET.ACCUMULATE_RADAR:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "radar-accu-text-tip-background" : "radar-accu-tip-background");
                break;
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "axis-percent-text-tip-background" : "axis-percent-tip-background");
                break;
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "area-percent-text-tip-background" : "area-percent-tip-background");
                break;
            case BICst.WIDGET.COMPARE_AXIS:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "axis-compare-text-tip-background" : "axis-compare-tip-background");
                break;
            case BICst.WIDGET.COMPARE_AREA:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "area-compare-text-tip-background" : "area-compare-tip-background");
                break;
            case BICst.WIDGET.FALL_AXIS:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "axis-fall-text-tip-background" : "axis-fall-tip-background");
                break;
            case BICst.WIDGET.RANGE_AREA:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "area-range-text-tip-background" : "area-range-tip-background");
                break;
            case BICst.WIDGET.BAR:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "bar-text-tip-background" : "bar-tip-background");
                break;
            case BICst.WIDGET.ACCUMULATE_BAR:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "bar-accu-text-tip-background" : "bar-accu-tip-background");
                break;
            case BICst.WIDGET.COMPARE_BAR:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "bar-compare-text-tip-background" : "bar-compare-tip-background");
                break;
            case BICst.WIDGET.COMBINE_CHART:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "combine-text-tip-background" : "combine-tip-background");
                break;
            case BICst.WIDGET.RADAR:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "radar-text-tip-background" : "radar-tip-background");
                break;
            case BICst.WIDGET.DONUT:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "donut-text-tip-background" : "donut-tip-background");
                break;
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "combine-m-text-tip-background" : "combine-m-tip-background");
                break;
            case BICst.WIDGET.FORCE_BUBBLE:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "bubble-force-text-tip-background" : "bubble-force-tip-background");
                break;
            case BICst.WIDGET.FUNNEL:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "funnel-text-tip-background" : "funnel-tip-background");
                break;
            case BICst.WIDGET.MAP:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "map-text-tip-background" : "map-tip-background");
                break;
            case BICst.WIDGET.GIS_MAP:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "map-gis-text-tip-background" : "map-gis-tip-background");
                break;
            case BICst.WIDGET.PIE:
                tar1Size === 0 && (cls = status === BICst.WIDGET_STATUS.EDIT ? "pie-text-tip-background" : "pie-tip-background");
                break;
            case BICst.WIDGET.DASHBOARD:
                tar1Size === 0 && (cls = status === BICst.WIDGET_STATUS.EDIT ? "dashboard-text-tip-background" : "dashboard-tip-background");
                break;
        }
        return cls;
    };


    BI.aspect.before(this, "populate", function () {
        var cls = _isShowChartPane();
        if (cls !== true) {
            assertTip();
            self.tipPane.setVisible(true);
            self.tipPane.element.addClass(cls);
            return false;
        }
        if (!BI.Utils.isAllFieldsExistByWidgetID(self.options.wId)) {
            assertTip();
            self.tipPane.setVisible(true);
            self.tipPane.element.removeClass().addClass("data-miss-background");
            return false;
        }
        self.tipPane && self.tipPane.setVisible(false);
    })
};