BI.TableChartManagerAspect = function () {
    var self = this;
    var assertTip = function () {
        if (!self.tipPane) {
            self.tipPane = BI.createWidget({
                type: "bi.layout",
                height: 100
            });
            self.textLabel = BI.createWidget({
                type: "bi.label",
                text: BI.i18nText("BI-Empty_Widget_Tip"),
                cls: "empty-widget-tip",
                height: 30
            });
            self.contactAdmin = BI.createWidget({
                type: "bi.label",
                text: BI.i18nText("BI-Please_Contact_Admin"),
                cls: "contact-admin-tip",
                height: 30
            });
            self.mainPane = BI.createWidget({
                type: "bi.center_adapt",
                cls: "widget-tip-pane",
                items: [{
                    type: "bi.vertical",
                    items: [self.tipPane, self.textLabel, self.contactAdmin]
                }]
            });
            BI.createWidget({
                type: "bi.absolute",
                element: self.element,
                scrollable: false,
                items: [{
                    el: self.mainPane,
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: 0
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
            if (BI.parseInt(vId) < BI.parseInt(BICst.REGION.DIMENSION2)) {
                BI.each(v, function (i, dId) {
                    BI.Utils.isDimensionUsable(dId) && BI.Utils.isDimensionValidByDimensionID(dId) && dim1Size++;
                });
            } else if (BI.parseInt(BICst.REGION.DIMENSION2) <= BI.parseInt(vId) &&
                BI.parseInt(vId) < BI.parseInt(BICst.REGION.TARGET1)) {
                BI.each(v, function (i, dId) {
                    BI.Utils.isDimensionUsable(dId) && dim2Size++;
                });
            } else if (vId === BICst.REGION.TARGET1) {
                BI.each(v, function (i, dId) {
                    BI.Utils.isDimensionUsable(dId) && tar1Size++;
                });
            } else if (vId === BICst.REGION.TARGET2) {
                BI.each(v, function (i, dId) {
                    BI.Utils.isDimensionUsable(dId) && tar2Size++;
                });
            } else if (vId === BICst.REGION.TARGET3) {
                BI.each(v, function (i, dId) {
                    BI.Utils.isDimensionUsable(dId) && tar3Size++;
                });
            }
        });
        switch (BI.Utils.getWidgetTypeByID(wId)) {
            case BICst.WIDGET.TABLE:
                (dim1Size + dim2Size + tar1Size + tar2Size + tar3Size) === 0 && (cls = "table-group-tip-background");
                break;
            case BICst.WIDGET.CROSS_TABLE:
                (dim1Size + dim2Size + tar1Size + tar2Size + tar3Size) === 0 && (cls = "table-cross-tip-background");
                break;
            case BICst.WIDGET.COMPLEX_TABLE:
                (dim1Size + dim2Size + tar1Size + tar2Size + tar3Size) === 0 && (cls = "table-complex-tip-background");
                break;
            case BICst.WIDGET.BUBBLE:
                !(dim1Size > 0 && tar1Size > 0 && tar2Size > 0 && tar3Size > 0) && (cls = "bubble-tip-background");
                break;
            case BICst.WIDGET.SCATTER:
                !(dim1Size > 0 && tar1Size > 0 && tar2Size > 0) && (cls = "scatter-tip-background");
                break;
            case BICst.WIDGET.AXIS:
                !((tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "axis-tip-background");
                break;
            case BICst.WIDGET.LINE:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "line-tip-background");
                break;
            case BICst.WIDGET.AREA:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "area-tip-background");
                break;
            case BICst.WIDGET.ACCUMULATE_AXIS:
                !((tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "axis-accu-tip-background");
                break;
            case BICst.WIDGET.ACCUMULATE_AREA:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "area-accu-tip-background");
                break;
            case BICst.WIDGET.ACCUMULATE_RADAR:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "radar-accu-tip-background");
                break;
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
                !((tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "axis-percent-tip-background");
                break;
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "area-percent-tip-background");
                break;
            case BICst.WIDGET.COMPARE_AXIS:
                !((tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "axis-compare-tip-background");
                break;
            case BICst.WIDGET.COMPARE_AREA:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "area-compare-tip-background");
                break;
            case BICst.WIDGET.FALL_AXIS:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "axis-fall-tip-background");
                break;
            case BICst.WIDGET.RANGE_AREA:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "area-range-tip-background");
                break;
            case BICst.WIDGET.BAR:
                !((tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "bar-tip-background");
                break;
            case BICst.WIDGET.ACCUMULATE_BAR:
                !((tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "bar-accu-tip-background");
                break;
            case BICst.WIDGET.COMPARE_BAR:
                !((tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "bar-compare-tip-background");
                break;
            case BICst.WIDGET.COMBINE_CHART:
                !((tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "combine-tip-background");
                break;
            case BICst.WIDGET.RADAR:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "radar-tip-background");
                break;
            case BICst.WIDGET.DONUT:
                !((dim1Size > 0|| dim2Size > 0) && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "donut-tip-background");
                break;
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                !((tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "combine-m-tip-background");
                break;
            case BICst.WIDGET.FORCE_BUBBLE:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "bubble-force-tip-background");
                break;
            case BICst.WIDGET.FUNNEL:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "funnel-tip-background");
                break;
            case BICst.WIDGET.MAP:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "map-tip-background");
                break;
            case BICst.WIDGET.GIS_MAP:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = "map-gis-tip-background");
                break;
            case BICst.WIDGET.PIE:
                tar1Size === 0 && (cls = "pie-tip-background");
                break;
            case BICst.WIDGET.DASHBOARD:
                tar1Size === 0 && (cls = "dashboard-tip-background");
                break;
        }
        return cls;
    };


    BI.aspect.before(this, "populate", function () {
        var cls = _isShowChartPane();
        if (cls !== true) {
            assertTip();
            self.mainPane.setVisible(true);
            self.textLabel.setText(BI.i18nText("BI-Empty_Widget_Tip"));
            self.contactAdmin.setVisible(false);
            self.tipPane.element.removeClass().addClass(cls);
            self.textLabel.setVisible(self.options.status === BICst.WIDGET_STATUS.EDIT);
            return false;
        }
        if (!BI.Utils.isAllFieldsExistByWidgetID(self.options.wId)) {
            assertTip();
            self.mainPane.setVisible(true);
            self.textLabel.setVisible(true);
            self.textLabel.setText(BI.i18nText("BI-Data_Miss_Tip"));
            self.contactAdmin.setVisible(true);
            self.tipPane.element.removeClass().addClass("data-miss-background");
            return false;
        }
        self.mainPane && self.mainPane.setVisible(false);
    })
};