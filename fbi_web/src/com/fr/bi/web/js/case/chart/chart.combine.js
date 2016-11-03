/**
 * 图表控件
 * @class BI.CombineChart
 * @extends BI.Widget
 */
BI.CombineChart = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CombineChart.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-combine-chart",
            items: [],
            xAxis: [{type: "category"}],
            yAxis: [{type: "value"}],
            types: [[], []],
            formatConfig: function (config) {
                return config;
            }
        })
    },

    _init: function () {
        BI.CombineChart.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        //图可配置属性
        this.CombineChart = BI.createWidget({
            type: "bi.chart",
            element: this.element
        });
        this.CombineChart.on(BI.Chart.EVENT_CHANGE, function (obj) {
            self.fireEvent(BI.CombineChart.EVENT_CHANGE, obj);
        });

        if (BI.isNotEmptyArray(o.items)) {
            this.populate(o.items);
        }
    },

    _formatItems: function (items) {
        var result = [], self = this, o = this.options;
        var yAxisIndex = 0;
        BI.each(items, function (i, belongAxisItems) {
            var combineItems = BI.ChartCombineFormatItemFactory.combineItems(o.types[i], belongAxisItems);
            BI.each(combineItems, function (j, axisItems) {
                if (BI.isArray(axisItems)) {
                    result = BI.concat(result, axisItems);
                } else {
                    result.push(BI.extend(axisItems, {"yAxis": yAxisIndex}));
                }
            });
            if (BI.isNotEmptyArray(combineItems)) {
                yAxisIndex++;
            }
        });
        var config = BI.ChartCombineFormatItemFactory.combineConfig();
        config.plotOptions.click = function () {
            var data = this.options;
            data.toolTipRect = this.getTooltipRect();
            data.getPopupItems = BI.bind(self.getPopupItems, self);
            self.fireEvent(BI.CombineChart.EVENT_CHANGE, data);
        };
        return [result, config];
    },

    getPopupItems: function (obj) {
        var self = this, position = obj.position, clicked = obj.clicked, linkages = [];
        BI.each(obj.dId, function (idx, dId) {
            if (BI.Utils.getDimensionTypeByID(dId) === BICst.TARGET_TYPE.FORMULA) {
                var expression = BI.Utils.getDimensionSrcByID(dId).expression;
                if (!expression) {
                    return;
                }
                BI.each(BI.Utils.getWidgetLinkageByID(obj.wId), function (i, link) {
                    if (dId === link.cids[0]) {
                        var name = BI.i18nText("BI-An");
                        BI.each(link.cids, function (idx, cId) {
                            name += BI.Utils.getDimensionNameByID(cId) + "-";
                        });
                        name += BI.Utils.getDimensionNameByID(link.from) + BI.i18nText("BI-Link");
                        linkages.push({
                            text: name,
                            title: name,
                            to: link.to,
                            from: link.from
                        });
                    }
                });
            } else {
                BI.each(BI.Utils.getWidgetLinkageByID(obj.wId), function (i, link) {
                    if (dId === link.from && BI.isEmptyArray(link.cids)) {
                        var name = BI.i18nText("BI-An") + BI.Utils.getDimensionNameByID(link.from) + BI.i18nText("BI-Link");
                        linkages.push({
                            text: name,
                            title: name,
                            to: link.to,
                            from: link.from
                        });
                    }
                });
            }
        });
        if(linkages.length === 0) {
            return;
        }
        if(linkages.length === 1) {
            return this.fireEvent(BI.CombineChart.EVENT_ITEM_CLICK, {
                to: linkages[0].to,
                from: linkages[0].from,
                clicked: clicked
            });
        }
        var combo = BI.createWidget({
            type: "bi.combo",
            direction: "bottom",
            popup: {
                el: BI.createWidget({
                    type: "bi.vertical",
                    cls: "bi-linkage-list",
                    items: BI.createItems(linkages, {
                        type: "bi.text_button",
                        cls: "bi-linkage-list-item",
                        textAlign: "left",
                        height: 30,
                        handler: function () {
                            self.fireEvent(BI.CombineChart.EVENT_ITEM_CLICK, {
                                to: this.options.to,
                                from: this.options.from,
                                clicked: clicked
                            });
                            combo.destroy();
                        },
                        lgap: 10
                    }),
                    width: position.width
                })
            },
            width: 0
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: combo,
                top: position.y,
                left: position.x
            }]
        });
        combo.showView();
    },

    setTypes: function (types) {
        this.options.types = types || [[]];
    },

    populate: function (items, types) {
        var o = this.options;
        if (BI.isNotNull(types)) {
            this.setTypes(types);
        }
        var opts = this._formatItems(items);
        BI.extend(opts[1], {
            xAxis: o.xAxis,
            yAxis: o.yAxis
        });
        var result = o.formatConfig(opts[1], opts[0]);
        this.CombineChart.populate(result[0], result[1]);
    },

    resize: function () {
        this.CombineChart.resize();
    },

    magnify: function () {
        this.CombineChart.magnify();
    }
});
BI.CombineChart.EVENT_CHANGE = "EVENT_CHANGE";
BI.CombineChart.EVENT_ITEM_CLICK = "EVENT_ITEM_CLICK";
$.shortcut('bi.combine_chart', BI.CombineChart);