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



    _onClickPush: function (isVisible) {
        this.wrapper.setVisible(isVisible);
        isVisible ? this.pushButton.setPushUp() : this.pushButton.setPushDown();
    },

    populate: function () {
        var self = this, wId = this.options.wId;

        this.pushButton.setPushUp();

        var currentDrilldIds = [];
        //看一下钻取
        var drillList = BI.Utils.getDrillList(wId);
        BI.each(BI.Utils.getAllUsableDimDimensionIDs(wId), function (i, dim) {
            if(BI.has(drillList, dim)){
                currentDrilldIds.push(drillList[drillList.length - 1]);
            }else{
                currentDrilldIds.push(dim);
            }
        });
        var width = 0;
        this.wrapper.empty();
        BI.each(currentDrilldIds, function(idx, dId){
            var drill = BI.createWidget({
                type: "bi.chart_drill_cell",
                dId: dId
            });
            drill.on(BI.ChartDrillCell.EVENT_DRILL, function (v) {
                self.fireEvent(BI.ChartDrill.EVENT_CHANGE, v);
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
