/**
 * @class BI.ChartTypeShow
 * @extend BI.Widget
 * 选择图表类型组
 */
BI.ChartTypeShow = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ChartTypeShow.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-type"
        })
    },

    _init: function () {
        BI.ChartTypeShow.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.buttonTree = BI.createWidget({
            type: "bi.button_tree",
            element: this.element,
            items: this._formatItems(BI.deepClone(BICst.DASHBOARD_WIDGETS[0])),
            layouts: [{
                type: "bi.horizontal",
                scrollx: false,
                scrollable: false,
                vgap: 3,
                hgap: 3
            }]
        });
        this.buttonTree.on(BI.ButtonTree.EVENT_CHANGE, function () {
            self.fireEvent(BI.ChartType.EVENT_CHANGE, arguments);
        })
    },

    //只能显示表格和其自身  交互说的神逻辑 0.0
    _formatItems: function (items) {
        var self = this, o = this.options;
        var result = [];
        var wId = o.wId;
        var wType = BI.Utils.getWidgetTypeByID(wId);
        BI.each(items, function (i, item) {
            if (BI.isNotEmptyArray(item.children)) {
                var foundType = false;
                BI.each(item.children, function (i, child) {
                    child.iconClass = child.cls;
                    child.iconWidth = 20;
                    child.iconHeight = 20;
                    if (child.value === wType) {
                        foundType = true;
                    }
                });
                if (foundType || item.value === BICst.WIDGET.TABLE) {
                    result.push(BI.extend({
                        type: "bi.icon_combo",
                        width: 40,
                        iconClass: item.cls,
                        items: item.children,
                        iconWidth: 24,
                        iconHeight: 24
                    }, item, {
                        cls: "chart-type-combo"
                    }));
                }
            } else {
                if (item.value === BICst.WIDGET.MAP) {
                    BI.each(BICst.SVG_MAP_TYPE, function (i, it) {
                        it.iconClass = it.cls;
                    });
                    if (item.value === wType) {
                        result.push(BI.extend({
                            type: "bi.map_type_combo_show",
                            width: 40,
                            items: BICst.SVG_MAP_TYPE,
                            iconWidth: 24,
                            iconHeight: 24
                        }, {
                            cls: "chart-type-combo"
                        }));
                    }
                } else {
                    if (item.value === wType) {
                        result.push(BI.extend({
                            type: "bi.icon_button",
                            width: 40,
                            iconWidth: 24,
                            iconHeight: 24
                        }, item, {
                            cls: item.cls + " chart-type-icon"
                        }));
                    }
                }
            }
        });
        return result;
    },

    getValue: function () {
        return this.buttonTree.getValue()[0];
    },

    setValue: function (v) {
        this.buttonTree.setValue(v);
    }
});
BI.ChartTypeShow.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_type_show", BI.ChartTypeShow);
