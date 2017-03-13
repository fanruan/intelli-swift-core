/**
 * 图表控件
 * @class BI.ChartDisplay
 * @extends BI.Pane
 */
BI.ChartDisplay = BI.inherit(BI.Pane, {

    constants: {
        SCATTER_REGION_COUNT: 3,
        BUBBLE_REGION_COUNT: 4
    },

    _defaultConfig: function () {
        return BI.extend(BI.ChartDisplay.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-display",
            overlap: false,
            wId: ""
        })
    },

    _init: function () {
        BI.ChartDisplay.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.vanChars = VanCharts.init(this.element[0]);
    },

    populate: function () {
        var widget = this, o = widget.options;

        widget.loading();

        BI.Utils.getWidgetDataByID(o.wId, {
            success: function (options) {

<<<<<<< HEAD
                widget.loaded();

                widget.vanChars.setOptions(options);
=======
    _onClickDrill: function (dId, value, drillId) {
        var wId = this.options.wId;
        var drillMap = BI.Utils.getDrillByID(wId);
        if (BI.isNull(dId)) {
            this.fireEvent(BI.ChartDisplay.EVENT_CHANGE, {clicked: BI.extend(BI.Utils.getLinkageValuesByID(wId), {})});
            return;
        }
        //value 存当前的过滤条件——因为每一次钻取都要带上所有父节点的值
        //当前钻取的根节点
        var rootId = dId;
        BI.each(drillMap, function (drId, ds) {
            if (dId === drId || BI.find(ds, function(idx, obj){
                    return obj.dId === dId
                })) {
                rootId = drId;
            }
        });

        var drillOperators = drillMap[rootId] || [];
        //上钻
        if (BI.isNull(drillId)) {
            if (drillOperators.length !== 0) {
                var index = drillOperators.length - 1;
                var val = drillOperators[index].values[0].dId;
                while (val !== dId) {
                    if (index < 0) {
                        break;
                    }
                    var obj = drillOperators[index--];
                    val = obj.values[0].dId;
                }
                while (index > -1 && index <= drillOperators.length - 1){
                    drillOperators.pop();
                }
                if(index === -1 && val === dId){
                    drillOperators = [];
                }
            }
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
        this.fireEvent(BI.ChartDisplay.EVENT_CHANGE, {clicked: BI.extend(BI.Utils.getLinkageValuesByID(wId), drillMap)});
    },
>>>>>>> 67b55d486e769f445942f15883303ca839ffd092

            }
        });
    },

    _assertValue: function (v) {
        if (BI.isNull(v)) {
            return;
        }
        if (!BI.isFinite(v)) {
            return 0;
        }
        return v;
    },

    resize: function () {
        this.vanChars.resize();
    },

    magnify: function () {

    }
});

BI.ChartDisplay.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.chart_display', BI.ChartDisplay);
